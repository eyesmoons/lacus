package com.lacus.domain.lakeintelligence.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.system.query.AbstractPageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据集分页查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DatasetPageQuery extends AbstractPageQuery {

    @ApiModelProperty("数据集名称（模糊查询）")
    private String datasetName;

    @ApiModelProperty("存储来源: LOCAL/HDFS/S3/MINIO/HTTP")
    private String storageSource;

    @ApiModelProperty("状态: PROCESSING/WAITING_DOWNLOAD/DOWNLOADING/READY/ERROR")
    private String status;

    @ApiModelProperty("创建者ID")
    private String creatorId;

    @ApiModelProperty("任务类型: SIMILARITY/CLASSIFICATION")
    private String taskType;

    @Override
    public QueryWrapper toQueryWrapper() {
        QueryWrapper wrapper = new QueryWrapper();
        if (datasetName != null && !datasetName.isEmpty()) {
            wrapper.like("dataset_name", datasetName);
        }
        if (storageSource != null && !storageSource.isEmpty()) {
            wrapper.eq("storage_source", storageSource);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        if (creatorId != null && !creatorId.isEmpty()) {
            wrapper.eq("creator_id", creatorId);
        }
        if (taskType != null && !taskType.isEmpty()) {
            wrapper.eq("task_type", taskType);
        }
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("create_time");
        return wrapper;
    }
}
