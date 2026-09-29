package com.lacus.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("one_api_info")
public class ApiInfoEntity {

    @TableId(value = "api_id", type = IdType.AUTO)
    private Long api_id;

    /**
     * api名称
     */
    @TableField("api_name")
    private String apiName;

    /**
     * 接口url
     */
    @TableField("api_url")
    private String apiUrl;

    /**
     * 接口类型
     */
    @TableField("api_type")
    private String apiType;

    /**
     * api分组名
     */
    @TableField("group_id")
    private String groupId;

    /**
     * 数据源id
     */
    @TableField("datasource_id")
    private Long datasourceId;

    /**
     * 超时时间，默认3秒，最大10s
     */
    @TableField("query_timeout")
    private Integer queryTimeout;

    /**
     * 请求方式
     */
    @TableField("req_method")
    private String reqMethod;

    /**
     * 最大返回条数
     */
    @TableField("limit_count")
    private Integer limitCount;

    /**
     * 接口描述
     */
    @TableField("api_desc")
    private String apiDesc;

    /**
     * api配置
     */
    @TableField("api_config")
    private String apiConfig;

    /**
     * 接口状态：0未发布状态，1发布状态
     */
    @TableField("status")
    private Integer status;

    /**
     * api响应结果
     */
    @TableField("api_response")
    private String apiResponse;

    /**
     * 是否为在线编辑，0：下线编辑，1：在线编辑
     */
    @TableField("online_edit")
    private Integer onlineEdit;

    @TableField("deleted")
    private Integer deleted;

    @TableField("creator_id")
    private String creatorId;

    @TableField("create_time")
    private Date createTime;

    @TableField("updater_id")
    private String updaterId;

    @TableField("update_time")
    private Date updateTime;
}
