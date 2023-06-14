package com.lacus.test.sink.fink;


import com.lacus.job.flink.impl.SinkFlinkJob;

public class FinkSinkTest {


    static String paramJson = "";

    public static void main(String[] args) {

        String[] params = new String[1];

        params[0] = paramJson;


        SinkFlinkJob sinkFlinkJob = new SinkFlinkJob(params);
        sinkFlinkJob.run();

    }


}
