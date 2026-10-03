package com.lacus.enums;

/**
 * 存储来源枚举
 */
public enum StorageSource {

    LOCAL("LOCAL", "本地文件系统"),
    HDFS("HDFS", "HDFS 分布式文件系统"),
    S3("S3", "AWS S3 对象存储"),
    MINIO("MINIO", "MinIO 对象存储"),
    HTTP("HTTP", "HTTP 远程URL");

    private final String code;
    private final String desc;

    StorageSource(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static StorageSource fromCode(String code) {
        for (StorageSource source : values()) {
            if (source.getCode().equalsIgnoreCase(code)) {
                return source;
            }
        }
        throw new IllegalArgumentException("Unknown StorageSource code: " + code);
    }
}
