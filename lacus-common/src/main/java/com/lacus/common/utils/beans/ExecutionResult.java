package com.lacus.common.utils.beans;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class ExecutionResult implements Serializable {
        private static final long serialVersionUID = 3624792913480518384L;
        // 元数据
        private List<Map<String, String>> meta;
        // 数据体
        private List<List<Object>> data;
        // 执行结果信息
        private ExecutionInfo info;

        @Data
        public static class ExecutionInfo implements Serializable {
            private static final long serialVersionUID = 101849265616910242L;

            // 返回行数
            private Long returnRows;

            // 执行时长
            private String duration;

            // 返回结果信息
            private String message;

            // 执行状态
            private Integer StatusCode;
        }
    }