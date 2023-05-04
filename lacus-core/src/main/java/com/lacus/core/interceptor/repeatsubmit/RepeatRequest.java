package com.lacus.core.interceptor.repeatsubmit;

import java.util.Objects;
import lombok.Data;

@Data
public class RepeatRequest {

    private String repeatParams;
    private Long repeatTime;

    public boolean compareParams(RepeatRequest preRequest) {
        if (preRequest == null) {
            return false;
        }
        return Objects.equals(this.repeatParams, preRequest.repeatParams);
    }


    public boolean compareTime(RepeatRequest preRequest, int interval) {
        if (preRequest == null) {
            return false;
        }
        return preRequest.getRepeatTime() - this.repeatTime < interval;
    }

}
