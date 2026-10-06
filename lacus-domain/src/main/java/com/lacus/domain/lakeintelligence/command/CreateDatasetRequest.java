package com.lacus.domain.lakeintelligence.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建数据集请求命令
 */
@Data
public class CreateDatasetRequest {

    @ApiModelProperty(value = "数据集名称", required = true)
    @NotBlank(message = "数据集名称不能为空")
    private String datasetName;

    @ApiModelProperty("数据集描述")
    private String description;

    @ApiModelProperty(value = "存储来源: LOCAL/HDFS/S3/MINIO/HTTP", required = true)
    @NotBlank(message = "存储来源不能为空")
    private String storageSource;

    @ApiModelProperty(value = "数据源配置 (JSON)", required = true)
    @NotNull(message = "数据源配置不能为空")
    private String sourceConfig;

    @ApiModelProperty(value = "任务类型: SIMILARITY/CLASSIFICATION")
    private String taskType = "SIMILARITY";

    @ApiModelProperty(value = "初始状态: PROCESSING/READY (上传解析后可直接设为 READY)")
    private String status;

    @ApiModelProperty(value = "数据集大小（数据条数）")
    private Integer imageCount;

    @ApiModelProperty(value = "创建者ID")
    private String creatorId;
}
