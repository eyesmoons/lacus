package com.lacus.datasource.model;

import lombok.Data;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

/**
 * 数据源连接参数
 */
@Data
public class ConnectionParam {
    
    /**
     * 参数定义列表
     */
    private List<ParamDefinition> paramDefinitions;
    
    /**
     * 参数值映射
     */
    private Map<String, Object> paramValues = new HashMap<>();
    
    /**
     * 获取参数值
     */
    public <T> T getValue(String paramName) {
        return (T) paramValues.get(paramName);
    }
    
    /**
     * 设置参数值
     */
    public void setValue(String paramName, Object value) {
        paramValues.put(paramName, value);
    }

    // 向后兼容的getter和setter方法
    public String getHost() {
        return getValue("host");
    }
    
    public void setHost(String host) {
        setValue("host", host);
    }
    
    public Integer getPort() {
        return getValue("port");
    }
    
    public void setPort(Integer port) {
        setValue("port", port);
    }
    
    public String getDatabase() {
        return getValue("database");
    }
    
    public void setDatabase(String database) {
        setValue("database", database);
    }
    
    public String getUsername() {
        return getValue("username");
    }
    
    public void setUsername(String username) {
        setValue("username", username);
    }
    
    public String getPassword() {
        return getValue("password");
    }
    
    public void setPassword(String password) {
        setValue("password", password);
    }
    
    public String getParams() {
        return getValue("params");
    }
    
    public void setParams(String params) {
        setValue("params", params);
    }
} 