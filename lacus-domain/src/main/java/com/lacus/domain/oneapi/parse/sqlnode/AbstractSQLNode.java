package com.lacus.domain.oneapi.parse.sqlnode;

import org.apache.ibatis.scripting.xmltags.SqlNode;

import java.util.Set;

public abstract class AbstractSQLNode<T> {

    public abstract T sqlNodeParse(SqlNode sqlNode, Set<String> requestParams);

}
