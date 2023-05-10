package com.lacus.dao.datasync.enums;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum FlinkStatusEnum {

    /**
     * The job has been received by the Dispatcher, and is waiting for the job manager to be created.
     */
    INITIALIZING("INITIALIZING",1,"RUNNING"),

    /** Job is newly created, no task has started to run. */
    CREATED("CREATED",1,"RUNNING"),

    /** Some tasks are scheduled or running, some may be pending, some may be finished. */
    RUNNING("RUNNING",1,"RUNNING"),

    /** The job has failed and is currently waiting for the cleanup to complete. */
    FAILING("FAILING",2,"FAIL"),

    /** The job has failed with a non-recoverable task failure. */
    FAILED("FAILED",2,"FAIL"),

    /** The job has failed with a non-recoverable task failure. */
    YARN_FAILED("YARN_FAILED",-1,"FAIL"),

    /** Job is being cancelled. */
    CANCELLING("CANCELLING",0,"STOP"),

    /** Job has been cancelled. */
    CANCELED("CANCELED",0,"STOP"),

    /** All of the job's tasks have successfully finished. */
    FINISHED("FINISHED",0,"STOP"),

    /** The job is currently undergoing a reset and total restart. */
    RESTARTING("RESTARTING",2,"FAIL"),

    /** submit job **/
    NOINITIATED("NOINITIATED",0,"NOINITIATED"),

    PAUSE("PAUSE",0,"STOP"),
    STOP("STOP",0,"STOP");

    private String status;
    private Integer level;
    private String name;

    FlinkStatusEnum(String status,Integer level,String name) {
        this.status = status;
        this.level = level;
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public static  Boolean isRunning (String status) {
        for (FlinkStatusEnum ft : FlinkStatusEnum.values ()) {
            if (ft.getStatus().equals(status) && ft.level == 0) {
                return false;
            }
            if(YARN_FAILED.status.equals(status)){
                return false;
            }
            if(PAUSE.status.equals(status)){
                return false;
            }
        }
        return true;
    }

    public static  Boolean couldStop (String status) {
        for (FlinkStatusEnum ft : FlinkStatusEnum.values ()) {
            if (ft.getStatus().equals(status)) {
                if (ft.level == 2 || ft.level == 1){
                    return true;
                }
            }
        }
        return false;
    }

    public static  String getName (String status) {
        for (FlinkStatusEnum ft : FlinkStatusEnum.values ()) {
            if (ft.getStatus().equals(status)) {
                return ft.name;
            }
        }
        log.info("状态未匹配:{}",status);
        return "未知状态";
    }
}
