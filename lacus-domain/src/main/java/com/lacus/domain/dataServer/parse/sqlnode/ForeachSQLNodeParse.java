package com.lacus.domain.dataServer.parse.sqlnode;

import com.lacus.domain.dataServer.parse.util.ReflectUtil;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;

import java.util.Set;


public class ForeachSQLNodeParse extends SQLNodeParse<Set<String>> {

    private static final String COLLECTION_EXPRESSION = "collectionExpression";

    @Override
    public Set<String> sqlNodeParse(SqlNode sqlNode,Set<String> requestParams) {
        if (sqlNode instanceof ForEachSqlNode) {
            ForEachSqlNode forEachSqlNode = (ForEachSqlNode) sqlNode;
            String foreachParam = (String) ReflectUtil.reflectPrivateFiled(COLLECTION_EXPRESSION, forEachSqlNode);
            requestParams.add(foreachParam);
        }
        return requestParams;
    }
}
