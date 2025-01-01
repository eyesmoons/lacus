package com.lacus.dao.dataserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("dataserver_api_info")
public class DataServerEntity extends BaseEntity<DataServerEntity> {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("接口id")
    @TableId(value = "api_id", type = IdType.AUTO)
    private Long apiId;

    @TableField("api_name")
    @ApiModelProperty("接口名称")
    private String apiName;

    @ApiModelProperty("接口驱动类型:Mysql,Doris,oracle...")
    @TableField("driver_type")
    private String driverType;

    @ApiModelProperty("数据源id")
    @TableField("datasource_id")
    private Long datasourceId;

    @ApiModelProperty("接口Url")
    @TableField("api_url")
    private String apiUrl;

    @ApiModelProperty("接口描述")
    @TableField("api_desc")
    private String apiDesc;

    @ApiModelProperty("接口请求方式")
    @TableField("request_method")
    private String requestMethod;

    @ApiModelProperty("接口所属项目组")
    @TableField("project_team")
    private String projectTeam;

    @ApiModelProperty("接口超时时间")
    @TableField("query_timeout")
    private Integer queryTimeout;

    @ApiModelProperty("是否开启限流，不开启0,开启1,默认为0")
    @TableField("current_limit")
    private Integer currentLimit;

    @ApiModelProperty("接口状态,下线为0,上线为1，默认为0")
    @TableField("api_status")
    private Integer apiStatus;

    @ApiModelProperty("接口最大返回行数")
    @TableField("max_return_rows")
    private Long maxReturnRows;

    @ApiModelProperty("接口脚本")
    @TableField("api_script")
    private String apiScript;


}
