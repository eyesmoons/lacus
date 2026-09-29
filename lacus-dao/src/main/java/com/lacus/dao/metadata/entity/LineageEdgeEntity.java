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
@TableName("lineage_edge")
public class LineageEdgeEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "edge_id", type = IdType.AUTO)
    private Long edgeId;

    @ApiModelProperty("源节点ID（上游/数据流出方）")
    @TableField("source_node_id")
    private Long sourceNodeId;

    @ApiModelProperty("目标节点ID（下游/数据流入方）")
    @TableField("target_node_id")
    private Long targetNodeId;

    @ApiModelProperty("依赖类型：DIRECT-直接依赖，TRANSFORM-转换依赖")
    @TableField("dep_type")
    private String depType;

    @ApiModelProperty("登记来源：AUTO-自动解析，MANUAL-手动登记")
    @TableField("source_flag")
    private String sourceFlag;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

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