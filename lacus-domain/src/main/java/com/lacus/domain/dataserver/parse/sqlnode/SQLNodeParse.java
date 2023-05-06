package com.lacus.domain.dataserver.parse.sqlnode;

import org.apache.ibatis.scripting.xmltags.SqlNode;

import java.util.Set;

/**
 * Created by:
 *
 * @Author: lit
 * @Date: 2023/04/28/14:30
 * @Description:
 */
public abstract class SQLNodeParse<T> {

    public abstract T sqlNodeParse(SqlNode sqlNode, Set<String> requestParams);

}
