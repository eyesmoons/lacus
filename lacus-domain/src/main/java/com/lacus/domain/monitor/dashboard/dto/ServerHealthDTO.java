package com.lacus.domain.monitor.dashboard.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * 服务器健康摘要（从 ServerInfo 提取的轻量字段）
 */
@Data
public class ServerHealthDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** CPU 使用率 % */
    private Double cpuUsage;

    /** 已用内存 G */
    private Double memoryUsed;

    /** 总内存 G */
    private Double memoryTotal;

    /** JVM 已用内存 M */
    private Double jvmUsed;

    /** JVM 最大内存 M */
    private Double jvmMax;
}
