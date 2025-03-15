package com.lacus.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiInfoDTO {

    /**
     * 接口ID
     */
    @JsonIgnore
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
     * api分组名
     */
    private String groupId;


    private String apiType;

    /**
     * 数据源id
     */
    private Long datasourceId;

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

}
