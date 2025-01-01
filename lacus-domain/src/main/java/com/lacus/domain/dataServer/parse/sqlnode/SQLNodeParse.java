package com.lacus.domain.dataServer.parse.sqlnode;

import org.apache.ibatis.scripting.xmltags.SqlNode;

import java.util.Set;


public abstract class SQLNodeParse<T> {

    public abstract T sqlNodeParse(SqlNode sqlNode, Set<String> requestParams);

}
