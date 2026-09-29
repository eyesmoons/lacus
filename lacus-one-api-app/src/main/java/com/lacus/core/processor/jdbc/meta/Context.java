package com.lacus.core.processor.jdbc.meta;

import com.google.common.collect.Lists;
import com.lacus.service.vo.RequestParamsVO;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

import java.util.*;

public class Context {


    static {
        OgnlRuntime.setPropertyAccessor(HashMap.class, new ContextAccessor());
    }

    public static final String BINDING_DATA = "_data";

    private Configuration cfg;

    private Map<String, Object> binding;

    private StringBuilder sql = new StringBuilder();

    private Map<String, String> requestKeys;

    private List<RequestParamsVO> parameter;

    private int uniqueNumber = 0;

    public Context(Configuration cfg, Object data) {
        this.cfg = cfg;
        binding = new HashMap<>();
        parameter = Lists.newArrayList();
        binding.put(BINDING_DATA, data);
    }

    public Context(Configuration cfg, Object data, Map<String, String> requestKeys) {
        this.cfg = cfg;
        binding = new HashMap<>();
        parameter = Lists.newArrayList();
        binding.put(BINDING_DATA, data);
        this.requestKeys = requestKeys;
    }


    public void bind(String key, Object value) {
        binding.put(key, value);
    }

    public void appendSql(String sqlFragement) {
        sql.append(sqlFragement).append(" ");
    }

    public Map<String, Object> getBinding() {
        return this.binding;
    }

    public List<RequestParamsVO> getParameter() {
        return this.parameter;
    }


    public void addParameter(RequestParamsVO param) {
        this.parameter.add(param);
    }

    public String getSql() {
        return sql.toString();
    }

    public void setSql(String sql) {
        this.sql = new StringBuilder(sql);
    }

    public int getUniqueNumber() {
        return ++uniqueNumber;
    }

    public Configuration getConfiguration() {
        return this.cfg;
    }


    public Map<String, String> getRequestKeys() {
        return requestKeys;
    }

    static class ContextAccessor implements PropertyAccessor {


        @Override
        public Object getProperty(OgnlContext ognlContext, Object target, Object name) throws OgnlException {
            Map map = (Map) target;
            Object result = map.get(name);
            if (result != null) {
                return result;
            }

            Object parameterObject = map.get(BINDING_DATA);
            if (parameterObject instanceof Map) {
                return ((Map) parameterObject).get(name);
            }
            return null;
        }

        @Override
        public void setProperty(OgnlContext ognlContext, Object target, Object name, Object value) throws OgnlException {
            Map map = (Map) target;
            map.put(name, value);
        }

        public String getSourceAccessor(OgnlContext arg0, Object arg1,
                                        Object arg2) {
            // TODO Auto-generated method stub
            return null;
        }

        public String getSourceSetter(OgnlContext arg0, Object arg1, Object arg2) {
            // TODO Auto-generated method stub
            return null;
        }

    }

}
