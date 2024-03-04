package com.lacus.common.constant;

import java.util.Arrays;
import java.util.List;

/**
 * @created by shengyu on 2023/9/7 10:58
 */
public class Constants {
    public static final String DELETE_KEY = "_is_deleted";
    public static final String UPDATE_STAMP_KEY = "_update_stamp";

    public static final String STREAM_LOAD_COLUMNS = "columns";
    public static final String STREAM_LOAD_JSONPATH = "jsonpaths";
    public static final String STREAM_LOAD_FORMAT = "format";
    public static final String STREAM_LOAD_MAX_FILTER_RATIO = "max_filter_ratio";
    public static final String STREAM_LOAD_STRIP_OUTER_ARRAY = "strip_outer_array";

    public static final String ERROR_TOPIC = "RTC_ERROR_TOPIC";
    public final static Integer SUCCESS_CODE = 200;

    public static final List<String> OPERATION_TYPES = Arrays.asList("READ", "CREATE", "UPDATE", "DELETE");
    public final static String OPERATION_DELETE = "DELETE";
    public final static Integer DELETE_VALUE_NORMAL = 0;
    public final static Integer DELETE_VALUE_DELETED = 1;
}