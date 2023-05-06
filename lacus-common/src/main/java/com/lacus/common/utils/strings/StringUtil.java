package com.lacus.common.utils.strings;

/**
 * Created by:
 *
 * @Author: lit
 * @Date: 2023/04/27/10:04
 * @Description:
 */
public class StringUtil {

    /**
     * 校验字符串是否为空串或者null
     *
     * @param str
     * @return
     */
    public static boolean checkValNull(String str) {
        return null == str || "".equals(str);
    }

}
