package com.lacus.flink.dto;


import com.lacus.flink.enums.SqlCommandEnum;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SqlCommand {
    private SqlCommandEnum sqlCommandEnum;
    private String[] operands;

    public SqlCommand(SqlCommandEnum sqlCommandEnum, String[] operands) {
        this.sqlCommandEnum = sqlCommandEnum;
        this.operands = operands;
    }

    public SqlCommand(String[] operands) {
        this.operands = operands;
    }
}
