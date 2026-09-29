package com.lacus.service.metadata;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.metadata.entity.MetaDatasourcePlugin;

import java.util.List;

public interface IMetaDatasourcePluginService extends IService<MetaDatasourcePlugin> {

    /**
     * 分页查询数据源插件
     *
     * @param page 分页参数
     * @param name 插件名称
     * @param type 插件类型
     * @return 分页结果
     */
    Page<MetaDatasourcePlugin> pagePlugins(Page<MetaDatasourcePlugin> page, String name, Integer type);

    /**
     * 查询所有数据源插件
     *
     * @param name 插件名称
     * @param type 插件类型
     * @return 插件列表
     */
    List<MetaDatasourcePlugin> listPlugins(String name, Integer type);

    /**
     * 根据插件名称查询数据源插件
     *
     * @param name 插件名称
     * @return 数据源插件
     */
    MetaDatasourcePlugin getPluginByName(String name);
}
