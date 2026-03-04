package com.lacus.domain.dig.dto;

import lombok.Data;

import java.util.List;

@Data
public class SourceFieldsConfig {
    private String tableName;
    private List<TableField> tableFields;
}
