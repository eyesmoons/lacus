package com.lacus.flink.parser;

import com.lacus.flink.dto.SqlCommand;
import com.lacus.flink.enums.SqlCommandEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

public class SqlFileParser {


    public static List<String> parserSql(List<String> lineList) {
        if (CollectionUtils.isEmpty(lineList)) {
            throw new RuntimeException("lineList is null");
        }
        List<String> sqlList = new ArrayList<>();
        StringBuilder stmt = new StringBuilder();
        for (String line : lineList) {
            if (line.trim().isEmpty() || line.startsWith("--") || trimStart(line).startsWith("--")) {
                continue;
            }
            stmt.append("\n").append(line);
            if (line.trim().endsWith(";")) {
                sqlList.add(stmt.substring(0, stmt.length() - 1));
                stmt.setLength(0);
            }
        }
        return sqlList;
    }

    public static List<SqlCommand> fileToSql(List<String> lineList) {
        if (CollectionUtils.isEmpty(lineList)) {
            throw new RuntimeException("lineList is null");
        }

        List<SqlCommand> sqlCommandList = new ArrayList<>();

        StringBuilder stmt = new StringBuilder();

        for (String line : lineList) {
            if (line.trim().isEmpty() || line.startsWith("--") || trimStart(line).startsWith("--")) {
                continue;
            }
            stmt.append("\n").append(line);
            if (line.trim().endsWith(";")) {
                Optional<SqlCommand> optionalCall = parse(stmt.toString());
                if (optionalCall.isPresent()) {
                    sqlCommandList.add(optionalCall.get());
                } else {
                    throw new RuntimeException("不支持该语法使用" + stmt.toString() + "'");
                }
                stmt.setLength(0);
            }
        }
        return sqlCommandList;

    }

    private static Optional<SqlCommand> parse(String stmt) {
        stmt = stmt.trim();
        if (stmt.endsWith(";")) {
            stmt = stmt.substring(0, stmt.length() - 1).trim();
        }
        for (SqlCommandEnum cmd : SqlCommandEnum.values()) {
            final Matcher matcher = cmd.getPattern().matcher(stmt);
            if (matcher.matches()) {
                final String[] groups = new String[matcher.groupCount()];
                for (int i = 0; i < groups.length; i++) {
                    groups[i] = matcher.group(i + 1);
                }
                return cmd.getOperandConverter().apply(groups).map((operands) -> new SqlCommand(cmd, operands));
            }
        }
        return Optional.empty();
    }

    private static String trimStart(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        final char[] value = str.toCharArray();

        int start = 0;
        int end = str.length() - 1;
        while ((start <= end) && (value[start] <= ' ')) {
            start++;
        }
        if (start == 0) {
            return str;
        }
        if (start >= end) {
            return "";
        }
        return str.substring(start, end);
    }
}
