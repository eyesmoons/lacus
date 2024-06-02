package com.lacus.domain.dataserver.query;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.utils.strings.StringUtil;
import com.lacus.dao.dataserver.entity.DataServerEntity;
import com.lacus.dao.system.query.AbstractPageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataServerQuery extends AbstractPageQuery {



    @ApiModelProperty("接口名称")
    private String apiName;

    @ApiModelProperty("接口Url")
    private String apiUrl;

    @ApiModelProperty("接口所属项目组")
    private String projectTeam;

    @ApiModelProperty("api状态")
    private Integer apiStatus;


    @Override
    public QueryWrapper<DataServerEntity> toQueryWrapper() {
        QueryWrapper<DataServerEntity> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtil.checkValNotNull(apiName), "api_name", apiName);
        wrapper.like(StringUtil.checkValNotNull(apiUrl), "api_url", apiUrl);
        wrapper.like(StringUtil.checkValNotNull(projectTeam), "project_team", projectTeam);
        wrapper.eq(Objects.nonNull(apiStatus), "api_status", apiStatus);
        return wrapper;
    }
}