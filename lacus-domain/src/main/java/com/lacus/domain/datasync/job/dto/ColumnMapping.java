package com.lacus.domain.datasync.job.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ColumnMapping implements Serializable {
    private String sourceColumn;
    private String sinkColumn;
}