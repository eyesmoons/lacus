package com.lacus.domain.dig.dto;

import lombok.Data;

@Data
public class TableField {

    private String type;

    private String name;

    private String comment;

    private Boolean primaryKey;

    private String defaultValue;

    private Boolean nullable;
}
