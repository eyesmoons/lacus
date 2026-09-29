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
 * 数据质量统计值快照实体（由 Spark 任务写入，不继承 BaseEntity）
 */
@Data
@TableName("dq_statistics_value")
public class DqStatisticsValueEntity {

    @ApiModelProperty("记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("规则ID")
    @TableField("rule_id")
    private Long ruleId;

    @ApiModelProperty("规则模板编码")
    @TableField("template_code")
    private String templateCode;

    @ApiModelProperty("执行记录ID（关联 dq_execution_log.id）")
    @TableField("log_id")
    private Long logId;

    @ApiModelProperty("统计指标名称，如 null_count.statistics_value")
    @TableField("statistics_name")
    private String statisticsName;

    @ApiModelProperty("统计值")
    @TableField("statistics_value")
    private BigDecimal statisticsValue;

    @ApiModelProperty("写入时间")
    @TableField("create_time")
    private Date createTime;
}
