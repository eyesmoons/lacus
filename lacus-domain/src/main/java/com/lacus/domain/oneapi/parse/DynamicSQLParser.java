package com.lacus.domain.oneapi.parse;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.lacus.common.exception.CustomException;
import com.lacus.domain.oneapi.parse.sqlnode.ForeachSQLNode;
import com.lacus.domain.oneapi.parse.sqlnode.IfSQLNode;
import com.lacus.domain.oneapi.parse.sqlnode.StaticSQLNode;
import com.lacus.domain.oneapi.parse.sqlnode.TextSQLNode;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.scripting.xmltags.IfSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.scripting.xmltags.TextSqlNode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DynamicSQLParser {


    /**
     * 解析动态请求参数
     *
     * @param sqlNodeList
     * @return
     */
    public Set<String> parseReq(List<SqlNode> sqlNodeList) {
        Set<String> requestParams = new HashSet<>();
        sqlNodeList.forEach(sqlNode -> {
            if (sqlNode instanceof IfSqlNode) {
                IfSQLNode ifSQLNode = new IfSQLNode();
                requestParams.addAll(ifSQLNode.sqlNodeParse(sqlNode, requestParams));
                return;
            }
            if (sqlNode instanceof ForEachSqlNode) {
                ForeachSQLNode foreachSQLNode = new ForeachSQLNode();
                requestParams.addAll(foreachSQLNode.sqlNodeParse(sqlNode, requestParams));
                return;
            }
            if (sqlNode instanceof TextSqlNode) {
                TextSQLNode textSQLNode = new TextSQLNode();
                requestParams.addAll(textSQLNode.sqlNodeParse(sqlNode, requestParams));
            }
            if (sqlNode instanceof StaticTextSqlNode) {
                StaticSQLNode staticSQLNode = new StaticSQLNode();
                requestParams.addAll(staticSQLNode.sqlNodeParse(sqlNode, requestParams));
            }
        });
        return requestParams;
    }


    /**
     * 解析静态返回参数
     *
     * @param sqlScript
     * @return
     */
    public Set<String> parseReturn(String sqlScript) {
        String lowerSql = sqlScript.toLowerCase();
        if (!lowerSql.contains("from")) {
            throw new CustomException("SQL语法错误，缺失From关键字");
        }
        String subSql = sqlScript.substring(0, lowerSql.indexOf("from")).replaceAll("\n", " ");
        String finalSql = subSql + " from lacus";
        return this.doParse(finalSql);
    }


    private Set<String> doParse(String sql) {
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
        Set<String> returnParams = new HashSet<>();
        sqlStatements.forEach(sqlStatement -> {
            if (sqlStatement instanceof SQLSelectStatement) {
                List<SQLSelectItem> selectList;
                try {
                    SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) sqlStatement;
                    MySqlSelectQueryBlock selectQuery = (MySqlSelectQueryBlock) sqlSelectStatement.getSelect().getQuery();
                    selectList = selectQuery.getSelectList();
                } catch (Exception aex) {
                    throw new CustomException("动态sql返回值列表解析异常", aex);
                }
                if (CollectionUtil.isNotEmpty(selectList)) {
                    selectList.forEach(selectItem -> {
                        String alias = selectItem.getAlias();
                        String column = ObjectUtil.isEmpty(alias) ? selectItem.toString() : alias;
                        if (column.trim().equals("*")) {
                            throw new CustomException("暂不支持`select * `语法");
                        }
                        returnParams.add(column.replaceAll("`", ""));
                    });
                }
            }
        });
        return returnParams;
    }

}
