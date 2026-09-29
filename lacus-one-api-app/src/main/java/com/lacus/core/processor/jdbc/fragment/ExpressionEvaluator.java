package com.lacus.core.processor.jdbc.fragment;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.lacus.common.exception.ApiException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpressionEvaluator {

    public boolean evaluateBoolean(String expression, Object parameterObject) {
        Object value = OgnlCache.getValue(expression, parameterObject);
        if (value instanceof Boolean)
            return (Boolean) value;
        if (value instanceof Number)
            return !new BigDecimal(String.valueOf(value))
                    .equals(BigDecimal.ZERO);
        return value != null;
    }

    public Iterable<?> evaluateIterable(String expression,
                                        Object parameterObject) {
        Object value = OgnlCache.getValue(expression, parameterObject);
        if (value == null || StringUtils.isBlank(value.toString())) {
            return null;
        }
        if (value instanceof Iterable)
            return (Iterable<?>) value;
        if (value.getClass().isArray()) {
            int size = Array.getLength(value);
            List<Object> answer = new ArrayList<Object>();
            for (int i = 0; i < size; i++) {
                Object o = Array.get(value, i);
                answer.add(o);
            }
            return answer;
        }
        if (JSON.isValidArray(value.toString())) {
            return JSONArray.parseArray(value.toString());
        }

        if (value instanceof Map) {
            return ((Map) value).entrySet();
        }
        throw new ApiException("参数匹配错误, '" + expression
                + "'.  参数值:(" + value + ") 不是数组");
    }

}
