package com.lacus.dao.rtc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("data_sync_job_catalog")
public class DataSyncJobCatalogEntity extends BaseEntity<DataSyncJobCatalogEntity> {
    private static final long serialVersionUID = -255942356355408828L;

    @ApiModelProperty("主键")
    @TableId(value = "catalog_id", type = IdType.AUTO)
    private String catalogId;

    @ApiModelProperty("分组名称")
    @TableField("catalog_name")
    private String catalogName;

    @ApiModelProperty("jobManager内存，单位为GB")
    @TableField("job_manager")
    private Integer jobManager;

    @ApiModelProperty("taskManager内存，单位为GB")
    @TableField("task_manager")
    private Integer taskManager;

    @ApiModelProperty("分组描述")
    @TableField("remark")
    private String remark;
}
