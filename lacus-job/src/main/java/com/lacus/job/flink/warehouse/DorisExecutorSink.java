package com.lacus.job.flink.warehouse;


import com.alibaba.fastjson2.JSONObject;
import com.lacus.job.utils.StringUtils;
import org.apache.doris.flink.cfg.DorisExecutionOptions;
import org.apache.doris.flink.cfg.DorisOptions;
import org.apache.doris.flink.cfg.DorisReadOptions;
import org.apache.doris.flink.sink.DorisSink;
import org.apache.doris.flink.sink.writer.SimpleStringSerializer;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class DorisExecutorSink extends AbstractSink<DorisSink<Map<String, List<String>>>, Map<String, List<String>>> {

    private String feIp;

    private String dorisDb;

    private String dorisTable;

    private String userName;

    private String password;


    private String dorisConfig;


    public DorisExecutorSink(String dorisConfig) {
        this.dorisConfig = dorisConfig;
        init();
    }


    @Override
    protected void init() {
        if (StringUtils.checkValNotNull(dorisConfig)) {
            JSONObject config = JSONObject.parseObject(dorisConfig);
            this.feIp = config.getString("doris.feIp");
            this.dorisDb = config.getString("doris.db");
            this.dorisTable = config.getString("doris.table");
            this.userName = config.getString("doris.username");
            this.password = config.getString("doris.password");
        }
    }

    @Override
    public DorisSink<Map<String, List<String>>> sink(Map<String, List<String>> stream) {
        DorisSink.Builder<Map<String, List<String>>> builder = DorisSink.builder();
        DorisOptions.Builder dorisBuilder = DorisOptions.builder();
        String tableIdentifier = this.dorisDb + "." + this.dorisTable;
        dorisBuilder.setFenodes(feIp)
                .setTableIdentifier(tableIdentifier)
                .setUsername(userName)
                .setPassword(password);

        Properties properties = new Properties();
        properties.setProperty("format", "json");
        properties.setProperty("read_json_by_line", "true");


        DorisExecutionOptions.Builder executionBuilder = DorisExecutionOptions.builder();
        executionBuilder.setLabelPrefix(UUID.randomUUID().toString()).setStreamLoadProp(properties);

        DorisSink<Map<String, List<String>>> build = builder.build();

        builder.setDorisReadOptions(DorisReadOptions.builder().build())
                .setDorisExecutionOptions(executionBuilder.build())
                .setDorisOptions(dorisBuilder.build());
        return builder.build();
    }
}
