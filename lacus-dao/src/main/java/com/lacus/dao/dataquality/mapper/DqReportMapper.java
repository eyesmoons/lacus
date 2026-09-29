package com.lacus.dao.dataquality.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lacus.dao.dataquality.entity.DqCheckResultEntity;
import com.lacus.dao.dataquality.entity.DqReportVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据质量报告聚合 Mapper（基于 @Select GROUP BY SQL）
 */
public interface DqReportMapper extends BaseMapper<DqCheckResultEntity> {

    /**
     * 概览 — 检测总量/通过/失败（基于 el.start_time 过滤）
     */
    @Select("SELECT COUNT(*) AS total, " +
            "SUM(cr.pass_flag = 1) AS passed, " +
            "SUM(cr.pass_flag = 0) AS failed " +
            "FROM dq_check_result cr " +
            "JOIN dq_execution_log el ON cr.log_id = el.id " +
            "WHERE el.start_time BETWEEN #{startTime} AND #{endTime}")
    Map<String, Object> aggregateOverview(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 概览 — 执行成功/失败
     */
    @Select("SELECT SUM(el.status = 'SUCCESS') AS execSuccess, " +
            "SUM(el.status IN ('FAILED','STOPPED')) AS execFailed " +
            "FROM dq_execution_log el " +
            "WHERE el.start_time BETWEEN #{startTime} AND #{endTime}")
    Map<String, Object> aggregateExecSummary(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 趋势 — 日粒度（DATE_FORMAT 桶）
     */
    @Select("SELECT DATE_FORMAT(el.start_time,'%Y-%m-%d') AS time, " +
            "COUNT(*) AS count, " +
            "ROUND(SUM(cr.pass_flag = 1) / COUNT(*) * 100, 1) AS passRate " +
            "FROM dq_check_result cr " +
            "JOIN dq_execution_log el ON cr.log_id = el.id " +
            "WHERE el.start_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY DATE_FORMAT(el.start_time,'%Y-%m-%d') " +
            "ORDER BY time")
    List<DqReportVO.TrendBucketVO> aggregateTrendDay(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 趋势 — 周粒度（YEARWEEK 桶）
     */
    @Select("SELECT YEARWEEK(el.start_time) AS time, " +
            "COUNT(*) AS count, " +
            "ROUND(SUM(cr.pass_flag = 1) / COUNT(*) * 100, 1) AS passRate " +
            "FROM dq_check_result cr " +
            "JOIN dq_execution_log el ON cr.log_id = el.id " +
            "WHERE el.start_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY YEARWEEK(el.start_time) " +
            "ORDER BY time")
    List<DqReportVO.TrendBucketVO> aggregateTrendWeek(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 按规则 Top5 失败最多
     */
    @Select("SELECT cr.rule_name AS ruleName, COUNT(*) AS fails " +
            "FROM dq_check_result cr " +
            "JOIN dq_execution_log el ON cr.log_id = el.id " +
            "WHERE el.start_time BETWEEN #{startTime} AND #{endTime} AND cr.pass_flag = 0 " +
            "GROUP BY cr.rule_name ORDER BY fails DESC LIMIT 5")
    List<DqReportVO.RuleFailVO> aggregateTopRulesByFails(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 按维度（三表 JOIN，子查询关联 log_id）
     */
    @Select("SELECT t.dimension AS dimension, COUNT(*) AS fails " +
            "FROM dq_check_result cr " +
            "JOIN dq_rule r ON cr.rule_id = r.id " +
            "JOIN dq_rule_template t ON r.template_id = t.id " +
            "WHERE cr.log_id IN (SELECT id FROM dq_execution_log WHERE start_time BETWEEN #{startTime} AND #{endTime}) " +
            "AND cr.pass_flag = 0 " +
            "GROUP BY t.dimension")
    List<DqReportVO.DimensionFailVO> aggregateByDimension(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
