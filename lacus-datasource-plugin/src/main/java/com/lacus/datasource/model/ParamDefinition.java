package com.lacus.datasource.model;

import com.lacus.datasource.enums.ParamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据源连接参数定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParamDefinition {

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数显示名称
     */
    private String displayName;

    /**
     * 参数描述
     */
    private String description;

    /**
     * 参数类型
     */
    private ParamType type;

    /**
     * 是否必填
     */
    private boolean required;

    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * 参数校验规则
     */
    private ParamValidation validation;

    /**
     * 参数排序
     */
    private int order;
}
