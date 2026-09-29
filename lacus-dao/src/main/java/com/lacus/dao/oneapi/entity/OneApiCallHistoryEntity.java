package com.lacus.dao.oneapi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@TableName("one_api_call_history")
@ApiModel(value = "OneApiCallHistoryEntity对象", description = "api调用历史表")
public class OneApiCallHistoryEntity extends BaseEntity<OneApiCallHistoryEntity> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "history_id", type = IdType.AUTO)
    private Long historyId;

    @ApiModelProperty("调用日期")
    @TableField("call_date")
    private String callDate;

    @ApiModelProperty("调用ip")
    @TableField("call_ip")
    private String callIp;

    @ApiModelProperty("api地址")
    @TableField("api_url")
    private String apiUrl;

    @ApiModelProperty("调用状态：success/fail")
    @TableField("call_status")
    private String callStatus;

    @ApiModelProperty("调用code")
    @TableField("call_code")
    private Long callCode;

    @ApiModelProperty("错误信息")
    @TableField("error_info")
    private String errorInfo;

    @ApiModelProperty("调用延迟(ms)")
    @TableField("call_delay")
    private Long callDelay;

    @ApiModelProperty("调用时间")
    @TableField("call_time")
    private Date callTime;

    @Override
    public Serializable pkVal() {
        return this.historyId;
    }
}
