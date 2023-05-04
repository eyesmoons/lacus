package com.lacus.dao.metadata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("meta_table")
public class MetaTableEntity extends BaseEntity<MetaTableEntity> {

    @ApiModelProperty("数据表ID")
    @TableId(value = "table_id", type = IdType.AUTO)
    private Long tableId;

    @ApiModelProperty("数据表名")
    @TableField("table_name")
    private String tableName;

    @ApiModelProperty("数据库ID")
    @TableField("db_id")
    private Long dbId;

    @ApiModelProperty("备注")
    @TableField("comment")
    private String comment;

    @ApiModelProperty("类型")
    @TableField("type")
    private String type;

    @ApiModelProperty("引擎")
    @TableField("engine")
    private String engine;

    @ApiModelProperty("表创建时间")
    @TableField("table_create_time")
    private Date tableCreateTime;

    @TableField(exist = false)
    private String dbName;
}
