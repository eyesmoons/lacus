package com.lacus.common.enums;


public enum HttpRequestEnum {

    GET("get", "get请求"),
    POST("post", "post请求");

    private String name;

    private String desc;

    HttpRequestEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public static HttpRequestEnum getMethodEnum(String method) {
        for (HttpRequestEnum sinkEnums : HttpRequestEnum.values()) {
            if (sinkEnums.getName().equals(method)) {
                return sinkEnums;
            }
        }
        return null;
    }


}
