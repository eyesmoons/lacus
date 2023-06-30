package com.lacus.job.utils;


import com.alibaba.fastjson2.JSON;
import com.lacus.job.exception.SinkException;
import com.lacus.job.flink.warehouse.DorisExecutorSink;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class HttpClientUtils {

    private static final Logger log = LoggerFactory.getLogger(HttpClientUtils.class);


    public static String put(String requestUrl, String data, List<Header> headers) {
        HttpPut httpPut = new HttpPut(requestUrl);
        httpPut.setConfig(timeout());

        httpPut.setEntity(new StringEntity(data, "utf-8"));
        httpPut.setHeaders(headers.toArray(new Header[]{}));

        httpPut.addHeader("Content-Type", "application/json");
        httpPut.addHeader("Accept", "application/json");
        try {
            return request(httpPut);
        } catch (IOException e) {
            log.error("Request url: {}, error: {}", requestUrl, e.getMessage());
            throw new SinkException(e.getMessage(), e);
        }
    }


    public static RequestConfig timeout() {
        return RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000).build();
    }

    public static String request(HttpUriRequest request) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(request);
        String result = EntityUtils.toString(response.getEntity());
        close(response, httpclient);
        return result;
    }


    public static void close(CloseableHttpResponse response, CloseableHttpClient httpClient) {
        try {
            if (response != null) {
                response.close();
            }
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (IOException e) {
            log.error("Close http connection failed", e);
            throw new SinkException("Close http connection failed", e);
        }
    }


}






