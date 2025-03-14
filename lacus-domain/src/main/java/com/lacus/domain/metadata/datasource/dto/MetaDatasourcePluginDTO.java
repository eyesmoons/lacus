package com.lacus.domain.metadata.datasource.dto;

import com.lacus.dao.metadata.entity.MetaDatasourcePlugin;
import lombok.Data;

/**
 * 数据源插件DTO
 */
@Data
public class MetaDatasourcePluginDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 插件名称
     */
    private String name;

    /**
     * 插件类型
     */
    private Integer type;

    /**
     * 驱动类名
     */
    private String driverName;

    /**
     * 图标URL
     */
    private String icon;

    /**
     * 连接参数模板
     */
    private String connectionParams;

    /**
     * 备注
     */
    private String remark;

    /**
     * 类型名称
     */
    private String typeName;

    public static MetaDatasourcePluginDTO from(MetaDatasourcePlugin plugin) {
        MetaDatasourcePluginDTO dto = new MetaDatasourcePluginDTO();
        dto.setId(plugin.getId());
        dto.setName(plugin.getName());
        dto.setType(plugin.getType());
        dto.setDriverName(plugin.getDriverName());
        dto.setIcon(plugin.getIcon());
        dto.setConnectionParams(plugin.getConnectionParams());
        dto.setRemark(plugin.getRemark());

        // 设置类型名称
        if (plugin.getType() != null) {
            switch (plugin.getType()) {
                case 1:
                    dto.setTypeName("关系型数据库");
                    break;
                case 2:
                    dto.setTypeName("非关系型数据库");
                    break;
                case 3:
                    dto.setTypeName("OLAP数据库");
                    break;
                default:
                    dto.setTypeName("未知类型");
            }
        }

        return dto;
    }
}
