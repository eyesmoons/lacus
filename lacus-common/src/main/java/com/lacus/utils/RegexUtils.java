package com.lacus.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * This is Regex expression utils.
 */
public class RegexUtils {

    private static final Pattern LINUX_USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_].{0,30}");

    private RegexUtils() {
    }

    /**
     * check if the input is a valid linux username
     * @param str input
     * @return boolean
     */
    public static boolean isValidLinuxUserName(String str) {
        return LINUX_USERNAME_PATTERN.matcher(str).matches();
    }

    public static String escapeNRT(String str) {
        // Logging should not be vulnerable to injection attacks: Replace pattern-breaking characters
        if (!StringUtils.isEmpty(str)) {
            return str.replaceAll("[\n|\r|\t]", "_");
        }
        return null;
    }

}
