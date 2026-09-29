package com.lacus.utils.yarn;

import com.lacus.utils.time.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.hadoop.yarn.api.records.ApplicationReport;

import java.util.Date;

@Data
public class ApplicationModel {

    public ApplicationModel() {}

    @ApiModelProperty("应用ID")
    private String applicationId;

    @ApiModelProperty("应用名称")
    private String name;

    @ApiModelProperty("启动时间")
    private String startTime;

    @ApiModelProperty("yarn 任务状态")
    private String yarnApplicationState;

    @ApiModelProperty("yarn 最终任务状态")
    private String finalApplicationStatus;

    @ApiModelProperty("flink任务状态")
    private String flinkStatus;

    @ApiModelProperty("flink任务UI")
    private String trackingUrl;

    @ApiModelProperty("已使用的containers")
    private Integer numUsedContainers;

    @ApiModelProperty("已使用的virtualCores")
    private Integer virtualCores;

    @ApiModelProperty("使用内存，单位MB")
    private Integer memory;

    @ApiModelProperty("运行时长")
    private String duration;

    public ApplicationModel(ApplicationReport app) {
        this.applicationId = app.getApplicationId().toString();
        this.name = app.getName();
        this.startTime = DateFormatUtils.format(app.getStartTime(),"yyyy-MM-dd HH:mm:ss");
        this.yarnApplicationState = app.getYarnApplicationState().toString();
        this.finalApplicationStatus = app.getFinalApplicationStatus().toString();
        this.trackingUrl = app.getTrackingUrl();
        this.numUsedContainers = app.getApplicationResourceUsageReport().getNumUsedContainers();
        this.virtualCores = app.getApplicationResourceUsageReport().getUsedResources().getVirtualCores();
        this.memory = app.getApplicationResourceUsageReport().getUsedResources().getMemory();
        this.duration = DateUtils.getDatePoor(new Date(System.currentTimeMillis()) , new Date(app.getStartTime()));
    }
}
