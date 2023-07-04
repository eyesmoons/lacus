package com.lacus.job.utils;

import com.lacus.job.constants.SinkResponseEnums;
import com.lacus.job.exception.SinkException;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
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
        StringEntity entity = new StringEntity(data, "utf-8");
        httpPut.setEntity(entity);
        httpPut.setHeaders(headers.toArray(new Header[]{}));
        httpPut.addHeader("Content-Type", "application/json");
        httpPut.addHeader("Accept", "application/json");
        log.info("Request url: {}", requestUrl);
        return sendRequest(httpPut);
    }

    public static String sendRequest(HttpRequestBase request) {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        try {
            httpclient = HttpClients.createDefault();
            response = httpclient.execute(request);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException iox) {
            log.info("Doris streamLoad http connected failed: {}", iox.getMessage());
            throw new SinkException(SinkResponseEnums.DORIS_SINK_HTTP_CONNECTED_FAILED, iox);
        } finally {
            close(response, httpclient);
            request.releaseConnection();
        }
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






