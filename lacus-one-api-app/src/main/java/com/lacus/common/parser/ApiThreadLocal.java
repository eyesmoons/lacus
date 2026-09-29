package com.lacus.common.parser;


import lombok.Data;

import java.util.Map;

@Data
public class ApiThreadLocal {

    private static final ThreadLocal<ApiThreadLocal> localThread = new ThreadLocal<>();

    private String apiUrl;

    private Map<String, Object> params;

    private String remoteIp;

    private String method;

    public ApiThreadLocal(String apiUrl, Map<String, Object> params, String remoteIp, String method) {
        this.apiUrl = apiUrl;
        this.params = params;
        this.remoteIp = remoteIp;
        this.method = method;
    }

    public void set() {
        localThread.set(this);
    }

    public static ApiThreadLocal get() {
        return localThread.get();
    }

}
