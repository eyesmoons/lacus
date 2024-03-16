package com.lacus.core.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lacus.common.exception.CustomException;
import com.lacus.common.utils.spring.SpringUtils;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.dao.metadata.entity.MetaDatasourceTypeEntity;
import com.lacus.dao.metadata.mapper.MetaDatasourceMapper;
import com.lacus.dao.metadata.mapper.MetaDatasourceTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 数据源切换处理
 */
@Slf4j
public class DynamicDataSourceContextHolder {

    /**
     * 使用ThreadLocal维护变量，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置数据源的变量
     */
    public static void setDataSourceType(String dsType) {
        log.info("切换到{}数据源", dsType);
        CONTEXT_HOLDER.set(dsType);
    }

    private static String convertUrl(MetaDatasourceEntity metaDatasource, String protocol) {
        return String.format("jdbc:%s://%s:%s/", protocol, metaDatasource.getIp(), metaDatasource.getPort());
    }

    private static String convertUrl(String jdbcUrl, MetaDatasourceEntity metaDatasource) {
        return String.format(jdbcUrl, metaDatasource.getIp(), metaDatasource.getPort(), metaDatasource.getDefaultDbName());
    }

    /**
     * 获得数据源的变量
     */
    public static String getDataSourceType() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清空数据源变量
     */
    public static void clearDataSourceType() {
        CONTEXT_HOLDER.remove();
    }

    public static void setDataSourceId(Long datasourceId) {
        MetaDatasourceMapper metaDatasourceMapper = SpringUtils.getBean(MetaDatasourceMapper.class);
        MetaDatasourceEntity metaDatasource = metaDatasourceMapper.selectById(datasourceId);
        if (Objects.isNull(metaDatasource)) {
            throw new CustomException("无效数据源ID:" + datasourceId);
        }
        DruidDataSource druidDataSource = new DruidDataSource();
        MetaDatasourceTypeMapper metaDatasourceTypeMapper = SpringUtils.getBean(MetaDatasourceTypeMapper.class);
        LambdaQueryWrapper<MetaDatasourceTypeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MetaDatasourceTypeEntity::getTypeName, metaDatasource.getType());
        MetaDatasourceTypeEntity datasourceTypeEntity = metaDatasourceTypeMapper.selectOne(wrapper);
        if (ObjectUtils.isNotEmpty(datasourceTypeEntity)) {
            druidDataSource.setDriverClassName(datasourceTypeEntity.getDriverName());
            druidDataSource.setUrl(convertUrl(datasourceTypeEntity.getJdbcUrl(), metaDatasource));
        }
        druidDataSource.setUsername(metaDatasource.getUsername());
        druidDataSource.setBreakAfterAcquireFailure(true);
        druidDataSource.setConnectionErrorRetryAttempts(1);
        druidDataSource.setMaxWait(2000);
        druidDataSource.setFailFast(true);
        if (StringUtils.isNotEmpty(metaDatasource.getPassword())) {
            druidDataSource.setPassword(metaDatasource.getPassword());
        }

        DynamicDataSource dynamicDataSource = SpringUtils.getBean(DynamicDataSource.class);
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(metaDatasource.getDatasourceName(), druidDataSource);
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.afterPropertiesSet();

        log.info("切换到{}数据源", metaDatasource.getDatasourceName());
        CONTEXT_HOLDER.set(metaDatasource.getDatasourceName());
    }
}
