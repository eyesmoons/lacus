package com.lacus.test.sink.fink;


import com.lacus.job.flink.impl.DataSyncJob;

public class DataSyncJobTest {
    static String paramJson = "{\n" +
            "    \"flinkConf\": {\n" +
            "        \"jobName\": \"demo\",\n" +
            "        \"maxBatchInterval\": 5,\n" +
            "        \"maxBatchRows\": 20000,\n" +
            "        \"maxBatchSize\": 10485760\n" +
            "    },\n" +
            "    \"sink\": {\n" +
            "        \"engine\": {\n" +
            "            \"columnMap\": {\n" +
            "                \"dolphinscheduler.t_ds_user\": {\n" +
            "                    \"sinkTable\": \"ods_dolphinscheduler_t_ds_user_delta\",\n" +
            "                    \"format\": \"json\",\n" +
            "                    \"max_filter_ratio\": \"1.0\",\n" +
            "                    \"strip_outer_array\": true,\n" +
            "                    \"columns\": \"`create_time`,`email`,`id`,`phone`,`queue`,`state`,`tenant_id`,`time_zone`,`update_time`,`user_name`,`user_password`,`user_type`\",\n" +
            "                    \"jsonpaths\": \"[\\\"$.create_time\\\",\\\"$.email\\\",\\\"$.id\\\",\\\"$.phone\\\",\\\"$.queue\\\",\\\"$.state\\\",\\\"$.tenant_id\\\",\\\"$.time_zone\\\",\\\"$.update_time\\\",\\\"$.user_name\\\",\\\"$.user_password\\\",\\\"$.user_type\\\"]\"\n" +
            "                }\n" +
            "            },\n" +
            "            \"dbName\": \"demo\",\n" +
            "            \"ip\": \"124.70.98.238\",\n" +
            "            \"password\": \"123456\",\n" +
            "            \"port\": 9030,\n" +
            "            \"userName\": \"root\"\n" +
            "        },\n" +
            "        \"sinkType\": \"DORIS\"\n" +
            "    },\n" +
            "    \"source\": {\n" +
            "        \"bootStrapServers\": \"hadoop1:9092,hadoop2:9092,hadoop3:9092\",\n" +
            "        \"databaseList\": [\n" +
            "            \"dolphinscheduler\"\n" +
            "        ],\n" +
            "        \"groupId\": \"data_sync_group_1\",\n" +
            "        \"hostname\": \"127.0.0.1\",\n" +
            "        \"password\": \"123456\",\n" +
            "        \"port\": 3306,\n" +
            "        \"syncType\": \"initial\",\n" +
            "        \"tableList\": [\n" +
            "            \"dolphinscheduler.t_ds_user\"\n" +
            "        ],\n" +
            "        \"topic\": \"data_sync_topic_1\",\n" +
            "        \"username\": \"shengyu\"\n" +
            "    }\n" +
            "}";

    public static void main(String[] args) {
        String[] params = new String[2];
        params[0]  = "lacus";
        params[1] = paramJson;
        new DataSyncJob(params).run();
    }
}
