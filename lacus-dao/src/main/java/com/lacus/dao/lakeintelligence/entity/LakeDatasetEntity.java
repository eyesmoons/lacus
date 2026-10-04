package com.lacus.dao.lakeintelligence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 图片库元信息实体
 */
@Getter
@Setter
@TableName("lake_datasets")
public class LakeDatasetEntity {

    @TableId(value = "dataset_id", type = IdType.AUTO)
    private Long datasetId;

    /**
     * 图片库名称
     */
    @TableField("dataset_name")
    private String datasetName;

    /**
     * 图片库描述
     */
    @TableField("description")
    private String description;

    /**
     * 存储来源: LOCAL/HDFS/S3/MINIO/HTTP
     */
    @TableField("storage_source")
    private String storageSource;

    /**
     * 数据源配置 (JSON)
     */
    @TableField("source_config")
    private String sourceConfig;

    /**
     * 任务类型: IMAGE_SIMILARITY/IMAGE_CLASSIFICATION
     */
    @TableField("task_type")
    private String taskType;

    /**
     * 状态: PROCESSING/WAITING_DOWNLOAD/DOWNLOADING/READY/ERROR
     */
    @TableField("status")
    private String status;

    /**
     * 图片数量
     */
    @TableField("image_count")
    private Integer imageCount;

    /**
     * 总文件大小(字节)
     */
    @TableField("total_size_bytes")
    private Long totalSizeBytes;

    /**
     * 本地存储路径
     */
    @TableField("local_path")
    private String localPath;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

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
}
