package com.lacus.core.druid;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;
import com.lacus.common.exception.ResultCode;
import com.lacus.common.constants.Constant;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.BusinessException;
import com.lacus.core.buried.BuriedFunc;
import com.lacus.core.buried.LogsBuried;
import com.lacus.service.vo.DataPageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.alibaba.druid.sql.SQLUtils.parseStatements;

@Component
@Slf4j
public class DruidSQLParser {

    private static SQLSelectQuery initCountQuery;

    private static SQLSelectQueryBlock initCountQueryBlock;

    @PostConstruct
    private void init() {
        final String initSql = "select COUNT(1) as ct from lacus;";
        initCountQuery = (((SQLSelectStatement) parseStatements(initSql, JdbcConstants.MYSQL).iterator().next()).getSelect()).getQuery();
        initCountQueryBlock = (SQLSelectQueryBlock) initCountQuery;
    }

    public static String buildCountSql(String originSql) {
        try {
            SQLSelectQuery selectQuery = (((SQLSelectStatement) parseStatements(originSql, JdbcConstants.MYSQL).iterator().next()).getSelect()).getQuery();
            SQLSelectQueryBlock realSQLSelectQueryBlock = (SQLSelectQueryBlock) selectQuery;
            initCountQueryBlock.setFrom(realSQLSelectQueryBlock, Constant.ALIAS);
            return SQLUtils.toSQLString(initCountQuery);
        } catch (Exception e) {
            log.error("COUNT_SQL解析错误:{}", originSql, e);
            BuriedFunc.addLog(LogsBuried.setError("异步构建数据总量查询SQL错误！"));
            throw new ApiException(ResultCode.COUNT_SQL_PARSE_ERROR, e.getCause());
        }

    }

    public static String buildPageLimitQuery(String sql, DataPageVO page) {
        try {
            SQLSelectQuery selectQuery = (((SQLSelectStatement) parseStatements(sql, JdbcConstants.MYSQL).iterator().next()).getSelect()).getQuery();
            SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock) selectQuery;
            SQLOrderBy orderBy = selectQueryBlock.getOrderBy();
            if (orderBy == null) {
                throw new BusinessException(ResultCode.LIMIT_OFFSET_MUST_BE_ORDER_BY);
            }

            int pageSize = page.getPageSize();
            int pageNum = (page.getPageNum() - 1) * page.getPageSize();


            if (pageSize > Constant.MAX_PAGE_SIZE) {
                throw new BusinessException(ResultCode.PAGE_SIZE_MORE_THAN_MAX);
            }

            SQLLimit sqlLimit = selectQueryBlock.getLimit();
            if (sqlLimit == null) {
                SQLLimit newSqlLimit = new SQLLimit();
                newSqlLimit.setOffset(pageNum);
                newSqlLimit.setRowCount(pageSize);
                selectQueryBlock.setLimit(newSqlLimit);
            }
            return SQLUtils.toSQLString(selectQuery);
        } catch (Exception e) {
            log.error("分页SQL构建失败:{}", sql, e);
            BuriedFunc.addLog(LogsBuried.setError("分页查询SQL构建失败! "));
            throw new BusinessException(ResultCode.LIMIT_SQL_PARSE_ERROR, e.getMessage());
        }
    }

    public static String buildTotalLimitQuery(Integer limitCount, String sql) {
        try {
            SQLSelectQuery selectQuery = (((SQLSelectStatement) parseStatements(sql.trim(), JdbcConstants.MYSQL).iterator().next()).getSelect()).getQuery();
            SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock) selectQuery;
            SQLLimit sqlLimit = selectQueryBlock.getLimit();
            int dataMaxLimit = ((limitCount == null) || (limitCount > Constant.DEFAULT_LIMIT)) ? Constant.DEFAULT_LIMIT : limitCount;
            if (sqlLimit == null) {
                sqlLimit = new SQLLimit();
            }
            sqlLimit.setRowCount(dataMaxLimit);
            selectQueryBlock.setLimit(sqlLimit);
            return SQLUtils.toSQLString(selectQuery);

        } catch (Exception e) {
            BuriedFunc.addLog(LogsBuried.setError("Limit_SQL构建失败! "));
            throw new ApiException(ResultCode.LIMIT_SQL_PARSE_ERROR, e.getMessage());
        }
    }
}
