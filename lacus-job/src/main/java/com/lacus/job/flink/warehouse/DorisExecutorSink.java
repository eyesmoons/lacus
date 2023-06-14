package com.lacus.job.flink.warehouse;


import com.alibaba.fastjson2.JSONObject;
import com.lacus.job.constants.SinkResponse;
import com.lacus.job.exception.SinkException;
import com.lacus.job.utils.DruidJdbcUtils;
import com.lacus.job.utils.StringUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.lacus.job.constants.SinkResponse.DORIS_BACKEND_ALIVE_NOT_FOUND;

public class DorisExecutorSink extends RichSinkFunction<Map<String, String>> implements SinkExecutor {

    private static final Logger log = LoggerFactory.getLogger(DorisExecutorSink.class);

    private String feIp;
    private String dorisDb;
    private String userName;
    private String password;
    private Integer port;
    private final String FORMAT = "json";
    private final String MAX_FILTER_RATIO = "1.0";
    private final boolean STRIP_OUTER_ARRAY = true;
    private Map<String, Mapping> mappingMap = new HashMap<>(5);
    private String dorisConfig;

    private String STREAM_LOAD_URL = "http://%s/api/%s/%s/_stream_load?";


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
        String backend = dorisBEConfig();
        output.forEach((key, value) -> {
            Mapping mapping = this.mappingMap.get(key);
            if (mapping == null) {
                log.error("Source table:{} not found mapping in sink config", key);
                throw new SinkException(SinkResponse.MAPPING_CONF_NOT_FOUND);
            }


        });
    }


    private static class Mapping {
        private String sinkTable;
        private String columns;
        private String jsonpaths;

        public String getSinkTable() {
            return sinkTable;
        }

        public String getColumns() {
            return columns;
        }

        public String getJsonpaths() {
            return jsonpaths;
        }
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
        log.info("Doris backends config :{}", backendsList);
        return backendsList.get(0);
    }


    private void sink(String backend, String data) {

    }


}
