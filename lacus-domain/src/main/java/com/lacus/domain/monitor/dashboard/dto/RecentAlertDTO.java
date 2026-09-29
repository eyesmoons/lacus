package com.lacus.domain.monitor.dashboard.dto;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 最近告警条目
 */
@Data
public class RecentAlertDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /** 告警记录编号 */
    private String recordNo;

    /** 告警标题 */
    private String title;

    /** 告警级别 */
    private String alertLevel;

    /** 告警组名称 */
    private String groupName;

    /** 触发时间 */
    private Date requestedTime;
}
