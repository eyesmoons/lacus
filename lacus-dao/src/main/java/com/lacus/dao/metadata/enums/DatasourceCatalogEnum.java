package com.lacus.dao.metadata.enums;

public enum DatasourceCatalogEnum {
    SQL("SQL", "关系型数据库"),
    NOSQL("NoSQL", "非关系型数据库"),
    GRAPH("GRAPH", "图数据库"),
    OLAP("OLAP", "分析型数据库"),
    FILE("FILE", "文件型数据库"),
    ;
    private String name;
    private String remark;

    DatasourceCatalogEnum(String name, String remark) {
        this.name = name;
        this.remark = remark;
    }

    public String getName() {
        return name;
    }

    public String getRemark() {
        return remark;
    }
}
