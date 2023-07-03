package com.lacus.job.utils;

import com.lacus.job.exception.SinkException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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
        //  httpPut.setConfig(timeout());
        StringEntity entity = new StringEntity(data, "utf-8");
        httpPut.setEntity(entity);
        httpPut.setHeaders(headers.toArray(new Header[]{}));
        httpPut.addHeader("Content-Type", "application/json");
        httpPut.addHeader("Accept", "application/json");
        log.info("Request url: {}", requestUrl);
        return sendRequest(httpPut);
    }


/*    public static RequestConfig timeout() {
        return RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000).build();
    }*/

    public static String request(HttpRequestBase request) {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        try {
            httpclient = HttpClients.createDefault();
            response = httpclient.execute(request);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException iox) {
            throw new SinkException(iox.getMessage(), iox);
        } finally {
            close(response, httpclient);
            request.releaseConnection();
        }
    }

    private static String sendRequest(HttpRequestBase send) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(send);
            int code = response.getStatusLine().getStatusCode();
            if (code != HttpStatus.SC_OK) {
                throw new RuntimeException(EntityUtils.toString(response.getEntity()));
            }
            return EntityUtils.toString(response.getEntity());
        } catch (ClientProtocolException e) {
            throw new RuntimeException("Request -- Client protocol exception!", e);
        } catch (IOException e) {
            throw new RuntimeException("Request -- IO error!", e);
        } finally {
            send.releaseConnection();
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






