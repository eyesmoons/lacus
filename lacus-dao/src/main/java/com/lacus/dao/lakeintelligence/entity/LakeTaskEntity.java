package com.lacus.dao.lakeintelligence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 训练任务实体
 */
@Getter
@Setter
@TableName("lake_tasks")
public class LakeTaskEntity {

    @TableId(value = "task_id", type = IdType.AUTO)
    private Long taskId;

    /**
     * 任务名称
     */
    @TableField("task_name")
    private String taskName;

    /**
     * 任务类型: IMAGE_SIMILARITY
     */
    @TableField("task_type")
    private String taskType;

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
     * Python ML 端的任务 UUID
     */
    @TableField("ml_task_id")
    private String mlTaskId;

    /**
     * 该任务训练产出的模型文件路径
     */
    @TableField("model_path")
    private String modelPath;

    /**
     * 状态: PENDING/TRAINING/COMPLETED/FAILED/CANCELLED
     */
    @TableField("status")
    private String status;

    /**
     * 超参数配置 (JSON)
     */
    @TableField("hyper_params")
    private String hyperParams;

    /**
     * 训练进度百分比
     */
    @TableField("training_progress")
    private Integer trainingProgress;

    /**
     * 损失曲线数据 (JSON数组)
     */
    @TableField("loss_history")
    private String lossHistory;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 开始时间
     */
    @TableField("started_at")
    private Date startedAt;

    /**
     * 完成时间
     */
    @TableField("completed_at")
    private Date completedAt;

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
