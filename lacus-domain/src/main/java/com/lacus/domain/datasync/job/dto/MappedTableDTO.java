package com.lacus.domain.datasync.job.dto;

import lombok.Data;

import java.util.LinkedList;

@Data
public class MappedTableDTO {
    private String jobId;
    private LinkedList<TableDTO> mappedSourceTables;
    private LinkedList<TableDTO> mappedSinkTables;
}
