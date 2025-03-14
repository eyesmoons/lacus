package com.lacus.domain.oneapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApiInfoDTO {


    /**
     * 接口ID
     */
    private Long id;


    /**
     * api名称
     */
    private String apiName;

    /**
     * 接口url
     */
    private String apiUrl;

    /**
     * api分组id
     */
    private Long groupId;

    /**
     * 分组名
     */
    private String groupName;

    /**
     * 数据源id
     */
    private Long datasourceId;


    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 超时时间，默认3秒，最大10s
     */
    private Integer queryTimeout;

    /**
     * 请求方式
     */
    private String reqMethod;

    /**
     * 最大返回条数
     */
    private Integer limitCount;

    /**
     * 接口描述
     */
    private String apiDesc;

    /**
     * api配置
     */
    private String apiConfig;

    /**
     * 接口状态：0未发布状态，1发布状态
     */
    private Integer status;

    /**
     * api响应结果
     */
    private String apiResponse;

    /**
     * 是否为在线编辑，0：下线编辑，1：在线编辑
     */
    private Integer onlineEdit;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人id
     */
    private Long createUserId;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 修改人id
     */
    private Long updateUserId;

    /**
     * 修改人姓名
     */
    private String updateUserName;

}
