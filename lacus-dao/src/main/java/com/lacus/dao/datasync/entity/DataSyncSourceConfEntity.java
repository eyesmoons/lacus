package com.lacus.dao.datasync.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("data_sync_source_conf")
public class DataSyncSourceConfEntity extends BaseEntity<DataSyncSourceConfEntity> {

    @ApiModelProperty("主键")
    @TableId(value = "source_conf_id", type = IdType.AUTO)
    private Long sourceConfId;

    @ApiModelProperty("配置名称")
    @TableField("source_conf_name")
    private String sourceConfName;

    @ApiModelProperty("数据源ID")
    @TableField("datasource_id")
    private Long datasourceId;

    @ApiModelProperty("输出源配置脚本")
    @TableField("source_conf")
    private String sourceConf;
}
