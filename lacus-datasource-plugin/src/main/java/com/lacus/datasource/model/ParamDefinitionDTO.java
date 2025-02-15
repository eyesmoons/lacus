package com.lacus.datasource.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据源连接参数定义DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParamDefinitionDTO {
    
    /**
     * 默认值
     */
    private Object defaultValue;
    
    /**
     * 是否必填
     */
    private boolean required;
    
    /**
     * 输入类型
     */
    private String inputType;
    
    /**
     * 显示名称
     */
    private String displayName;
    
    /**
     * 参数描述
     */
    private String description;
    
    /**
     * 参数排序
     */
    private int order;
    
    /**
     * 参数校验规则
     */
    private ParamValidation validation;
} 