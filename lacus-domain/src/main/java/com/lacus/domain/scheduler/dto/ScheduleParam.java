package com.lacus.domain.scheduler.dto;

import java.util.Date;

public class ScheduleParam {
    private Date startTime;
    private Date endTime;
    private String crontab;
    private String timezoneId;
    private String tenantCode;
    private String failureStrategy;

    public ScheduleParam() {
    }

    public ScheduleParam(Date startTime, Date endTime, String crontab) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.crontab = crontab;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getFailureStrategy() {
        return failureStrategy;
    }

    public void setFailureStrategy(String failureStrategy) {
        this.failureStrategy = failureStrategy;
    }

    public String getTimezoneId() {
        return timezoneId;
    }

    public void setTimezoneId(String timezoneId) {
        this.timezoneId = timezoneId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getCrontab() {
        return crontab;
    }

    public void setCrontab(String crontab) {
        this.crontab = crontab;
    }


    @Override
    public String toString() {
        return "ScheduleParam{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", crontab='" + crontab + '\'' +
                '}';
    }
}
