package com.lacus.domain.dataserver.parse.sqlnode;

import com.lacus.domain.dataserver.parse.util.ReflectUtil;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;

import java.util.Set;


public class StaticSQLNodeParse extends SQLNodeParse<String> {

    private static final String TEXT = "text";

    @Override
    public String sqlNodeParse(SqlNode sqlNode, Set<String> requestParams) {
        if (sqlNode instanceof StaticTextSqlNode) {
            String staticSql = (String) ReflectUtil.reflectPrivateFiled(TEXT, sqlNode);
            if (staticSql.trim().startsWith("select") ||
                    staticSql.trim().startsWith("SELECT")) {
                return staticSql;
            }
        }
        return null;
    }
}
