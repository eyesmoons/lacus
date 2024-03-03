package com.lacus.model;

import com.alibaba.fastjson2.annotation.JSONField;
import com.lacus.common.enums.StatusCodeEnum;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class ErrorMsgModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务id
     */
    @JSONField(name = "job_id")
    private Long jobId;

    @JSONField(name = "instance_id")
    private Long instanceId;

    /**
     * 记录时间
     */
    @JSONField(name = "record_time",format = "yyyy-MM-dd HH:mm:ss" )
    private Timestamp recordTime;
    /**
     * 业务名称
     */
    @JSONField(name = "business")
    private String business;
    /**
     * 错误类型：errorMsgType ErrorMsgEnum
     */
    @JSONField(name = "error_msg_type")
    private String errorMsgType;

    /**
     * 数据源名称
     */
    @JSONField(name = "source_datasource_name")
    private String sourceDatasourceName;

    /**
     * 数据来源-数据库名称
     */
    @JSONField(name = "source_db_name")
    private String sourceDbName;
    /**
     * 数据来源-表名
     */
    @JSONField(name = "source_table_name")
    private String sourceTableName;
    /**
     * Kafka对列名称-canal侧
     */
    @JSONField(name = "source_kafka_topic")
    private String sourceKafkaTopic;
    /**
     * Kafka对列名称-目标侧
     */
    @JSONField(name = "target_kafka_topic")
    private String targetKafkaTopic;
    /**
     * dori数据源名称-目标侧
     */
    @JSONField(name = "sink_datasource_name")
    private String sinkDatasourceName;
    /**
     * doris库名称-目标侧
     */
    @JSONField(name = "sink_db_name")
    private String sinkDbName;
    /**
     * doris表名称-目标侧
     */
    @JSONField(name = "sink_table_name")
    private String sinkTableName;
    /**
     * 异常数据
     */
    @JSONField(name = "error_data")
    private String errorData;

    @JSONField(name = "truncated_flag")
    private Integer truncatedFlag = 0;

    /**
     * 异常类型 - statusCodeEnum
     */
    @JSONField(name = "error_type")
    private Integer errorType;
    /**
     * 异常堆栈信息
     */
    @JSONField(name = "error_msg")
    private String errorMsg;
    /**
     * 简单异常信息msg
     */
    @JSONField(name = "error_doris_msg")
    private String errorDorisMsg;
    /**
     * 异常信息
     */
    @JSONField(name = "error_url")
    private String errorUrl;
    /**
     * 异常信息
     */
    @JSONField(name = "error_url_detail")
    private String errorUrlDetail;

    public ErrorMsgModel() {
        this.recordTime = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Streamload 错误异常体
     *
     */
    public ErrorMsgModel(String business, String errorData, Integer errorType, String errorMsg,
                         String sourceKafkaTopic, String sinkDbName, String sinkTableName) {
        this.business = business;
        this.errorData = errorData;
        this.errorType = errorType;
        this.errorMsg = errorMsg;
        this.sourceKafkaTopic = sourceKafkaTopic;
        this.sinkDbName = sinkDbName;
        this.sinkTableName = sinkTableName;
        this.recordTime = new Timestamp(System.currentTimeMillis());
    }

    /**
     * 历史接入任务
     */
    public ErrorMsgModel(String business, String errorData, Integer errorType, String errorMsg,
                         String sourceKafkaTopic, String targetKafkaTopic) {
        this.business = business;
        this.errorData = errorData;
        this.errorType = errorType;
        this.errorMsg = errorMsg;
        this.sourceKafkaTopic = sourceKafkaTopic;
        this.targetKafkaTopic = targetKafkaTopic;
        this.recordTime = new Timestamp(System.currentTimeMillis());
    }

    public ErrorMsgModel(Long jobId, Timestamp recordTime, String business, String errorMsgType,
                         String sourceDbName, String sourceTableName,
                         String sourceKafkaTopic, String targetKafkaTopic,
                         String sinkDatasourceName, String sinkDbName, String sinkTableName,
                         String errorData, Integer errorType, String errorMsg, String errorDorisMsg, String errorUrl, String errorUrlDetail) {
        this.jobId = jobId;
        this.recordTime = recordTime;
        this.business = business;
        this.errorMsgType = errorMsgType;
        this.sourceDbName = sourceDbName;
        this.sourceTableName = sourceTableName;
        this.sourceKafkaTopic = sourceKafkaTopic;
        this.targetKafkaTopic = targetKafkaTopic;
        this.sinkDatasourceName = sinkDatasourceName;
        this.sinkDbName = sinkDbName;
        this.sinkTableName = sinkTableName;
        this.errorData = errorData;
        this.errorType = errorType;
        this.errorMsg = errorMsg;
        this.errorDorisMsg = errorDorisMsg;
        this.errorUrl = errorUrl;
        this.errorUrlDetail = errorUrlDetail;
    }

    /**
     * 初始化清洗异常
     */
    public void initClean(Long jobId, String data, String topic,
                            String datasource,String dbName,String tblName,String errorMsg){
        this.jobId = jobId;
        this.errorData = data;
        this.sourceKafkaTopic = topic;
        this.sinkDatasourceName = datasource;
        this.sinkDbName = dbName;
        this.sinkTableName = tblName;
        this.errorType = StatusCodeEnum.ENCRYPT_SETTING_ERROR.getCode();
        this.recordTime = new Timestamp(System.currentTimeMillis());
        this.errorMsg = errorMsg;
    }
}
