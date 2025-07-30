package com.lacus.domain.dig.dto;

import lombok.Data;

import java.util.List;

@Data
public class DatasourceConfig {
    private String database;
    private List<String> tables;
}
