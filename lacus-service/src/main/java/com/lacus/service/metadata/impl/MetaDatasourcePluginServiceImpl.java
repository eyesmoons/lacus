package com.lacus.service.metadata.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.metadata.entity.MetaDatasourcePlugin;
import com.lacus.dao.metadata.mapper.MetaDatasourcePluginMapper;
import com.lacus.service.metadata.IMetaDatasourcePluginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetaDatasourcePluginServiceImpl extends ServiceImpl<MetaDatasourcePluginMapper, MetaDatasourcePlugin> implements IMetaDatasourcePluginService {

    @Override
    public Page<MetaDatasourcePlugin> pagePlugins(Page<MetaDatasourcePlugin> page, String name, Integer type) {
        LambdaQueryWrapper<MetaDatasourcePlugin> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), MetaDatasourcePlugin::getName, name)
                .eq(type != null, MetaDatasourcePlugin::getType, type);
        return this.page(page, wrapper);
    }

    @Override
    public List<MetaDatasourcePlugin> listPlugins(String name, Integer type) {
        LambdaQueryWrapper<MetaDatasourcePlugin> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), MetaDatasourcePlugin::getName, name)
                .eq(type != null, MetaDatasourcePlugin::getType, type);
        return this.list(wrapper);
    }

    @Override
    public MetaDatasourcePlugin getPluginByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        LambdaQueryWrapper<MetaDatasourcePlugin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MetaDatasourcePlugin::getName, name);
        return this.getOne(wrapper);
    }
}
