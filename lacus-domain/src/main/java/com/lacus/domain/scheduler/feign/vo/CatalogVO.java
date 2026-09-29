package com.lacus.domain.scheduler.feign.vo;

import lombok.Data;

/**
 * @author shengyu
 * @date 2024/7/31 09:40
 */
@Data
public class CatalogVO {
    private String oldCatalogName;
    private String catalogName;
    private String connectorName;
}
