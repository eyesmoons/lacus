package com.lacus.domain.lakeintelligence.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.system.query.AbstractPageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 训练任务分页查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskPageQuery extends AbstractPageQuery {

    @ApiModelProperty("任务名称（模糊查询）")
    private String taskName;

    @ApiModelProperty("任务类型: IMAGE_SIMILARITY")
    private String taskType;

    @ApiModelProperty("关联图片库ID")
    private Long datasetId;

    @ApiModelProperty("状态: PENDING/TRAINING/COMPLETED/FAILED/CANCELLED")
    private String status;

    @ApiModelProperty("创建者ID")
    private String creatorId;

    @Override
    public QueryWrapper toQueryWrapper() {
        QueryWrapper wrapper = new QueryWrapper();
        if (taskName != null && !taskName.isEmpty()) {
            wrapper.like("task_name", taskName);
        }
        if (taskType != null && !taskType.isEmpty()) {
            wrapper.eq("task_type", taskType);
        }
        if (datasetId != null) {
            wrapper.eq("dataset_id", datasetId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        if (creatorId != null && !creatorId.isEmpty()) {
            wrapper.eq("creator_id", creatorId);
        }
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("create_time");
        return wrapper;
    }
}
