package com.lacus.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 数据源插件实体类
 */
@Data
@TableName("meta_datasource_plugin")
public class MetaDatasourcePlugin {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
} 