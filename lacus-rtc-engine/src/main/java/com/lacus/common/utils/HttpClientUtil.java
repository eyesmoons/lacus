package com.lacus.common.utils;

import com.google.common.base.Charsets;
import com.lacus.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
public class HttpClientUtil {

    /**
     * GET 请求
     *
     * @param path           请求路径
     * @param parametersBody 请求体
     */
    public static String getRequest(String path, List<NameValuePair> parametersBody) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(path);
        uriBuilder.setParameters(parametersBody);
        HttpGet get = new HttpGet(uriBuilder.build());
        HttpClient client = HttpClientBuilder.create().build();
        try {
            HttpResponse response = client.execute(get);
            int code = response.getStatusLine().getStatusCode();
            if (code != HttpStatus.SC_OK) {
                throw new CustomException("Could not access protected resource. Server returned http code: " + code);
            }
            return EntityUtils.toString(response.getEntity());
        } catch (ClientProtocolException e) {
            throw new CustomException("postRequest： Client protocol exception!", e);
        } catch (IOException e) {
            throw new CustomException("postRequest： IO error!", e);
        } finally {
            get.releaseConnection();
        }
    }

    /**
     * 发送POST请求（普通表单形式）
     *
     * @param path           请求路径
     * @param parametersBody 请求体
     */
    public static String postForm(String path, List<NameValuePair> parametersBody) {
        HttpEntity entity = new UrlEncodedFormEntity(parametersBody, Charsets.UTF_8);
        return postRequest(path, "application/x-www-form-urlencoded", entity);
    }

    /**
     * 发送POST请求（JSON形式）
     *
     * @param path 请求地址
     * @param json 请求数据
     */
    public static String postJSON(String path, String json) {
        StringEntity entity = new StringEntity(json, Charsets.UTF_8);
        return postRequest(path, "application/json", entity);
    }

    /**
     * 发送POST请求
     *
     * @param path      请求地址
     * @param mediaType 请求类型
     * @param entity    HTTP实体
     */
    public static String postRequest(String path, String mediaType, HttpEntity entity) {
        log.debug("[postRequest] resourceUrl: {}", path);
        HttpPost post = new HttpPost(path);
        post.addHeader("Content-Type", mediaType);
        post.addHeader("Accept", "application/json");
        post.setEntity(entity);
        return sendRequest(post);
    }

    /**
     * 发送POST请求
     *
     * @param path    请求路径
     * @param json    请求数据
     * @param headers 请求头
     */
    public static String putJson(String path, String json, List<Header> headers) {
        log.debug("[putRequest] resourceUrl: {}", path);
        StringEntity entity = new StringEntity(json, Charsets.UTF_8);
        HttpPut put = new HttpPut(path);
        put.setHeaders(headers.toArray(new Header[]{}));
        put.addHeader("Content-Type", "application/json");
        put.addHeader("Accept", "application/json");
        put.setEntity(entity);
        return sendRequest(put);
    }

    /**
     * 发送请求
     */
    private static String sendRequest(HttpRequestBase send) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(send);
            int code = response.getStatusLine().getStatusCode();
            if (code != HttpStatus.SC_OK) {
                throw new CustomException(EntityUtils.toString(response.getEntity()));
            }
            return EntityUtils.toString(response.getEntity());
        } catch (ClientProtocolException e) {
            throw new CustomException("Request：Client protocol exception!", e);
        } catch (IOException e) {
            throw new CustomException("Request：IO error!", e);
        } finally {
            send.releaseConnection();
        }
    }
}
