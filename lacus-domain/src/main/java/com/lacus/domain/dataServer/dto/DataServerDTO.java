package com.lacus.domain.dataServer.dto;

import com.lacus.core.cache.CacheCenter;
import com.lacus.dao.dataserver.entity.DataServerEntity;
import com.lacus.dao.dataserver.enums.DataServerStatusEnum;
import com.lacus.dao.dataserver.enums.DriverTypeEnum;
import com.lacus.dao.system.entity.SysUserEntity;
import com.lacus.enums.interfaces.BasicEnumUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class DataServerDTO {


    @ApiModelProperty("接口名称")
    private String apiName;

    @ApiModelProperty("接口驱动类型:Mysql,Doris,oracle...")
    private String driverTypeName;

    @ApiModelProperty("接口Url")
    private String apiUrl;

    @ApiModelProperty("接口描述")
    private String apiDesc;

    @ApiModelProperty("接口状态,下线为0,上线为1，默认为0")
    private String apiStatusName;

    @ApiModelProperty("接口请求方式")
    private String requestMethod;

    @ApiModelProperty("接口所属项目组")
    private String projectTeam;

    @ApiModelProperty("接口超时时间")
    private Integer queryTimeout;

    @ApiModelProperty("是否开启限流")
    private String currentLimitName;

    @ApiModelProperty("接口最大返回行数")
    private Long maxReturnRows;

    @ApiModelProperty("数据源id")
    private Long datasourceId;

    @ApiModelProperty("接口脚本")
    private String apiScript;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date lastModifyTime;


    public DataServerDTO(DataServerEntity entity) {
        this.apiName = entity.getApiName();
        this.driverTypeName = BasicEnumUtil.getDescriptionByValue(DriverTypeEnum.class, entity.getDriverType());
        this.apiUrl = entity.getApiUrl();
        this.apiDesc = entity.getApiDesc();
        this.requestMethod = entity.getRequestMethod();
        this.projectTeam = entity.getProjectTeam();
        this.queryTimeout = entity.getQueryTimeout();
        this.currentLimitName = BasicEnumUtil.getDescriptionByValue(DataServerStatusEnum.class, entity.getCurrentLimit());
        this.maxReturnRows = entity.getMaxReturnRows();
        this.datasourceId = entity.getDatasourceId();
        this.apiScript = entity.getApiScript();
        this.apiStatusName = BasicEnumUtil.getDescriptionByValue(DataServerStatusEnum.class, entity.getApiStatus());
        this.createTime = entity.getCreateTime();
        SysUserEntity creator = CacheCenter.userCache.getObjectById(entity.getCreatorId());
        if (creator != null) {
            this.creator = creator.getUsername();
        }
        this.lastModifyTime = entity.getUpdateTime();
        if (creator != null) {
            this.modifier = creator.getUsername();
        }
    }
}
