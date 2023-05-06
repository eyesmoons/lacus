package com.lacus.domain.dataserver.parse;

import com.lacus.domain.dataserver.parse.sqlnode.ForeachSQLNodeParse;
import com.lacus.domain.dataserver.parse.sqlnode.IfSQLNodeParse;
import com.lacus.domain.dataserver.parse.sqlnode.StaticSQLNodeParse;
import com.lacus.domain.dataserver.parse.sqlnode.TextSQLNodeParse;
import org.apache.ibatis.scripting.xmltags.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by:
 *
 * @Author: lit
 * @Date: 2023/04/28/13:30
 * @Description:
 */
public abstract class AbstractSQLParser {


    /**
     * 解析动态请求参数
     *
     * @param sqlNodeList
     * @return
     */
    public Set<String> parseDynamic(List<SqlNode> sqlNodeList) {
        Set<String> requestParamList = new HashSet<>();
        sqlNodeList.forEach(sqlNode -> {
            if (sqlNode instanceof IfSqlNode) {
                IfSQLNodeParse ifsqlNodeParse = new IfSQLNodeParse();
                requestParamList.addAll(ifsqlNodeParse.sqlNodeParse(sqlNode, requestParamList));
                return;
            }
            if (sqlNode instanceof ForEachSqlNode) {
                ForeachSQLNodeParse foreachSQLNodeParse = new ForeachSQLNodeParse();
                requestParamList.addAll(foreachSQLNodeParse.sqlNodeParse(sqlNode, requestParamList));
                return;
            }
            if (sqlNode instanceof TextSqlNode) {
                TextSQLNodeParse textSQLNodeParse = new TextSQLNodeParse();
                requestParamList.addAll(textSQLNodeParse.sqlNodeParse(sqlNode, requestParamList));
            }
        });
        return requestParamList;
    }


    /**
     * 解析静态返回参数
     *
     * @param sqlNodeList
     * @return
     */
    public String parseStatic(List<SqlNode> sqlNodeList) {
        for (SqlNode sqlNode : sqlNodeList) {
            if (!(sqlNode instanceof StaticTextSqlNode)) {
                continue;
            }
            StaticSQLNodeParse staticSQLNodeParse = new StaticSQLNodeParse();
            String staticSql = staticSQLNodeParse.sqlNodeParse(sqlNode, null);
            if (staticSql != null) {
                return staticSql;
            }
        }
        return null;
    }


}
