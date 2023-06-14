package com.lacus.test.sink.fink;


import com.lacus.job.flink.impl.SinkFlinkJob;

public class FinkSinkTest {


    static String paramJson = "{\n" +
            "    \"flinkConf\": {\n" +
            "        \"jobName\": \"lacus测试数据接入\",\n" +
            "        \"maxBatchInterval\": 30,\n" +
            "        \"maxBatchRows\": 2,\n" +
            "        \"maxBatchSize\": 20971520,\n" +
            "        \"parallelism\": 1,\n" +
            "        \"slotsPerTaskManager\": 1\n" +
            "    },\n" +
            "    \"sink\": {\n" +
            "        \"sinkType\": \"doris\",\n" +
            "        \"engine\": {\n" +
            "            \"dorisDb\": \"test\",\n" +
            "            \"dorisMap\": {\n" +
            "                \"lacus_topic\": {\n" +
            "                    \"columns\": \"`id`,`name`,`age`,`birthdy`,`_is_delete`,`update_stamp`\",\n" +
            "                    \"doris_table\": \"test_tbl\",\n" +
            "                    \"format\": \"json\",\n" +
            "                    \"jsonpaths\": \"[\\\"$.id\\\",\\\"$.name\\\",\\\"$.age\\\",\\\"$.birthdy\\\",\\\"$._is_delete\\\",\\\"$.update_stamp\\\"]\",\n" +
            "                    \"max_filter_ratio\": \"1.0\",\n" +
            "                    \"strip_outer_array\": \"true\"\n" +
            "                }\n" +
            "            },\n" +
            "            \"dorisPwd\": \"\",\n" +
            "            \"dorisUrl\": \"10.220.146.10:9030\",\n" +
            "            \"dorisUser\": \"root\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"source\": {\n" +
            "        \"bootStrapServer\": \"172.22.224.111:9092,172.22.224.112:9092,172.22.224.113:9092,172.22.224.115:9092,172.22.224.116:9092\",\n" +
            "        \"groupId\": \"g1\",\n" +
            "        \"topics\": [\n" +
            "            \"lacus_topic\"\n" +
            "        ]\n" +
            "    }\n" +
            "}";

    public static void main(String[] args) {

        String[] params = new String[1];

        params[0] = paramJson;


        SinkFlinkJob sinkFlinkJob = new SinkFlinkJob(params);
        sinkFlinkJob.run();

    }


}
