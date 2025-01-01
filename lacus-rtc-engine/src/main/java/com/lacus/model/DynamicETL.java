package com.lacus.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @created by shengyu on 2023/9/6 11:01
 */
@Data
public class DynamicETL implements Serializable {
    private static final long serialVersionUID = 7909943071301606492L;
    private String etlLanguage;
    private String etlScript;
}