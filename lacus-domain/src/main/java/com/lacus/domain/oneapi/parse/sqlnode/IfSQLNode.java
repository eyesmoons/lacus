package com.lacus.domain.oneapi.parse.sqlnode;

import com.lacus.utils.ReflectUtil;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;

import java.util.Set;

public class IfSQLNode extends AbstractSQLNode<Set<String>> {

    private static final String CONTENTS = "contents";

    @Override
    public Set<String> sqlNodeParse(SqlNode sqlNode, Set<String> requestParams) {
        SqlNode ifSqlNode = (SqlNode) ReflectUtil.reflectPrivateFiled(CONTENTS, sqlNode);
        if (ifSqlNode instanceof MixedSqlNode) {
            MixSQLNode mixSQLNode = new MixSQLNode();
            requestParams.addAll(mixSQLNode.sqlNodeParse(ifSqlNode, requestParams));
        }
        return requestParams;
    }
}

