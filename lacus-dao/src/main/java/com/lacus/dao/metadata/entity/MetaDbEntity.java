package com.lacus.dao.metadata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("meta_db")
public class MetaDbEntity extends BaseEntity<MetaDbEntity> {

    @ApiModelProperty("数据库ID")
    @TableId(value = "db_id", type = IdType.AUTO)
    private Long dbId;

    @ApiModelProperty("数据库名")
    @TableField("db_name")
    private String dbName;

    @ApiModelProperty("数据源ID")
    @TableField("datasource_id")
    private Long datasourceId;

    @ApiModelProperty("备注")
    @TableField("comment")
    private String comment;
}
