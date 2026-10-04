package com.lacus.dao.lakeintelligence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 向量库索引实体
 */
@Getter
@Setter
@TableName("lake_vector_indexes")
public class LakeVectorIndexEntity {

    @TableId(value = "index_id", type = IdType.AUTO)
    private Long indexId;

    /**
     * 向量库名称
     */
    @TableField("index_name")
    private String indexName;

    /**
     * 关联图片库ID
     */
    @TableField("dataset_id")
    private Long datasetId;

    /**
     * 关联模型ID
     */
    @TableField("model_id")
    private Long modelId;

    /**
     * 向量索引本地路径
     */
    @TableField("index_path")
    private String indexPath;

    /**
     * 向量总数
     */
    @TableField("total_vectors")
    private Integer totalVectors;

    /**
     * 向量维度
     */
    @TableField("dimension")
    private Integer dimension;

    /**
     * 距离度量: cosine/euclidean
     */
    @TableField("distance_metric")
    private String distanceMetric;

    /**
     * 构建状态: PENDING/BUILDING/COMPLETED/FAILED
     */
    @TableField("build_status")
    private String buildStatus;

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
