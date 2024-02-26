package com.lacus.domain.common.dto;

import lombok.Data;

/**
 * @created by shengyu on 2023/9/6 11:01
 */
@Data
public class DynamicETL {
    private String etlLanguage;
    private String etlScript;
    private String table;
    private String data;
}