package com.lacus.datasource.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参数校验规则
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParamValidation {
    /**
     * 最小值（用于数值类型）
     */
    private Integer minValue;
    
    /**
     * 最大值（用于数值类型）
     */
    private Integer maxValue;
    
    /**
     * 正则表达式（用于字符串类型）
     */
    private String pattern;
    
    /**
     * 最小长度（用于字符串类型）
     */
    private Integer minLength;
    
    /**
     * 最大长度（用于字符串类型）
     */
    private Integer maxLength;
}
