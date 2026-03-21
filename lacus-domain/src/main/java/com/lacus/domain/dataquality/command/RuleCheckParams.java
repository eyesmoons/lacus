package com.lacus.domain.dataquality.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 校验规则参数（步骤四：校验方式/操作符/期望值及模板专属配置）
 * 各模板专属字段仅在对应模板下使用，其余为 null。
 */
@Data
public class RuleCheckParams {

    // ---- 通用校验字段 ----

    @ApiModelProperty("校验方式：fixed_check（固定值比较）/ fluctuation_check（波动率比较）")
    private String checkMethod;

    @ApiModelProperty("校验操作符：= / != / > / >= / < / <=")
    private String operator;

    @ApiModelProperty("期望值类型：FIXED / DAILY_AVG / WEEKLY_AVG / MONTHLY_AVG / LAST_7_DAYS_AVG / LAST_30_DAYS_AVG")
    private String expectedType;

    @ApiModelProperty("期望值（expectedType=FIXED 时使用）")
    private Double expectedValue;

    // ---- 完整性：空值/空字符串 ----
    // NULL_CHECK / EMPTY_STRING_CHECK：无专属字段，直接使用 field

    // ---- 唯一性 ----
    // UNIQUENESS_CHECK / DUPLICATE_COUNT_CHECK：无专属字段

    // ---- 唯一性：去重值个数（DISTINCT_COUNT_CHECK）无专属字段，比较结果与 expectedValue ----

    // ---- 及时性：时间字段比较（SINGLE_TABLE_TIME_CHECK / CROSS_TABLE_TIME_CHECK）----

    @ApiModelProperty("对比时间字段名（SINGLE_TABLE_TIME_CHECK / CROSS_TABLE_TIME_CHECK / CONSISTENCY_CHECK 专用）")
    private String field2;

    @ApiModelProperty("时间差单位（秒数）：1=秒 / 60=分 / 3600=时 / 86400=天（时间比较模板专用，Spark SQL 用 UNIX_TIMESTAMP 差值除以该值）")
    private String timeUnit;

    @ApiModelProperty("时间差阈值（超过此值为异常，时间比较模板专用）")
    private Double threshold;

    @ApiModelProperty("参考表名（CROSS_TABLE_TIME_CHECK 专用）")
    private String refTable;

    @ApiModelProperty("参考数据库名（CROSS_TABLE_TIME_CHECK 专用，与主表共用同一数据源）")
    private String refDbName;

    @ApiModelProperty("两表关联字段名（CROSS_TABLE_TIME_CHECK 专用）")
    private String joinField;

    // ---- 有效性：正则格式（REGEX_CHECK）----

    @ApiModelProperty("正则表达式（REGEX_CHECK 专用）")
    private String regexPattern;

    // ---- 有效性：字段长度（LENGTH_CHECK）----

    @ApiModelProperty("长度操作符：< / > / = / != / <= / >=（LENGTH_CHECK 专用）")
    private String lengthOp;

    @ApiModelProperty("长度阈值（LENGTH_CHECK 专用）")
    private Integer length;

    // ---- 稳定性：统计值校验（STAT_CHECK / FLUCTUATION_CHECK）----

    @ApiModelProperty("统计方式：AVG / MAX / MIN / SUM / COUNT（STAT_CHECK / FLUCTUATION_CHECK 专用）")
    private String statMethod;
}
