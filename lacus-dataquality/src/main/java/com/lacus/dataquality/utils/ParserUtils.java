package com.lacus.dataquality.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 密码编解码工具（URL 编码）
 */
public class ParserUtils {

    private static final String UTF_8 = "UTF-8";

    private ParserUtils() {
    }

    /**
     * URL 编码（密码存储时使用）
     */
    public static String encode(String value) {
        if (value == null) {
            return null;
        }
        try {
            return URLEncoder.encode(value, UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to encode value", e);
        }
    }

    /**
     * URL 解码（读取密码时使用）
     */
    public static String decode(String value) {
        if (value == null) {
            return null;
        }
        try {
            return URLDecoder.decode(value, UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to decode value", e);
        }
    }
}
