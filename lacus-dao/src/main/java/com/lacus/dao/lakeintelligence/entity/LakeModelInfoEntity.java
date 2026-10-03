package com.lacus.dao.lakeintelligence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 模型元信息实体
 */
@Getter
@Setter
@TableName("lake_model_info")
public class LakeModelInfoEntity {

    @TableId(value = "model_id", type = IdType.AUTO)
    private Long modelId;

    /**
     * 模型名称
     */
    @TableField("model_name")
    private String modelName;

    /**
     * 关联训练任务ID
     */
    @TableField("task_id")
    private Long taskId;

    /**
     * 关联图片库ID
     */
    @TableField("dataset_id")
    private Long datasetId;

    /**
     * 模型架构
     */
    @TableField("model_arch")
    private String modelArch;

    /**
     * 模型文件本地路径
     */
    @TableField("model_path")
    private String modelPath;

    /**
     * 模型文件大小(字节)
     */
    @TableField("model_size_bytes")
    private Long modelSizeBytes;

    /**
     * Embedding维度
     */
    @TableField("embedding_dim")
    private Integer embeddingDim;

    /**
     * 实际训练轮数
     */
    @TableField("training_epochs")
    private Integer trainingEpochs;

    /**
     * 最终损失值
     */
    @TableField("final_loss")
    private BigDecimal finalLoss;

    /**
     * 关联向量库ID
     */
    @TableField("vector_index_id")
    private Long vectorIndexId;

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
