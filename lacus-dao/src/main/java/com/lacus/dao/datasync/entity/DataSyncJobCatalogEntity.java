package com.lacus.dao.datasync.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("data_sync_job_catalog")
public class DataSyncJobCatalogEntity extends BaseEntity<DataSyncJobCatalogEntity> {

    @ApiModelProperty("主键")
    @TableId(value = "catalog_id", type = IdType.AUTO)
    private Long catalogId;

    @ApiModelProperty("分组名称")
    @TableField("catalog_name")
    private String catalogName;

    @ApiModelProperty("jobManager内存，单位为GB")
    @TableField("job_manager")
    private Long jobManager;

    @ApiModelProperty("taskManager内存，单位为GB")
    @TableField("task_manager")
    private Long taskManager;

    @ApiModelProperty("分组描述")
    @TableField("remark")
    private String remark;
}
