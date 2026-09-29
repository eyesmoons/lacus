package com.lacus.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.enums.CallStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@TableName("one_api_call_history")
@Getter
@Setter
public class ApiHistoryEntity {

    @TableId(value = "history_id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Long historyId;

    @TableField("call_date")
    @ApiModelProperty("调用日期")
    private String callDate;

    @TableField("call_ip")
    @ApiModelProperty("调用ip")
    private String callIp;

    @TableField("api_url")
    @ApiModelProperty("api地址")
    private String apiUrl;

    @TableField("call_status")
    @ApiModelProperty("调用状态")
    private CallStatusEnum callStatus;

    @TableField("call_code")
    @ApiModelProperty("调用code")
    private Long callCode;

    @TableField("error_info")
    @ApiModelProperty("错误信息")
    private String errorInfo;

    @TableField("call_delay")
    @ApiModelProperty("调用延迟")
    private Long callDelay;

    @TableField("call_time")
    @ApiModelProperty("调用时间")
    private Date callTime;

    @TableField("deleted")
    private Integer deleted;

    @TableField("creator_id")
    private String creatorId;

    @TableField("create_time")
    private Date createTime;

    @TableField("updater_id")
    private String updaterId;

    @TableField("update_time")
    private Date updateTime;

    public ApiHistoryEntity() {

    }

    public ApiHistoryEntity callCode(Long callCode) {
        this.callCode = callCode;
        return this;
    }

    public ApiHistoryEntity errorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
        return this;
    }

    public ApiHistoryEntity callStatus(CallStatusEnum callStatus) {
        this.callStatus = callStatus;
        return this;
    }


    public ApiHistoryEntity apiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
        return this;
    }


    public ApiHistoryEntity callDate(String callDate) {
        this.callDate = callDate;
        return this;
    }


    public ApiHistoryEntity callTime(Date callTime) {
        this.callTime = callTime;
        return this;
    }

}
