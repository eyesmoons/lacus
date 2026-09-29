package com.lacus.common.enums;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum DatabaseType {

    /**
     * MySQL
     */
    MySQL("jdbcProvider", "jdbc:mysql://%s:%s/%s", "com.mysql.cj.jdbc.Driver"),

    /**
     * Oracle
     */
    Oracle("oracle", "jdbc:oracle:thin:%s:%s:%s", "oracle.jdbc.OracleDriver"),

    /**
     * Postgresql
     */
    Postgresql("postgresql", "jdbc:postgresql://%s:%s/%s", "org.postgresql.Driver"),

    /**
     * SqlServer
     */
    SqlServer("sqlserver", "jdbc:sqlserver://%s:%s;databaseName=%s", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),

    /**
     * Doris
     */
    Doris("jdbcProvider", "jdbc:mysql://%s:%s/%s", "com.mysql.cj.jdbc.Driver");

    /**
     * url format
     */

    private String name;

    private String urlFormat;

    /**
     * driver class name
     */
    private String driverClassName;

    DatabaseType(String name, String urlFormat, String driverClassName) {
        this.name = name;
        this.urlFormat = urlFormat;
        this.driverClassName = driverClassName;
    }


    public static DatabaseType getDriver(String type) {
        DatabaseType[] values = DatabaseType.values();
        for (DatabaseType enums : values) {
            if (Objects.equals(enums.getName(), type)) {
                return enums;
            }
        }
        return null;
    }
}
