package com.lacus.core.provider;

import cn.hutool.core.collection.CollectionUtil;
import com.lacus.common.exception.ResultCode;
import com.lacus.common.constants.Constant;
import com.lacus.common.exception.ApiException;
import com.lacus.core.buried.BuriedFunc;
import com.lacus.core.buried.LogsBuried;
import com.lacus.core.druid.DruidExecutorEngin;
import com.lacus.core.druid.DruidSQLParser;
import com.lacus.service.dto.ApiConfigDTO;
import com.lacus.common.enums.DatabaseType;
import com.lacus.common.enums.PageFlagEnum;
import com.lacus.common.annotation.ProviderName;
import com.lacus.core.processor.AbstractProcessor;
import com.lacus.core.processor.ProcessorManager;
import com.lacus.core.processor.jdbc.meta.SQLMeta;
import com.lacus.service.vo.DataPageVO;
import com.lacus.service.vo.RequestParamsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ProviderName(value = {DatabaseType.MySQL, DatabaseType.Doris})
@Slf4j
@Component
public class JdbcProvider extends AbstractProvider {


    @Override
    public Long processTotal(ApiConfigDTO config, Map<String, Object> params) {
        Map<String, String> requestKeys = config.getApiParams().getRequestParams().stream().collect(Collectors.toMap(RequestParamsVO::getColumnName, RequestParamsVO::getColumnType));
        SQLMeta sqlMeta = getSQLMeta(config.getSql(), requestKeys, params);
        //解析后的参数列表
        List<RequestParamsVO> parameter = sqlMeta.getParameter();
        this.combine(parameter, config);
        String countSql = DruidSQLParser.buildCountSql(sqlMeta.getSql());
        log.info("编译后的COUNT_SQL为:{}", removeWrap(countSql));
        config.setSql(countSql);
        List<Map<String, Object>> result = DruidExecutorEngin.execute(datasourceInfo, config);
        return resultToLong(result);
    }

    @Override
    public List<Map<String, Object>> process(ApiConfigDTO config, Map<String, Object> params) {
        DataPageVO dataPageVO = null;
        Integer pageFlag = config.getPageFlag();
        if (Objects.equals(pageFlag, PageFlagEnum.FLAG_TRUE.getCode())) {
            int pageNum = CollectionUtil.isNotEmpty(params) ? Integer.parseInt(params.get(Constant.PAGE_NUM).toString()) : 1;
            int pageSize = CollectionUtil.isNotEmpty(params) ? Integer.parseInt(params.get(Constant.PAGE_SIZE).toString()) : 10;
            dataPageVO = new DataPageVO();
            dataPageVO.setPageNum(pageNum);
            dataPageVO.setPageSize(pageSize);
            dataPageVO.setPageFlag(PageFlagEnum.FLAG_TRUE.getCode());
        }
        Map<String, String> requestKeys = config.getApiParams().getRequestParams().stream().collect(Collectors.toMap(RequestParamsVO::getColumnName, RequestParamsVO::getColumnType));
        SQLMeta sqlMeta = getSQLMeta(config.getSql(), requestKeys, params);
        String sql = sqlMeta.getSql();
        if (Objects.equals(pageFlag, PageFlagEnum.FLAG_TRUE.getCode())) {
            //解析sql自动构建limit分页语句
            sql = DruidSQLParser.buildPageLimitQuery(sql, dataPageVO);
        } else {
            //非分页情况需要增加Limit
            sql = DruidSQLParser.buildTotalLimitQuery(config.getLimitCount(), sql);
        }
        log.info("编译后的SQL为：{}", sql);
        BuriedFunc.addLog(LogsBuried.setInfo(String.format("编译后的SQL为:【%s】！", removeWrap(sql))));
        config.setSql(sql);
        return DruidExecutorEngin.execute(datasourceInfo, config);
    }


    /**
     * 根据配置拿到请求参数的字段和类型映射map，然后填充sql解析后的请求参数对象
     *
     * @param parameter
     * @param config
     */
    private void combine(List<RequestParamsVO> parameter, ApiConfigDTO config) {
        List<RequestParamsVO> requestParams = config.getApiParams().getRequestParams();
        Map<String, String> paramsMap = requestParams.stream().collect(Collectors.toMap(RequestParamsVO::getColumnName, RequestParamsVO::getColumnType));
        for (RequestParamsVO paramsVO : parameter) {
            paramsVO.setColumnType(paramsMap.get(paramsVO.getColumnName()));
        }
    }

    private Long resultToLong(List<Map<String, Object>> result) {
        if (CollectionUtil.isEmpty(result)) {
            return 0L;
        }
        return Objects.isNull(result.get(0).get(Constant.CNT)) ? 0L : Long.parseLong(result.get(0).get(Constant.CNT).toString());
    }

    private SQLMeta getSQLMeta(String sql, Map<String, String> requestKeys, Map<String, Object> params) {
        try {
            String sqlTpl = sql.replaceAll("\\\\", "");
            AbstractProcessor processor = ProcessorManager.getProcessor(DatabaseType.MySQL);
            assert processor != null;
            return processor.process(sqlTpl, requestKeys, params);
        } catch (Exception e) {
            log.info("SQL解析失败，失败SQL:{},异常原因:", sql, e);
            throw new ApiException(ResultCode.SQL_PARSE_ERROR, e.getMessage());
        }
    }

    public static String removeWrap(String sql) {
        return sql.replaceAll("\r|\n", " ");
    }

}
