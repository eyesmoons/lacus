package com.lacus.domain.oneapi.parse.sqlnode;

import com.lacus.utils.ReflectUtil;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaticSQLNode extends AbstractSQLNode<Set<String>> {

    private static final String TEXT = "text";

    private static final Pattern REGEX = Pattern.compile("#\\{([a-zA-z_0-9]+)\\}");


    @Override
    public Set<String> sqlNodeParse(SqlNode sqlNode, Set<String> requestParams) {
        if (sqlNode instanceof StaticTextSqlNode) {
            StaticTextSqlNode textSqlNode = (StaticTextSqlNode) sqlNode;
            String text = (String) ReflectUtil.reflectPrivateFiled(TEXT, textSqlNode);
            Matcher matcher = REGEX.matcher(text);
            while (matcher.find()) {
                String group = matcher.group();
                String param = group.substring(group.indexOf("{") + 1, group.indexOf("}"));
                requestParams.add(param);
            }
        }
        return requestParams;
    }
}
