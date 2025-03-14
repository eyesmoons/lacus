package com.lacus.domain.oneapi.parse.sqlnode;

import com.lacus.domain.oneapi.parse.DynamicSQLParser;
import com.lacus.utils.ReflectUtil;
import org.apache.ibatis.scripting.xmltags.SqlNode;

import java.util.List;
import java.util.Set;

public class MixSQLNode extends AbstractSQLNode<Set<String>> {

    private static final String CONTENTS = "contents";


    @Override
    public Set<String> sqlNodeParse(SqlNode sqlNode, Set<String> requestParams) {
        List<SqlNode> sqlNodeList = (List<SqlNode>) ReflectUtil.reflectPrivateFiled(CONTENTS, sqlNode);
        DynamicSQLParser dynamicSQLParser = new DynamicSQLParser();
        requestParams.addAll(dynamicSQLParser.parseReq(sqlNodeList));
        return requestParams;
    }
}
