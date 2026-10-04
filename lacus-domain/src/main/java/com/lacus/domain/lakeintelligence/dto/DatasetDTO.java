package com.lacus.domain.lakeintelligence.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 数据集 DTO
 */
@Data
public class DatasetDTO {

    @ApiModelProperty("数据集ID")
    private Long datasetId;

    @ApiModelProperty("数据集名称")
    private String datasetName;

    @ApiModelProperty("数据集描述")
    private String description;

    @ApiModelProperty("存储来源: LOCAL/HDFS/S3/MINIO/HTTP")
    private String storageSource;

    @ApiModelProperty("数据源配置 (JSON)")
    private String sourceConfig;

    @ApiModelProperty("任务类型: IMAGE_SIMILARITY/IMAGE_CLASSIFICATION")
    private String taskType;

    @ApiModelProperty("状态: PROCESSING/WAITING_DOWNLOAD/DOWNLOADING/READY/ERROR")
    private String status;

    @ApiModelProperty("图片数量")
    private Integer imageCount;

    @ApiModelProperty("总文件大小(字节)")
    private Long totalSizeBytes;

    @ApiModelProperty("本地存储路径")
    private String localPath;

    @ApiModelProperty("错误信息")
    private String errorMessage;

    @ApiModelProperty("创建者ID")
    private String creatorId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}
