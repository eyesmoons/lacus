package com.lacus.job.utils;


public class StringUtils {


    public static boolean checkValNull(Object obj) {
        return obj == null || obj == "";
    }

    public static boolean checkValNotNull(Object obj) {
        return !checkValNull(obj);
    }

}
