package com.lacus.domain.lakeintelligence.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.system.query.AbstractPageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型分页查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ModelPageQuery extends AbstractPageQuery {

    @ApiModelProperty("模型名称（模糊查询）")
    private String modelName;

    @ApiModelProperty("模型架构")
    private String modelArch;

    @ApiModelProperty("关联图片库ID")
    private Long datasetId;

    @ApiModelProperty("创建者ID")
    private String creatorId;

    @Override
    public QueryWrapper toQueryWrapper() {
        QueryWrapper wrapper = new QueryWrapper();
        if (modelName != null && !modelName.isEmpty()) {
            wrapper.like("model_name", modelName);
        }
        if (modelArch != null && !modelArch.isEmpty()) {
            wrapper.eq("model_arch", modelArch);
        }
        if (datasetId != null) {
            wrapper.eq("dataset_id", datasetId);
        }
        if (creatorId != null && !creatorId.isEmpty()) {
            wrapper.eq("creator_id", creatorId);
        }
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("create_time");
        return wrapper;
    }
}
