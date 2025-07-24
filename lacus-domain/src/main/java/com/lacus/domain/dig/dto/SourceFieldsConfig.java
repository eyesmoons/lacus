package com.lacus.domain.dig.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SourceFieldsConfig {
    private String tableName;
    private List<TableField> tableFields;
}
