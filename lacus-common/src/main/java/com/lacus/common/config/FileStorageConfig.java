package com.lacus.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件存储配置
 */
@Component
@ConfigurationProperties(prefix = "storage")
@Data
public class FileStorageConfig {

    /**
     * 存储根目录
     */
    private String root = "/data/lake-intelligence";

    /**
     * 模型权重目录（需与 ML 服务的 MODEL_DIR 保持一致，用于下载防护）
     */
    private String modelDir = "/data/lake-intelligence/models";

    /**
     * 数据集最大文件数
     */
    private int maxDatasetSize = 50000;

    /**
     * 最大上传大小（字节）
     */
    private long maxUploadSize = 500 * 1024 * 1024L;

    /**
     * 最大图片大小（字节）
     */
    private long maxImageSize = 10 * 1024 * 1024L;
}
