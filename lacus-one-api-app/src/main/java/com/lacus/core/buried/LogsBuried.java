package com.lacus.core.buried;

import com.lacus.common.utils.DateUtils;

import java.util.Date;

public class LogsBuried {
    private static final String INFO = "<span style=\"color: #92D581;\">INFO</span>";
    private static final String ERROR = "<span style=\"color: #f05656;\">ERROR</span>";
    private static final String WARN = "<span style=\"color: #ffa033;\">WARN</span>";

    public static String setInfo(String content) {
        return concatString(INFO, content);
    }

    public static String setError(String content) {
        return concatString(ERROR, content);
    }

    public static String setWarn(String content) {
        return concatString(WARN, content);
    }

    private static String concatString(String type, String content) {
        String time = DateUtils.format(new Date(), DateUtils.STR_MS_PATTERN);
        String threadName = Thread.currentThread().getName();
        return String.format("[%s] [%s] [%s] %s", type, time, threadName, content);
    }

}
