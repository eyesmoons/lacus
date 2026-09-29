package com.lacus.domain.dig;

import com.lacus.domain.dig.resp.StConnectorInfo;
import com.lacus.enums.StConnectorStatus;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.loader.StComponentLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ST组件连接器业务类
 * 
 * @author lacus
 */
@Slf4j
@Service
public class StComponentConnectorBusiness {

    @Autowired
    private StComponentLoader stComponentLoader;

    /**
     * 获取源组件列表
     */
    public List<StConnectorInfo> listSources() {
        return getComponentsByType(StComponent.ComponentType.SOURCE);
    }

    /**
     * 获取转换组件列表
     */
    public List<StConnectorInfo> listTransforms() {
        return getComponentsByType(StComponent.ComponentType.TRANSFORM);
    }

    /**
     * 获取输出组件列表
     */
    public List<StConnectorInfo> listSinks() {
        return getComponentsByType(StComponent.ComponentType.SINK);
    }

    /**
     * 根据组件类型获取组件列表
     */
    private List<StConnectorInfo> getComponentsByType(StComponent.ComponentType type) {
        List<StConnectorInfo> result = new ArrayList<>();
        
        try {
            // 确保组件加载器已初始化
            stComponentLoader.initialize();
            
            // 获取指定类型的组件名称
            Set<String> componentNames = stComponentLoader.getComponentNamesByType(type);
            
            for (String componentName : componentNames) {
                Map<String, Object> metadata = stComponentLoader.getComponentMetadata(componentName);
                if (metadata != null) {
                    StConnectorInfo connectorInfo = new StConnectorInfo();
                    connectorInfo.setName((String) metadata.get("name"));
                    connectorInfo.setDisplayName((String) metadata.get("displayName"));
                    connectorInfo.setDescription((String) metadata.get("description"));
                    connectorInfo.setVersion((String) metadata.get("version"));
                    connectorInfo.setAuthor((String) metadata.get("author"));
                    connectorInfo.setType((String) metadata.get("type"));
                    
                    // 设置状态（目前所有组件都认为是已下载状态）
                    connectorInfo.setStatus("DOWNLOADED");
                    result.add(connectorInfo);
                }
            }
            
        } catch (Exception e) {
            log.error("获取ST组件列表失败", e);
        }
        
        return result;
    }

    /**
     * 获取组件表单结构
     */
    public Map<String, Object> getConnectorFormStructure(String connectorType, String connectorName) {
        try {
            // 确保组件加载器已初始化
            stComponentLoader.initialize();
            
            Map<String, Object> metadata = stComponentLoader.getComponentMetadata(connectorName);
            if (metadata == null) {
                log.warn("未找到组件: {}", connectorName);
                return null;
            }
            
            // 验证组件类型是否匹配
            String componentType = (String) metadata.get("type");
            if (!connectorType.toLowerCase().equals(componentType)) {
                log.warn("组件类型不匹配，期望: {}, 实际: {}", connectorType, componentType);
                return null;
            }
            
            // 返回字段配置信息
            return (Map<String, Object>) metadata.get("fieldConfigs");
            
        } catch (Exception e) {
            log.error("获取组件表单结构失败: {}", connectorName, e);
            return null;
        }
    }
}