package com.lacus.domain.scheduler.feign.vo;

import com.lacus.enums.FailureStrategy;
import com.lacus.enums.Priority;
import com.lacus.enums.ReleaseState;
import com.lacus.enums.WarningType;
import lombok.Data;

import java.util.Date;

@Data
public class ScheduleVO {

    private int id;

    /**
     * process definition code
     */
    private long processDefinitionCode;

    /**
     * process definition name
     */
    private String processDefinitionName;

    /**
     * project name
     */
    private String projectName;

    /**
     * schedule description
     */
    private String definitionDescription;

    /**
     * schedule start time
     */
    private String startTime;

    /**
     * schedule end time
     */
    private String endTime;

    /**
     * timezoneId
     * <p>see {@link java.util.TimeZone#getTimeZone(String)}
     */
    private String timezoneId;

    /**
     * crontab expression
     */
    private String crontab;

    /**
     * failure strategy
     */
    private FailureStrategy failureStrategy;

    /**
     * warning type
     */
    private WarningType warningType;

    /**
     * create time
     */
    private Date createTime;

    /**
     * update time
     */
    private Date updateTime;

    /**
     * created user id
     */
    private int userId;

    /**
     * created user name
     */
    private String userName;

    /**
     * release state
     */
    private ReleaseState releaseState;

    /**
     * warning group id
     */
    private int warningGroupId;

    /**
     * process instance priority
     */
    private Priority processInstancePriority;

    /**
     * worker group
     */
    private String workerGroup;

    /**
     * tenantCode
     */
    private String tenantCode;

    /**
     * environment code
     */
    private Long environmentCode;

    /**
     * environment name
     */
    private String environmentName;

}
