package com.lacus.domain.dig.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ST组件连接器信息
 * 
 * @author lacus
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StConnectorInfo {
    
    /**
     * 组件名称（唯一标识）
     */
    private String name;
    
    /**
     * 组件显示名称
     */
    private String displayName;
    
    /**
     * 组件描述
     */
    private String description;
    
    /**
     * 组件版本
     */
    private String version;
    
    /**
     * 组件作者
     */
    private String author;
    
    /**
     * 组件类型
     */
    private String type;
    
    /**
     * 组件状态
     */
    private String status;
}