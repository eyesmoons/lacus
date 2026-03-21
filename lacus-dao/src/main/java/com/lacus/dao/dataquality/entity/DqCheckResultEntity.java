package com.lacus.dao.dataquality.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 数据质量检测结果明细实体（由 Spark 任务写入，不继承 BaseEntity）
 */
@Data
@TableName("dq_check_result")
public class DqCheckResultEntity {

    @ApiModelProperty("结果ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("关联执行记录ID")
    @TableField("log_id")
    private Long logId;

    @ApiModelProperty("规则ID")
    @TableField("rule_id")
    private Long ruleId;

    @ApiModelProperty("规则名称快照")
    @TableField("rule_name")
    private String ruleName;

    @ApiModelProperty("规则模板编码")
    @TableField("template_code")
    private String templateCode;

    @ApiModelProperty("实际执行的检测SQL")
    @TableField("check_sql")
    private String checkSql;

    @ApiModelProperty("检测到的实际值")
    @TableField("actual_value")
    private BigDecimal actualValue;

    @ApiModelProperty("期望值")
    @TableField("expected_value")
    private BigDecimal expectedValue;

    @ApiModelProperty("期望值类型：FIXED/DAILY_AVG等")
    @TableField("expected_type")
    private String expectedType;

    @ApiModelProperty("校验方式")
    @TableField("check_method")
    private String checkMethod;

    @ApiModelProperty("校验操作符")
    @TableField("operator")
    private String operator;

    @ApiModelProperty("公式计算结果")
    @TableField("formula_result")
    private BigDecimal formulaResult;

    @ApiModelProperty("是否通过：1通过 0不通过")
    @TableField("pass_flag")
    private Integer passFlag;

    @ApiModelProperty("写入时间")
    @TableField("create_time")
    private Date createTime;
}
