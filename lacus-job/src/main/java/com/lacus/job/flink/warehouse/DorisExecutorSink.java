package com.lacus.job.flink.warehouse;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.lacus.job.constants.Constant;
import com.lacus.job.constants.SinkResponseEnums;
import com.lacus.job.exception.SinkException;
import com.lacus.job.utils.DruidJdbcUtils;
import com.lacus.job.utils.HttpClientUtils;
import com.lacus.job.utils.StringUtils;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.util.CollectionUtil;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.lacus.job.constants.SinkResponseEnums.DORIS_BACKEND_ALIVE_NOT_FOUND;

public class DorisExecutorSink extends RichSinkFunction<Map<String, String>> implements SinkExecutor {

    private static final Logger log = LoggerFactory.getLogger(DorisExecutorSink.class);

    private String feIp;
    private String dorisDb;
    private String userName;
    private String password;
    private Integer port;
    private final Map<String, Mapping> mappingMap = new HashMap<>();

    private String dorisConfig;

    private String BE_IP_PORT = null;

    private String STREAM_LOAD_URL = "http://%s/api/%s/%s/_stream_load";


    public DorisExecutorSink(String dorisConfig) {
        this.dorisConfig = dorisConfig;
        init();
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }


    @Override
    public void init() {
        if (StringUtils.checkValNotNull(dorisConfig)) {
            JSONObject config = JSONObject.parseObject(dorisConfig);
            this.feIp = config.getString("ip");
            this.port = config.getInteger("port");
            this.dorisDb = config.getString("dbName");
            this.userName = config.getString("userName");
            this.password = config.getString("password");
            JSONObject columnMaps = config.getJSONObject("columnMap");
            Set<String> keySet = columnMaps.keySet();
            for (String key : keySet) {
                Mapping mapping = JSONObject.parseObject(columnMaps.getString(key), Mapping.class);
                buildJsonPath(mapping);
                this.mappingMap.put(key, mapping);
            }
            if (Objects.isNull(BE_IP_PORT)) {
                BE_IP_PORT = dorisBEConfig();
            }
        }
    }

    @Override
    public void invoke(Map<String, String> output, Context context) throws Exception {
        output.forEach((key, value) -> {
            Mapping mapping = this.mappingMap.get(key);
            if (mapping == null) {
                log.error("Source table:{} not found mapping in sink config", key);
                throw new SinkException(SinkResponseEnums.MAPPING_CONF_NOT_FOUND);
            }
            sinkToDoris(mapping, value);
        });
    }


    private String dorisBEConfig() {
        List<String> backendsList = Lists.newArrayList();
        DruidJdbcUtils druidJdbcUtils = new DruidJdbcUtils(this.feIp, this.port, this.dorisDb, this.userName, this.password);
        List<Map<String, Object>> backends = druidJdbcUtils.execQuery("SHOW backends;");
        for (Map<String, Object> backend : backends) {
            boolean alive = Boolean.parseBoolean(backend.get("Alive").toString());
            if (alive) {
                String ip = backend.get("IP").toString();
                String httpPort = backend.get("HttpPort").toString();
                backendsList.add(ip + ":" + httpPort);
            }
        }
        if (CollectionUtil.isNullOrEmpty(backendsList)) {
            throw new SinkException(DORIS_BACKEND_ALIVE_NOT_FOUND);
        }
        Collections.shuffle(backendsList);
        log.info("Get doris backends config :{}", backendsList);
        return backendsList.get(0);
    }


    private void buildJsonPath(Mapping mapping) {
        List<String> mappingColumns = Arrays.stream(mapping.getColumns().split(",")).map(column -> column.replaceAll("`", "")).collect(Collectors.toList());
        mappingColumns.add(Constant.IS_DELETE_FILED);
        mappingColumns.add(Constant.UPDATE_STAMP_FILED);
        List<String> collect = mappingColumns.stream().map(column -> String.format("`%s`", column)).collect(Collectors.toList());
        String targetColumn = String.join(",", collect);
        JSONArray jsonPaths = JSONArray.parse(mapping.getJsonPaths());
        jsonPaths.add("$._is_delete");
        jsonPaths.add("$.update_stamp");
        mapping.setJsonPaths(jsonPaths.toString());
        mapping.setColumns(targetColumn);
    }


    //sink data to doris
    private void sinkToDoris(Mapping mapping, String data) {
        String httpUrl = String.format(STREAM_LOAD_URL, BE_IP_PORT, this.dorisDb, mapping.getSinkTable());
        String loadLabel = "lacus-" + mapping.getSinkTable() + "-" + new Date().getTime() + "-" + UUID.randomUUID().toString().replaceAll("-", "");
        List<Header> headers = buildLoadHeader(mapping);
        headers.add(new BasicHeader("label", loadLabel));
        String response = HttpClientUtils.put(httpUrl, data, headers);
        log.info("result：{}", response);
    }

    //build doris streamLoad http header
    private List<Header> buildLoadHeader(Mapping mapping) {
        List<Header> headers = Lists.newArrayList();
        String authEncoding = Base64.getEncoder().encodeToString(String.format("%s:%s", this.userName, this.password).getBytes(StandardCharsets.UTF_8));
        headers.add(new BasicHeader("Authorization", "Basic " + authEncoding));
        headers.add(new BasicHeader("Expect", "100-continue"));
        headers.add(new BasicHeader("strict_mode", "true"));
        headers.add(new BasicHeader(Constant.SINK_DORIS_FORMAT, mapping.getFormat()));
        headers.add(new BasicHeader(Constant.SINK_DORIS_COLUMNS, mapping.getColumns()));
        headers.add(new BasicHeader(Constant.SINK_DORIS_JSON_PATHS, mapping.getJsonPaths()));
        headers.add(new BasicHeader(Constant.MAX_FILTER_RATIO, mapping.getMaxFilterRatio()));
        headers.add(new BasicHeader(Constant.STRIP_OUTER_ARRAY, mapping.getStripOuterArray()));
        return headers;
    }


    @Data
    private static class Mapping implements Serializable {
        private String sinkTable;
        private String format;
        @JSONField(name = "max_filter_ratio")
        private String maxFilterRatio;
        @JSONField(name = "strip_outer_array")
        private String stripOuterArray;
        private String columns;
        private String jsonPaths;

        public String getSinkTable() {
            return sinkTable;
        }

        public String getColumns() {
            return columns;
        }

        public String getJsonPaths() {
            return jsonPaths;
        }

        public String getFormat() {
            return format;
        }

        public String getMaxFilterRatio() {
            return maxFilterRatio;
        }

        public String getStripOuterArray() {
            return stripOuterArray;
        }
    }


}
