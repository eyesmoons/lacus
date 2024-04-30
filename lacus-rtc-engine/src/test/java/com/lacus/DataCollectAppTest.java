package com.lacus;

import com.lacus.app.DataCollectApp;

/**
 * @author shengyu
 * @date 2024/4/17 20:04
 */
public class DataCollectAppTest {
    public static void main(String[] args) throws Exception {
        args = new String[4];
        args[0] = "mysql"; // reader type
        args[1] = "doris"; // writer type
        args[2] = "demo"; // job name
        args[3] = "{\n" +
                "    \"flinkConf\": {\n" +
                "        \"maxBatchInterval\": 5,\n" +
                "        \"maxBatchRows\": 20000,\n" +
                "        \"maxBatchSize\": 10485760\n" +
                "    },\n" +
                "    \"jobInfo\": {\n" +
                "        \"jobId\": 1,\n" +
                "        \"jobName\": \"demo\"\n" +
                "    },\n" +
                "    \"sink\": {\n" +
                "        \"sinkDataSource\": {\n" +
                "            \"dataSourceName\": \"test_doris\",\n" +
                "            \"dbName\": \"demo\",\n" +
                "            \"ip\": \"hadoop1\",\n" +
                "            \"password\": \"xxx\",\n" +
                "            \"port\": 9030,\n" +
                "            \"userName\": \"root\"\n" +
                "        },\n" +
                "        \"streamLoadPropertyMap\": {\n" +
                "            \"dolphinscheduler.dolphinscheduler.t_ds_project\": {\n" +
                "                \"columns\": \"`id`,`create_time`\",\n" +
                "                \"format\": \"json\",\n" +
                "                \"jsonpaths\": \"[\\\"$.code\\\",\\\"$.create_time\\\"]\",\n" +
                "                \"maxFilterRatio\": \"1.0\",\n" +
                "                \"sinkTable\": \"ods_dolphinscheduler_t_ds_user_delta\",\n" +
                "                \"stripOuterArray\": \"true\"\n" +
                "            }\n" +
                "        }\n" +
                "    },\n" +
                "    \"source\": {\n" +
                "        \"bootStrapServers\": \"hadoop1:9092,hadoop2:9092,hadoop3:9092\",\n" +
                "        \"databaseList\": [\n" +
                "            \"dolphinscheduler\"\n" +
                "        ],\n" +
                "        \"groupId\": \"rtc_group_1\",\n" +
                "        \"hostname\": \"hadoop1\",\n" +
                "        \"password\": \"xxx\",\n" +
                "        \"port\": \"3306\",\n" +
                "        \"sourceName\": \"test_mysql\",\n" +
                "        \"syncType\": \"initial\",\n" +
                "        \"tableList\": [\n" +
                "            \"dolphinscheduler.dolphinscheduler.t_ds_project\"\n" +
                "        ],\n" +
                "        \"topics\": [\n" +
                "            \"rtc_topic_1\"\n" +
                "        ],\n" +
                "        \"username\": \"lacus\"\n" +
                "    }\n" +
                "}";
        DataCollectApp.main(args);
    }
}