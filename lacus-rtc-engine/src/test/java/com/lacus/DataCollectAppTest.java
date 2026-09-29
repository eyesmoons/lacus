package com.lacus;

import com.lacus.app.DataCollectApp;

/**
 * @author shengyu
 * @date 2024/4/17 20:04
 */
public class DataCollectAppTest {
    public static void main(String[] args) throws Exception {
        args = new String[4];
        args[0] = "MYSQL"; // source type
        args[1] = "MYSQL"; // sink type
        args[2] = "demo"; // job name
        args[3] = "{\"flinkConf\":{\"maxBatchInterval\":5,\"maxBatchRows\":20000,\"maxBatchSize\":10485760},\"jobInfo\":{\"jobId\":2,\"jobName\":\"mysql_to_mysql_test\"},\"sink\":{\"sinkDataSource\":{\"dataSourceName\":\"local_mysql_out\",\"dataSourceType\":\"MYSQL\",\"dbName\":\"demo\",\"ip\":\"172.16.143.1\",\"password\":\"123456\",\"port\":3306,\"userName\":\"shengyu\"},\"streamLoadPropertyMap\":{\"demo.t_city\":{\"columns\":\"`cid`,`cname`\",\"format\":\"json\",\"jsonpaths\":\"[\\\"$.cid\\\",\\\"$.cname\\\"]\",\"maxFilterRatio\":\"1.0\",\"sinkTable\":\"t_city2\",\"stripOuterArray\":\"true\"}}},\"source\":{\"bootStrapServers\":\"hadoop1:9092,hadoop2:9092,hadoop3:9092\",\"databaseList\":[\"demo\"],\"datasourceType\":\"MYSQL\",\"groupId\":\"rtc_group_2\",\"hostname\":\"172.16.143.1\",\"password\":\"123456\",\"port\":\"3306\",\"sourceName\":\"local_mysql\",\"syncType\":\"initial\",\"tableList\":[\"demo.t_city\"],\"topics\":[\"rtc_topic_2\"],\"username\":\"shengyu\"}}";
        DataCollectApp.main(args);
    }
}
