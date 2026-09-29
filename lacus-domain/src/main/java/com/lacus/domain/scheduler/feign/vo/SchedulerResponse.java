package com.lacus.domain.scheduler.feign.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class SchedulerResponse<T> {

    public static Integer SUCCESS_CODE = 2000;

    public static Integer FALLBACK_CODE = -1;

    public static SchedulerResponse FALLBACK_RESPONSE = new SchedulerResponse(-1, "call error, fallback!");

    private Integer code;

    private String msg;

    private T data;

    public SchedulerResponse(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }

    public boolean isSuccess(int expectHttpStatus) {
        if (Objects.isNull(code)) {
            return false;
        }
        return code.intValue() == expectHttpStatus;
    }

}
