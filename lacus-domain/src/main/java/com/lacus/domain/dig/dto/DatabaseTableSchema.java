package com.lacus.domain.dig.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseTableSchema {
    private String database;
    private String tableName;
    private List<TableField> fields;
}
