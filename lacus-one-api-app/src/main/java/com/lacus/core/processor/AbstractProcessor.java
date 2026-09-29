package com.lacus.core.processor;

import com.lacus.core.processor.jdbc.meta.SQLMeta;

import java.util.Map;

public abstract class AbstractProcessor {

    public abstract SQLMeta process(String template, Map<String, String> requestKeys, Map<String, Object> params);

}
