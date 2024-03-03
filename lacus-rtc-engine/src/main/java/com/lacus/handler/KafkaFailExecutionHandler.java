package com.lacus.handler;

import com.alibaba.fastjson2.JSON;
import com.lacus.common.enums.ErrorMsgEnum;
import com.lacus.common.utils.HttpUtils;
import com.lacus.common.utils.KafkaSingletonUtil;
import com.lacus.common.utils.KafkaUtil;
import com.lacus.model.ErrorMsgModel;
import com.lacus.model.RespContent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@Data
public class KafkaFailExecutionHandler implements FailExecutionHandler, Serializable {

    private static final long serialVersionUID = 1L;

    private String sourceTopic;

    private String targetKafkaTopic;

    private String business;

    private String sourceName, sinkName;

    private String errorTopic;

    private Properties props;

    private KafkaSingletonUtil kafkaSingletonUtil;

    private Long jobId;

    private Long instanceId;

    private final static int ERR_DATA_MAX_LENGTH = 65535;

    public KafkaFailExecutionHandler(String sourceTopic, String targetKafkaTopic, String business, String errorTopic, Properties props) {
        this.sourceTopic = sourceTopic;
        this.targetKafkaTopic = targetKafkaTopic;
        this.business = business;
        this.errorTopic = errorTopic;
        this.props = props;
    }

    public KafkaFailExecutionHandler(Long jobId, String sourceName, String sinkName, Long instanceId, String sourceTopic, String business, String errorTopic, Properties props) {
        this.sourceTopic = sourceTopic;
        this.jobId = jobId;
        this.sourceName = sourceName;
        this.sinkName = sinkName;
        this.instanceId = instanceId;
        this.business = business;
        this.errorTopic = errorTopic;
        this.props = props;
    }

    @Override
    public void failExecutionInit() {
        this.kafkaSingletonUtil = KafkaSingletonUtil.getInstance();
        this.kafkaSingletonUtil.init(props);
    }

    @Override
    public void failExecutionClose() {
        if (this.kafkaSingletonUtil != null) {
            this.kafkaSingletonUtil.close();
        }
    }

    @Override
    public void failExecution(String key, String errorData, String targetDorisDatabase, String targetDorisTable, RespContent resp,
                              String stackTrace, Integer errorCode) {
        List<ErrorMsgModel> errorMsgList = new ArrayList<>();
        ErrorMsgModel errorMsg = new ErrorMsgModel(business, errorData, errorCode, stackTrace, sourceTopic, targetDorisDatabase, targetDorisTable);
        if (Objects.nonNull(jobId)) {
            errorMsg.setJobId(jobId);
        }
        if (Objects.nonNull(instanceId)) {
            errorMsg.setInstanceId(instanceId);
        }
        // 错误数据超过ERR_DATA_MAX_LENGTH，则截取
        if (errorData.length() >= ERR_DATA_MAX_LENGTH) {
            errorData = errorData.substring(0, ERR_DATA_MAX_LENGTH);
            errorMsg.setTruncatedFlag(1);
            errorMsg.setErrorData(errorData);
        }
        //默认写doris失败
        errorMsg.setErrorMsgType(ErrorMsgEnum.DORIS_ERROR.name());
        if (key.contains(".")) {
            String[] split = key.split("\\.", 2);
            errorMsg.setSourceDbName(split[0]);
            errorMsg.setSourceTableName(split[1]);
        } else {
            errorMsg.setSourceKafkaTopic(key);
        }
        String alterMsg = "";
        if (resp != null) {
            alterMsg = alterMsg + resp.getMessage();
            errorMsg.setErrorDorisMsg(resp.getMessage());
            errorMsg.setErrorUrl(resp.getErrorURL());
            if (StringUtils.isNotBlank(resp.getErrorURL())) {
                alterMsg = alterMsg + ", url: " + resp.getErrorURL();
                String errorUrlDetail;
                try {
                    errorUrlDetail = HttpUtils.sendGet(resp.getErrorURL());
                } catch (IOException e) {
                    log.error("url请求错误:{}", resp.getErrorURL(), e);
                    errorUrlDetail = "url请求失败:" + resp.getErrorURL();
                }
                errorMsg.setErrorUrlDetail(errorUrlDetail);
            }
        }
        errorMsgList.add(errorMsg);
        KafkaUtil.sendMessageAsync(errorTopic, JSON.toJSONString(errorMsgList), props);
    }

    @Override
    public void failExecution(ErrorMsgModel errorMsg, String msg) {
        errorMsg.setBusiness(business);
        kafkaSingletonUtil.sendMessageAsync(errorTopic, JSON.toJSONString(errorMsg));
    }

    @Override
    public Long getJobId() {
        return jobId;
    }
}
