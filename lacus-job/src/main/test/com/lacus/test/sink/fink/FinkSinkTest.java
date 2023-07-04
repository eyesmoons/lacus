package com.lacus.test.sink.fink;


import com.lacus.job.flink.impl.SinkFlinkJob;

public class FinkSinkTest {


    static String paramJson = "[\n" +
            "    {\n" +
            "        \"flinkConf\": {\n" +
            "            \"jobName\": \"测试任务4\",\n" +
            "            \"maxBatchInterval\": 5,\n" +
            "            \"maxBatchRows\": 20000,\n" +
            "            \"maxBatchSize\": 10485760\n" +
            "        },\n" +
            "        \"sink\": {\n" +
            "            \"engine\": {\n" +
            "                \"columnMap\": {\n" +
            "                    \"lacus.data_sync_job_catalog\": {\n" +
            "                        \"sinkTable\": \"demo2\",\n" +
            "                        \"format\": \"json\",\n" +
            "                        \"max_filter_ratio\": \"1.0\",\n" +
            "                        \"strip_outer_array\": true,\n" +
            "                        \"columns\": \"`material`,`start_voucher`\",\n" +
            "                        \"jsonpaths\": \"[\\\"$.catalog_id\\\",\\\"$.catalog_name\\\"]\"\n" +
            "                    },\n" +
            "                    \"lacus.data_sync_job\": {\n" +
            "                        \"sinkTable\": \"demo\",\n" +
            "                        \"format\": \"json\",\n" +
            "                        \"max_filter_ratio\": \"1.0\",\n" +
            "                        \"strip_outer_array\": true,\n" +
            "                        \"columns\": \"`description`,`type`\",\n" +
            "                        \"jsonpaths\": \"[\\\"$.app_container\\\",\\\"$.catalog_id\\\"]\"\n" +
            "                    }\n" +
            "                },\n" +
            "                \"dbName\": \"demo\",\n" +
            "                \"ip\": \"10.220.146.10\",\n" +
            "                \"password\": \"\",\n" +
            "                \"port\": 9030,\n" +
            "                \"userName\": \"root\"\n" +
            "            },\n" +
            "            \"sinkType\": \"DORIS\"\n" +
            "        },\n" +
            "        \"source\": {\n" +
            "            \"bootstrapServers\": \"gaia-dev-bigdata5:9092,gaia-dev-bigdata6:9092,gaia-dev-bigdata7:9092\",\n" +
            "            \"groupId\": \"data_sync_group_1674651282279800834\",\n" +
            "            \"topics\": [\n" +
            "                \"data_sync_topic_1674651282279800834\"\n" +
            "            ]\n" +
            "        }\n" +
            "    }\n" +
            "]";

    public static void main(String[] args) {

        String[] params = new String[2];

        params[0]  = "lacus";
        params[1] = paramJson;





        SinkFlinkJob sinkFlinkJob = new SinkFlinkJob(params);
        sinkFlinkJob.run();

    }


}
