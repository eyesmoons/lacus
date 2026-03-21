package com.lacus.domain.dataquality.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 规则结构化参数，由前端四步向导填写，后端转换为 DataQualityConfiguration JSON
 */
@Data
public class RuleParams {

    @ApiModelProperty("规则模板类型：NULL_CHECK/UNIQUENESS_CHECK/DUPLICATE_CHECK/RANGE_CHECK/ENUM_CHECK/REGEX_CHECK")
    private String template;

    @ApiModelProperty("数据源ID")
    private Long datasourceId;

    @ApiModelProperty("数据源名称（前端回传，用于展示）")
    private String datasourceName;

    @ApiModelProperty("数据库名称")
    private String database;

    @ApiModelProperty("数据表名称")
    private String table;

    @ApiModelProperty("检测字段列表")
    private List<String> fields;

    @ApiModelProperty("校验方式：expected_minus_actual/actual_minus_expected/actual_div_expected/diff_div_expected")
    private String checkMethod;

    @ApiModelProperty("校验操作符：=/!=/>/>=/</<= ")
    private String operator;

    @ApiModelProperty("期望值类型：FIXED/DAILY_AVG/WEEKLY_AVG/MONTHLY_AVG/LAST_7_DAYS_AVG/LAST_30_DAYS_AVG")
    private String expectedType;

    @ApiModelProperty("期望值（expectedType=FIXED时使用）")
    private Double expectedValue;

    // ---- 模板专属字段 ----

    @ApiModelProperty("最小值（RANGE_CHECK 专用）")
    private Double minValue;

    @ApiModelProperty("最大值（RANGE_CHECK 专用）")
    private Double maxValue;

    @ApiModelProperty("允许的枚举值列表（ENUM_CHECK 专用）")
    private List<String> enumValues;

    @ApiModelProperty("正则表达式（REGEX_CHECK 专用）")
    private String regexPattern;
}
