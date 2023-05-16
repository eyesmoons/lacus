package com.lacus.domain.dataserver.parse.sqlnode;

import com.lacus.domain.dataserver.parse.util.ReflectUtil;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;

import java.util.Set;


public class IfSQLNodeParse extends SQLNodeParse<Set<String>> {

    private static final String CONTENTS = "contents";

    @Override
    public Set<String> sqlNodeParse(SqlNode sqlNode, Set<String> requestParams) {
        SqlNode ifSqlNode = (SqlNode) ReflectUtil.reflectPrivateFiled(CONTENTS, sqlNode);
        if (ifSqlNode instanceof MixedSqlNode) {
            MixSQLNodeParse mixSQLNodeParse = new MixSQLNodeParse();
            requestParams.addAll(mixSQLNodeParse.sqlNodeParse(ifSqlNode, requestParams));
        }
        return requestParams;
    }
}
