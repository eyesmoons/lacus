package com.lacus.domain.dataquality.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.dataquality.entity.DqExecutionLogEntity;
import com.lacus.dao.system.query.AbstractPageQuery;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 执行记录分页查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DqExecutionLogQuery extends AbstractPageQuery {

    @ApiModelProperty("规则ID")
    private Long ruleId;

    @ApiModelProperty("规则名称（模糊查询）")
    private String ruleName;

    @ApiModelProperty("执行状态: SUBMITTED/RUNNING/SUCCESS/FAILED/STOPPED")
    private String status;

    @ApiModelProperty("开始时间（起）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTimeBegin;

    @ApiModelProperty("开始时间（止）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTimeEnd;

    @Override
    public QueryWrapper<DqExecutionLogEntity> toQueryWrapper() {
        QueryWrapper<DqExecutionLogEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(ObjectUtils.isNotEmpty(ruleId), "rule_id", ruleId);
        wrapper.like(ObjectUtils.isNotEmpty(ruleName), "rule_name", ruleName);
        wrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        wrapper.ge(ObjectUtils.isNotEmpty(startTimeBegin), "start_time", startTimeBegin);
        wrapper.le(ObjectUtils.isNotEmpty(startTimeEnd), "start_time", startTimeEnd);
        wrapper.orderByDesc("id");
        return wrapper;
    }
}
