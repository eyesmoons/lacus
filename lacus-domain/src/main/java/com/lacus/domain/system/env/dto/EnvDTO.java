package com.lacus.domain.system.env.dto;

import com.lacus.dao.system.entity.SysEnvEntity;
import lombok.Data;

import java.util.Date;

/**
 * @author shengyu
 * @date 2024/4/30 17:34
 */
@Data
public class EnvDTO {
    private Long envId;
    private String name;
    private String config;
    private String remark;
    private Date createTime;

    public EnvDTO(SysEnvEntity entity) {
        this.envId = entity.getEnvId();
        this.name = entity.getName();
        this.config = entity.getConfig();
        this.remark = entity.getRemark();
        this.createTime = entity.getCreateTime();
    }
}