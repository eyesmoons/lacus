package com.lacus.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * command types
 */
public enum CommandType {

    /**
     * command types
     * 0 start a new process
     * 1 start a new process from current nodes
     * 2 recover tolerance fault process
     * 3 recover suspended process
     * 4 start process from failure task nodes
     * 5 complement data
     * 6 start a new process from scheduler
     * 7 repeat running a process
     * 8 pause a process
     * 9 stop a process
     * 10 recover waiting thread
     */
    START_PROCESS(0, "start a new process"),
    START_CURRENT_TASK_PROCESS(1, "start a new process from current nodes"),
    RECOVER_TOLERANCE_FAULT_PROCESS(2, "recover tolerance fault process"),
    RECOVER_SUSPENDED_PROCESS(3, "recover suspended process"),
    START_FAILURE_TASK_PROCESS(4, "start process from failure task nodes"),
    COMPLEMENT_DATA(5, "complement data"),
    SCHEDULER(6, "start a new process from scheduler"),
    REPEAT_RUNNING(7, "repeat running a process"),
    PAUSE(8, "pause a process"),
    STOP(9, "stop a process"),
    RECOVER_WAITTING_THREAD(10, "recover waiting thread");

    CommandType(int code, String descp){
        this.code = code;
        this.descp = descp;
    }

    @EnumValue
    private final int code;
    private final String descp;

    public int getCode() {
        return code;
    }

    public String getDescp() {
        return descp;
    }

    public static CommandType of(Integer status){
        for(CommandType cmdType : values()){
            if(cmdType.getCode() == status){
                return cmdType;
            }
        }
        throw new IllegalArgumentException("invalid status : " + status);
    }
}
