package com.lacus.domain.datasync.job.dto;

import com.lacus.dao.metadata.entity.MetaColumnEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TableMapping implements Serializable {
    private String sourceTableName;
    private String sinkTableName;
    private List<MetaColumnEntity> sourceColumns;
    private List<MetaColumnEntity> sinkColumns;
}