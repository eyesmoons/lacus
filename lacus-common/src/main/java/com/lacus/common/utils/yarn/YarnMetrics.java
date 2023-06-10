package com.lacus.common.utils.yarn;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "yarn监控信息")
public class YarnMetrics {

    @ApiModelProperty(value = "可用节点")
    private Integer activeNodes;

    @ApiModelProperty(value = "总内存MB")
    private Integer totalMB;

    @ApiModelProperty(value = "总内存GB")
    private Integer totalGB;

    @ApiModelProperty(value = "可用内存MB")
    private Integer availableMB;

    @ApiModelProperty(value = "可用内存GB")
    private Integer availableGB;

    @ApiModelProperty(value = "已使用内存MB")
    private Integer allocatedMB;

    @ApiModelProperty(value = "已使用内存GB")
    private Integer allocatedGB;

    /**内存使用率*/
    @ApiModelProperty(value = "内存使用率")
    private String memoryPercents;

    @ApiModelProperty(value = "可用vCore")
    private Integer availableVirtualCores;

    @ApiModelProperty(value = "总vCore")
    private Integer totalVirtualCores;

    @ApiModelProperty(value = "已使用vCore")
    private Integer allocatedVirtualCores;

    /**vcore使用率*/
    @ApiModelProperty(value = "vCore使用率")
    private String virtualCoresPercents;

    /**节点地址*/
    @ApiModelProperty(value = "节点地址")
    private String yarnNode;

    /**运行任务*/
    @ApiModelProperty(value = "运行任务")
    private Integer runningJobs;

    /**失败任务*/
    @ApiModelProperty(value = "失败任务")
    private Integer failJobs;

    /**总任务*/
    @ApiModelProperty(value = "总任务")
    private Integer totalJobs;

}
