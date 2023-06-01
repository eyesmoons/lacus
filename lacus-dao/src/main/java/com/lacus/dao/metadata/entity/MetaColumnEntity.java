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
@TableName("meta_column")
public class MetaColumnEntity extends BaseEntity<MetaColumnEntity> {

    @ApiModelProperty("字段ID")
    @TableId(value = "column_id", type = IdType.AUTO)
    private Long columnId;

    @ApiModelProperty("字段名")
    @TableField("column_name")
    private String columnName;

    @ApiModelProperty("数据表ID")
    @TableField("table_id")
    private Long tableId;

    @ApiModelProperty("数据类型")
    @TableField("data_type")
    private String dataType;

    @ApiModelProperty("字段类型")
    @TableField("column_type")
    private String columnType;

    @ApiModelProperty("numeric_precision")
    @TableField("numeric_precision")
    private Long numericPrecision;

    @ApiModelProperty("numeric_scale")
    @TableField("numeric_scale")
    private Long numericScale;

    @ApiModelProperty("字段长度")
    @TableField("column_length")
    private Long columnLength;

    @ApiModelProperty("备注")
    @TableField("comment")
    private String comment;

    @ApiModelProperty("是否非空")
    @TableField("is_nullable")
    private String isNullable;

    @ApiModelProperty("字段默认值")
    @TableField("column_default")
    private String columnDefault;
}
