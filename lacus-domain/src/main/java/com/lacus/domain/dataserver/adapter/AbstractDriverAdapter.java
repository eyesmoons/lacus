package com.lacus.domain.dataserver.adapter;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.utils.strings.StringUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class AbstractDriverAdapter<T> {


    public abstract T parse(String apiScript);


    /**
     * 解析sql返回值参数列表
     *
     * @param sql
     * @param driverType
     * @return
     */
    public Set<String> staticReturnParam(String sql, DbType driverType) {
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, driverType);
        Set<String> returnParams = new HashSet<>();
        try {
            sqlStatements.forEach(sqlStatement -> {
                if (sqlStatement instanceof SQLSelectStatement) {
                    SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) sqlStatement;
                    MySqlSelectQueryBlock selectQuery = (MySqlSelectQueryBlock) sqlSelectStatement.getSelect().getQuery();
                    List<SQLSelectItem> selectList = selectQuery.getSelectList();
                    selectList.forEach(selectItem -> {
                        String column = selectItem.getAlias().replaceAll("`", "");
                        if (column.trim().equals("*")) {
                            throw new ApiException(ErrorCode.Business.FULL_COLUMN_QUERY_ERROR);
                        }
                        if (StringUtil.checkValNull(column)) {
                            column = selectItem.toString().replaceAll("`", "");
                        }
                        returnParams.add(column);
                    });
                }
            });
        } catch (ApiException aex) {
            throw new ApiException(aex, ErrorCode.Business.RETURN_COLUMN_PARSE_ERROR);
        }
        return returnParams;
    }


}
