package com.lacus.dao.metadata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("lineage_node")
public class LineageNodeEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "node_id", type = IdType.AUTO)
    private Long nodeId;

    @ApiModelProperty("关联元数据表ID（meta_table.table_id）")
    @TableField("table_id")
    private Long tableId;

    @ApiModelProperty("节点名称（表名）")
    @TableField("node_name")
    private String nodeName;

    @ApiModelProperty("节点类型：TABLE-普通表，VIRTUAL_TABLE-虚拟表，DATASOURCE-数据源")
    @TableField("node_type")
    private String nodeType;

    @ApiModelProperty("所属数据源ID")
    @TableField("datasource_id")
    private Long datasourceId;

    @ApiModelProperty("所属库名称快照")
    @TableField("db_name")
    private String dbName;

    @ApiModelProperty("所属数据源名称快照（非表字段，返回前端展示）")
    @TableField(exist = false)
    private String datasourceName;

    @ApiModelProperty("创建人")
    @TableField("creator_id")
    private String creatorId;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty("修改人")
    @TableField("updater_id")
    private String updaterId;

    @ApiModelProperty("更新时间")
    @TableField("update_time")
    private Date updateTime;
}