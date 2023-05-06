package com.lacus.domain.dataserver.parse.sqlnode;

import com.lacus.domain.dataserver.parse.util.ReflectUtil;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;

import java.util.Set;

/**
 * Created by:
 *
 * @Author: lit
 * @Date: 2023/04/28/14:32
 * @Description:
 */
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
