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
@TableName("meta_datasource")
public class MetaDatasourceEntity extends BaseEntity<MetaDatasourceEntity> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "datasource_id", type = IdType.AUTO)
    private Long datasourceId;

    @ApiModelProperty("数据源名称")
    @TableField("datasource_name")
    private String datasourceName;

    @ApiModelProperty("数据源类型: mysql，doris")
    @TableField("type")
    private String type;

    @ApiModelProperty("同步类型：1输入源，2输出源")
    @TableField("source_type")
    private Integer sourceType;

    @ApiModelProperty("数据源描述")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("ip/主机名")
    @TableField("ip")
    private String ip;

    @ApiModelProperty("端口")
    @TableField("port")
    private Integer port;

    @ApiModelProperty("用户名")
    @TableField("username")
    private String username;

    @ApiModelProperty("密码")
    @TableField("password")
    private String password;

    @ApiModelProperty("默认数据库名")
    @TableField("default_db_name")
    private String defaultDbName;

    @ApiModelProperty("数据源状态：启用 1，禁用 0")
    @TableField("status")
    private Integer status;

    @ApiModelProperty("参数参数")
    @TableField("connection_params")
    private String connectionParams;
}
