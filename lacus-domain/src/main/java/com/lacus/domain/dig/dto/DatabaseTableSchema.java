package com.lacus.domain.dig.dto;

import lombok.Data;

import java.util.List;

@Data
public class DatabaseTableSchema {
    private String database;
    private String tableName;
    private List<TableField> fields;
}
