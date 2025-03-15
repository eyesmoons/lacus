package com.lacus.core.processor.jdbc.token;

public class GenericTokenParser {

    private final String openToken;
    private final String closeToken;
    private final TokenHandler handler;

    public GenericTokenParser(String openToken, String closeToken,
                              TokenHandler handler) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.handler = handler;
    }

    public String parse(String text) {
        StringBuilder builder = new StringBuilder();
        if (text != null && !text.isEmpty()) {
            int offset = 0;
            String clearText = clear(text);
            int start = clearText.indexOf(openToken, offset);
            char[] src = clearText.toCharArray();
            while (start > -1) {
                if (start > 0 && src[start - 1] == '\\') {
                    // the variable is escaped. remove the backslash.
                    builder.append(src, offset, start - 1).append(openToken);
                    offset = start + openToken.length();
                } else {
                    int end = text.indexOf(closeToken, start);
                    if (end == -1) {
                        builder.append(src, offset, src.length - offset);
                        offset = src.length;
                    } else {
                        builder.append(src, offset, start - offset);
                        offset = start + openToken.length();
                        String content = new String(src, offset, end - offset);
                        builder.append(handler.handleToken(content));
                        offset = end + closeToken.length();
                    }
                }
                start = text.indexOf(openToken, offset);
            }
            if (offset < src.length) {
                builder.append(src, offset, src.length - offset);
            }
        }
        return builder.toString();
    }


    private String clear(String text) {
        int offset = 0;
        char[] src = text.toCharArray();
        int start = text.indexOf(openToken, offset);
        int end = text.indexOf(closeToken, start);
        if ((start == -1) || (start - 1 == -1) || (end + 1 >= src.length)) {
            return text;
        }

        if (src[start - 1] == '\'') {
            src[start - 1] = ' ';
        }
        if (src[end + 1] == '\'') {
            src[end + 1] = ' ';
        }
        return String.valueOf(src);
    }

}
