package com.lacus.enums;

/**
 * 工作台统一状态五分类（跨引擎归一化后的展示口径）
 */
public enum UnifiedStatusEnum {
    SUCCESS, FAILED, RUNNING, WAITING, STOPPED;

    /** flink_job_instance.status（common FlinkStatusEnum 名） → 统一分类 */
    public static UnifiedStatusEnum fromFlink(String raw) {
        if (raw == null) {
            return STOPPED;
        }
        switch (raw) {
            case "FINISHED":
                return SUCCESS;
            case "FAILED":
                return FAILED;
            case "RUNNING":
            case "STARTING":
                return RUNNING;
            case "INITIALIZING":
            case "SCHEDULED":
            case "RESTARTING":
                return WAITING;
            default:
                return STOPPED;
        }
    }

    /** spark_job_instance.job_status（SparkStatusEnum 名） → 统一分类 */
    public static UnifiedStatusEnum fromSpark(String raw) {
        if (raw == null) {
            return STOPPED;
        }
        switch (raw) {
            case "FINISHED":
                return SUCCESS;
            case "FAILED":
            case "KILLED":
            case "LOST":
                return FAILED;
            case "RUNNING":
                return RUNNING;
            case "SUBMITTED":
            case "CONNECTED":
            case "CREATED":
                return WAITING;
            default:
                return STOPPED;
        }
    }

    /**
     * data_sync_job_instance.status → 统一分类。
     * rtc 词表为英文枚举名，历史数据可能残留数字串（DDL 注释：1 RUNNING / 2 KILL / 3 FAILED），两套词表都兼容。
     * 注意：rtc 自带分组把 FINISHED 归 STOP，这里必须归 SUCCESS，否则成功率恒为 0。
     */
    public static UnifiedStatusEnum fromDataSync(String raw) {
        if (raw == null || raw.isEmpty()) {
            return STOPPED;
        }
        switch (raw) {
            case "FINISHED":
                return SUCCESS;
            case "FAILING":
            case "FAILED":
            case "YARN_FAILED":
                return FAILED;
            case "RUNNING":
            case "INITIALIZING":
            case "CREATED":
                return RUNNING;
            case "NOINITIATED":
            case "RESTARTING":
                return WAITING;
            case "CANCELLING":
            case "CANCELED":
            case "PAUSE":
            case "STOP":
                return STOPPED;
            default:
                break;
        }
        switch (raw) {
            case "1":
                return RUNNING;
            case "2":
                return STOPPED;
            case "3":
                return FAILED;
            default:
                return STOPPED;
        }
    }

    /** st_job_instance.status（Integer：0 运行中 / 1 失败 / 2 成功） → 统一分类 */
    public static UnifiedStatusEnum fromDig(Integer raw) {
        if (raw == null) {
            return STOPPED;
        }
        switch (raw) {
            case 0:
                return RUNNING;
            case 1:
                return FAILED;
            case 2:
                return SUCCESS;
            default:
                return STOPPED;
        }
    }
}
