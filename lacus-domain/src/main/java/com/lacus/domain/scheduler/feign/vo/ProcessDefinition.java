package com.lacus.domain.scheduler.feign.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ProcessDefinition implements Serializable {
    private static final long serialVersionUID = -3085982558511050899L;

    private Integer id;

    private Long code;

    private String name;

    private Integer version;

    private String releaseState;

    private String projectId;

    private String processDefinitionJson;

    private String description;

    private String globalParams;

    private String globalParamList;

    private String globalParamMap;

    private Date createTime;

    private Date updateTime;

    private String flag;

    private Integer userId;

    private String locations;

    private String connects;

    private Integer timeout;

    private Integer tenantId;

    private String modifyBy;

    private String resourceIds;
}
