package com.lacus.domain.lakeintelligence.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.system.query.AbstractPageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskPageQuery extends AbstractPageQuery {

    @ApiModelProperty("任务名称（模糊查询）")
    private String taskName;

    @ApiModelProperty("任务状态: PENDING/TRAINING/COMPLETED/FAILED/CANCELLED")
    private String status;

    @ApiModelProperty("任务类型: SIMILARITY/CLASSIFICATION")
    private String taskType;

    @ApiModelProperty("关联模型 ID")
    private Long modelId;

    @Override
    public QueryWrapper toQueryWrapper() {
        QueryWrapper wrapper = new QueryWrapper();
        if (taskName != null && !taskName.isEmpty()) {
            wrapper.like("task_name", taskName);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        if (taskType != null && !taskType.isEmpty()) {
            wrapper.eq("task_type", taskType);
        }
        if (modelId != null) {
            wrapper.eq("model_id", modelId);
        }
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("create_time");
        return wrapper;
    }
}
