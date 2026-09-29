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
@TableName("business_metadata")
public class BusinessMetadataEntity extends BaseEntity<BusinessMetadataEntity> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("业务对象类型：DATASOURCE-数据源，DB-数据库，TABLE-表，COLUMN-字段")
    @TableField("biz_type")
    private String bizType;

    @ApiModelProperty("业务对象ID；DB级为 datasourceId:dbName，其余为对应记录主键")
    @TableField("biz_id")
    private String bizId;

    @ApiModelProperty("属性键：businessName/description/owner/tags")
    @TableField("obj_key")
    private String objKey;

    @ApiModelProperty("属性值")
    @TableField("obj_value")
    private String objValue;
}