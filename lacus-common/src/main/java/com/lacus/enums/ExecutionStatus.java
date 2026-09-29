package com.lacus.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum ExecutionStatus {
    SUBMITTED_SUCCESS(0, "submit success"),
    RUNNING_EXECUTION(1, "running"),
    READY_PAUSE(2, "ready pause"),
    PAUSE(3, "pause"),
    READY_STOP(4, "ready stop"),
    STOP(5, "stop"),
    FAILURE(6, "failure"),
    SUCCESS(7, "success"),
    NEED_FAULT_TOLERANCE(8, "need fault tolerance"),
    KILL(9, "kill"),
    WAITTING_THREAD(10, "waiting thread"),
    WAITTING_DEPEND(11, "waiting depend node complete");

    @EnumValue
    private final int code;
    private final String descp;

    private ExecutionStatus(int code, String descp) {
        this.code = code;
        this.descp = descp;
    }

    public boolean typeIsSuccess() {
        return this == SUCCESS;
    }

    public boolean typeIsFailure() {
        return this == FAILURE || this == NEED_FAULT_TOLERANCE;
    }

    public boolean typeIsFinished() {
        return this.typeIsSuccess() || this.typeIsFailure() || this.typeIsCancel() || this.typeIsPause() || this.typeIsStop();
    }

    public boolean typeIsWaitingThread() {
        return this == WAITTING_THREAD;
    }

    public boolean typeIsPause() {
        return this == PAUSE;
    }

    public boolean typeIsStop() {
        return this == STOP;
    }

    public boolean typeIsRunning() {
        return this == RUNNING_EXECUTION || this == WAITTING_DEPEND;
    }

    public boolean typeIsCancel() {
        return this == KILL || this == STOP;
    }

    public int getCode() {
        return this.code;
    }

    public String getDescp() {
        return this.descp;
    }

    public static ExecutionStatus of(int status) {
        ExecutionStatus[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            ExecutionStatus es = var1[var3];
            if (es.getCode() == status) {
                return es;
            }
        }

        throw new IllegalArgumentException("invalid status : " + status);
    }
}
