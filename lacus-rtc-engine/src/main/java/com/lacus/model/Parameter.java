package com.lacus.model;

import com.lacus.exception.CustomException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import lombok.Getter;

import java.io.Serializable;

/**
 * 应用程序参数类
 */
@Getter
public class Parameter implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int REQUIRED_ARGS_LENGTH = 4;

    public final String sourceName;
    public final String sinkName;
    public final String jobName;
    public final String jobParams;

    public Parameter(String[] args) {
        validateParams(args);
        this.sourceName = args[0];
        this.sinkName = args[1];
        this.jobName = args[2];
        this.jobParams = args[3];
    }

    private void validateParams(String[] args) {
        if (ObjectUtils.isEmpty(args) || args.length < REQUIRED_ARGS_LENGTH) {
            throw new CustomException("参数不足，需要提供：sourceName、sinkName、jobName、jobParams");
        }
        for (int i = 0; i < REQUIRED_ARGS_LENGTH; i++) {
            if (StringUtils.isEmpty(args[i])) {
                throw new CustomException(String.format("第%d个参数不能为空", i + 1));
            }
        }
    }
}
