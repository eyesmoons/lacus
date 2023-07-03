package com.lacus.job.flink.warehouse;


import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.google.common.collect.Maps;
import com.lacus.job.constants.Constant;
import com.lacus.job.constants.SinkResponse;
import com.lacus.job.constants.SinkResponseEnums;
import com.lacus.job.exception.SinkException;
import com.lacus.job.utils.HttpClientUtils;
import com.lacus.job.utils.StringUtils;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class DorisExecutorSink extends RichSinkFunction<Map<String, String>> implements SinkExecutor {

    private static final Logger log = LoggerFactory.getLogger(DorisExecutorSink.class);

    private String feIp;
    private String dorisDb;
    private String userName;
    private String password;
    private Integer port;
    private final Map<String, Mapping> mappingMap = new HashMap<>();

    private String dorisConfig;

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
                this.mappingMap.put(key, mapping);
            }
        }
    }

    //{test.test_cdc_date=[{"id":6546456,"name":"333","age":3333,"birthdy":1685039150000,"_is_delete":0,"update_stamp":"2023-06-12 16:51:36"}]}
    @Override
    public void invoke(Map<String, String> output, Context context) throws Exception {
        //  String backend = dorisBEConfig();
        output.forEach((key, value) -> {
            Mapping mapping = this.mappingMap.get(key);
            if (mapping == null) {
                log.error("Source table:{} not found mapping in sink config", key);
                throw new SinkException(SinkResponseEnums.MAPPING_CONF_NOT_FOUND);
            }
            sink2(mapping, value);
        });
    }


/*    private String dorisBEConfig() {
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
        log.info("Doris backends config :{}", backendsList);
        return backendsList.get(0);
    }*/


    public void sink2(Mapping mapping, String data) {
        String ipPort = this.feIp + ":8030";
        String httpUrl = String.format(STREAM_LOAD_URL, ipPort, this.dorisDb, mapping.getSinkTable());
        String loadLabel = "lacus-" + mapping.getSinkTable() + "-" + new Date().getTime() + "-" + UUID.randomUUID().toString().replaceAll("-", "");
        List<Header> headers = buildLoadHeader(mapping);
        headers.add(new BasicHeader("label", loadLabel));
        System.out.println("---------------------------");
        System.out.println(httpUrl);
        String result = HttpClientUtils.put(httpUrl, data, headers);
        System.out.println(result);
        System.out.println("---------------------------");
    }


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


    private Map<String, String> buildLoadMap(Mapping mapping) {
        Map<String, String> confMap = Maps.newHashMap();

        confMap.put(Constant.SINK_DORIS_FORMAT, mapping.getFormat());
        confMap.put(Constant.SINK_DORIS_COLUMNS, mapping.getColumns());
        confMap.put(Constant.SINK_DORIS_JSON_PATHS, mapping.getJsonPaths());
        confMap.put(Constant.MAX_FILTER_RATIO, mapping.getMaxFilterRatio());
        confMap.put(Constant.STRIP_OUTER_ARRAY, mapping.getStripOuterArray());
        return confMap;
    }


    //sink data to doris
    private void sink(Mapping mapping, String data) throws IOException {
        String ipPort = this.feIp + ":8030" /*+ this.port*/;
        String httpUrl = String.format(STREAM_LOAD_URL, ipPort, this.dorisDb, mapping.getSinkTable());
        String loadLabel = "lacus-" + mapping.getSinkTable() + "-" + new Date().getTime() + "-" + UUID.randomUUID().toString().replaceAll("-", "");
        Map<String, String> confMap = buildLoadMap(mapping);
        HttpURLConnection httpConn = getHttpConn(httpUrl, confMap, loadLabel);
        try {
            BufferedOutputStream bs = new BufferedOutputStream(httpConn.getOutputStream());
            bs.write(data.getBytes());
            bs.close();
        } catch (IOException iox) {
            log.error("Doris steamLoad failed, streamLoad label:{}, error:{}", loadLabel, iox);
            throw new SinkException(SinkResponseEnums.DORIS_SINK_STREAM_LOAD_FAILED, iox);
        }

        int returnStatus = httpConn.getResponseCode();
        String returnMsg = httpConn.getResponseMessage();
        InputStream returnContent = (InputStream) httpConn.getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(returnContent));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();

        log.info("AuditLoader plugin load with label: {}, response code: {}, msg: {}, content: {}", loadLabel, returnStatus, returnMsg, response.toString());
        new SinkResponse(returnStatus, returnMsg, response.toString(), loadLabel);
        System.out.println(response.toString());
    }


    // obtain fe streamLoad http client
    private HttpURLConnection getHttpConn(String loadUrl, Map<String, String> confMap, String label) {
        try {
            URL url = new URL(loadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("PUT");
            String authEncoding = Base64.getEncoder().encodeToString(String.format("%s:%s", this.userName, this.password).getBytes(StandardCharsets.UTF_8));
            conn.setRequestProperty("Authorization", "Basic " + authEncoding);
            conn.addRequestProperty("Expect", "100-continue");
            conn.addRequestProperty("content-Type", "text/plain; charset=UTF-8");
            conn.addRequestProperty("label", label);
            conn.addRequestProperty("max_filter_ratio", "1.0");

            if (ObjectUtils.isNotEmpty(confMap)) {
                for (Map.Entry<String, String> entry : confMap.entrySet()) {
                    conn.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            return conn;
        } catch (IOException iox) {
            log.info("Doris streamLoad http connected failed: {}", iox.getMessage());
            throw new SinkException(SinkResponseEnums.DORIS_SINK_HTTP_CONNECTED_FAILED, iox);
        }


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
