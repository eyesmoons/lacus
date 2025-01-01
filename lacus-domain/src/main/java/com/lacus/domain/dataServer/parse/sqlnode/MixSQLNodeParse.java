package com.lacus.domain.dataServer.parse.sqlnode;

import com.lacus.domain.dataServer.parse.DynamicSQLParser;
import com.lacus.domain.dataServer.parse.util.ReflectUtil;
import org.apache.ibatis.scripting.xmltags.SqlNode;

import java.util.List;
import java.util.Set;


public class MixSQLNodeParse extends SQLNodeParse<Set<String>> {


    private static final String CONTENTS = "contents";

    @Override
    public Set<String> sqlNodeParse(SqlNode sqlNode, Set<String> requestParams) {
        List<SqlNode> sqlNodeList = (List<SqlNode>) ReflectUtil.reflectPrivateFiled(CONTENTS, sqlNode);
        DynamicSQLParser dynamicSQLParser = new DynamicSQLParser();
        requestParams.addAll(dynamicSQLParser.parseDynamic(sqlNodeList));
        return requestParams;
    }
}
