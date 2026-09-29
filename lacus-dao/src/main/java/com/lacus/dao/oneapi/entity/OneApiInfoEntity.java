package com.lacus.dao.oneapi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("one_api_info")
@ApiModel(value = "OneApiInfoEntity对象", description = "api详情表")
public class OneApiInfoEntity extends BaseEntity<OneApiInfoEntity> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("接口ID")
    @TableId(value = "api_id", type = IdType.AUTO)
    private Long apiId;

    @ApiModelProperty("api名称")
    @TableField("api_name")
    private String apiName;

    @ApiModelProperty("接口url")
    @TableField("api_url")
    private String apiUrl;

    @ApiModelProperty("api分组id")
    @TableField("group_id")
    private Integer groupId;

    @ApiModelProperty("api类型")
    @TableField("api_type")
    private String apiType;

    @ApiModelProperty("数据源id")
    @TableField("datasource_id")
    private Long datasourceId;

    @TableField(exist = false)
    private String datasourceName;

    @ApiModelProperty("超时时间，默认3秒，最大10s")
    @TableField("query_timeout")
    private Integer queryTimeout;

    @ApiModelProperty("请求方式")
    @TableField("req_method")
    private String reqMethod;

    @ApiModelProperty("最大返回条数")
    @TableField("limit_count")
    private Integer limitCount;

    @ApiModelProperty("接口描述")
    @TableField("api_desc")
    private String apiDesc;

    @ApiModelProperty("api配置")
    @TableField("api_config")
    private String apiConfig;

    @ApiModelProperty("接口状态：0未发布状态，1发布状态")
    @TableField("`status`")
    private Integer status;

    @ApiModelProperty("api响应结果")
    @TableField("api_response")
    private String apiResponse;

    @ApiModelProperty("是否为在线编辑，0：下线编辑，1：在线编辑")
    @TableField("online_edit")
    private Boolean onlineEdit;


    @Override
    public Serializable pkVal() {
        return this.apiId;
    }

}
