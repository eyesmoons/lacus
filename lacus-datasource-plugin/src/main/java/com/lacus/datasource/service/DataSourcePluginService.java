package com.lacus.datasource.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lacus.dao.entity.MetaDatasourcePlugin;
import com.lacus.dao.mapper.MetaDatasourcePluginMapper;
import com.lacus.datasource.api.DataSourcePlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * 数据源插件服务
 */
@Slf4j
@Service
public class DataSourcePluginService {

    @Autowired
    private MetaDatasourcePluginMapper pluginMapper;

    private final Map<String, DataSourcePlugin> dataSourcePluginMap = new HashMap<>();

    @Transactional(rollbackFor = Exception.class)
    public void registerAll() {
        // 注册所有插件
        registerPlugins();
    }

    private void registerPlugins() {
        // 使用SPI加载所有插件
        ServiceLoader<DataSourcePlugin> plugins = ServiceLoader.load(DataSourcePlugin.class);

        for (DataSourcePlugin plugin : plugins) {
            try {
                // 检查插件是否已存在
                LambdaQueryWrapper<MetaDatasourcePlugin> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(MetaDatasourcePlugin::getName, plugin.getName());
                MetaDatasourcePlugin existingPlugin = pluginMapper.selectOne(wrapper);

                if (Objects.isNull(existingPlugin)) {
                    // 插入新插件
                    MetaDatasourcePlugin newPlugin = new MetaDatasourcePlugin();
                    newPlugin.setName(plugin.getName());
                    newPlugin.setType(plugin.getType());
                    newPlugin.setDriverName(plugin.getDriverName());
                    newPlugin.setIcon(plugin.getIcon());
                    newPlugin.setConnectionParams(JSON.toJSONString(plugin.getConnectionParamDefinitions()));
                    newPlugin.setRemark(plugin.getRemark());

                    pluginMapper.insert(newPlugin);
                    dataSourcePluginMap.put(plugin.getName(), plugin);
                }
                log.info("注册数据源插件成功: {}", plugin.getName());
            } catch (Exception e) {
                log.error("注册数据源插件失败: {}", plugin.getName(), e);
            }
        }
    }

    public DataSourcePlugin getProcessor(String name) {
        return dataSourcePluginMap.get(name);
    }
}
