package com.lacus.domain.metadata.schema.command;

import lombok.Data;

import java.util.List;

@Data
public class SyncDbTablesCommand {
    private Long datasourceId;
    private List<String> dbTables;
}
