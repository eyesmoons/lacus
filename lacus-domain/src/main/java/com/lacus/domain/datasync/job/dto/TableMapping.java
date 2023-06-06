package com.lacus.domain.datasync.job.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TableMapping implements Serializable {
    private String sourceTableName;
    private String sinkTableName;
    private List<ColumnMapping> columns;
}