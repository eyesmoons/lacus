package com.lacus.dao.dataquality.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 数据质量任务执行记录实体
 */
@Data
@TableName("dq_execution_log")
public class DqExecutionLogEntity {

    @ApiModelProperty("执行记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("规则ID")
    @TableField("rule_id")
    private Long ruleId;

    @ApiModelProperty("规则名称快照")
    @TableField("rule_name")
    private String ruleName;

    @ApiModelProperty("Spark Application ID")
    @TableField("spark_app_id")
    private String sparkAppId;

    @ApiModelProperty("执行状态: SUBMITTED / RUNNING / SUCCESS / FAILED / STOPPED")
    @TableField("status")
    private String status;

    @ApiModelProperty("开始时间")
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty("检查结果值")
    @TableField("result_value")
    private String resultValue;

    @ApiModelProperty("是否通过：1 通过 0 未通过")
    @TableField("pass_flag")
    private Integer passFlag;

    @ApiModelProperty("执行日志信息（包含 stdout/stderr）")
    @TableField("log_info")
    private String logInfo;

    @ApiModelProperty("数据源 ID")
    @TableField("datasource_id")
    private Long datasourceId;

    @ApiModelProperty("数据源名称快照")
    @TableField("datasource_name")
    private String datasourceName;

    @ApiModelProperty("数据库名")
    @TableField("db_name")
    private String dbName;

    @ApiModelProperty("数据表名")
    @TableField("table_name")
    private String tableName;

    @ApiModelProperty("检测字段（逗号分隔）")
    @TableField("field_names")
    private String fieldNames;

    @ApiModelProperty("本次执行的 Spark DataQuality 配置 JSON 快照")
    @TableField("task_config")
    private String taskConfig;

    @ApiModelProperty("错误数据行 HDFS 输出路径")
    @TableField("error_data_path")
    private String errorDataPath;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Date createTime;
}
