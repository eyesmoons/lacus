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
@TableName("meta_datasource_type")
public class MetaDatasourceTypeEntity extends BaseEntity<MetaDatasourceTypeEntity> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "type_id", type = IdType.AUTO)
    private Long typeId;

    @TableField("type_name")
    private String typeName;

    @TableField("type_code")
    private String typeCode;

    @TableField("type_catalog")
    private String typeCatalog;

    @TableField("driver_name")
    private String driverName;

    @TableField("icon")
    private String icon;

    @TableField("jdbc_url")
    private String jdbcUrl;

    @TableField("remark")
    private String remark;
}
