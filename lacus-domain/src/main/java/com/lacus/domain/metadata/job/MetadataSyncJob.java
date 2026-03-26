package com.lacus.domain.metadata.job;

import com.lacus.common.exception.CustomException;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.dao.metadata.entity.MetaDbTableEntity;
import com.lacus.dao.metadata.mapper.MetaTableMapper;
import com.lacus.domain.metadata.schema.SyncSchemaBusiness;
import com.lacus.service.metadata.IMetaDataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 元数据同步定时任务
 */
@Component("metadataSyncJob")
@Slf4j
public class MetadataSyncJob {

    @Autowired
    private SyncSchemaBusiness syncSchemaBusiness;

    @Autowired
    private IMetaDataSourceService metaDataSourceService;

    @Autowired
    private MetaTableMapper metaTableMapper;

    /**
     * 同步指定数据源的元数据
     *
     * @param datasourceId 数据源ID
     */
    public void syncDatasource(Long datasourceId) {
        log.info("开始同步数据源元数据，datasourceId={}", datasourceId);

        try {
            // 1. 检查数据源是否存在且启用
            MetaDatasourceEntity datasource = metaDataSourceService.getById(datasourceId);
            if (datasource == null) {
                throw new CustomException("数据源不存在：" + datasourceId);
            }
            if (datasource.getStatus() != 1) {
                log.warn("数据源未启用，跳过同步：datasourceId={}, status={}", datasourceId, datasource.getStatus());
                return;
            }

            // 2. 获取该数据源下所有已同步的数据库和表
            List<String> dbTables = getExistingDbTables(datasourceId);

            if (dbTables.isEmpty()) {
                log.info("数据源下没有已同步的表，跳过同步：datasourceId={}", datasourceId);
                return;
            }

            // 3. 调用现有的同步逻辑
            boolean success = syncSchemaBusiness.syncDbTables(datasourceId, dbTables);

            log.info("数据源元数据同步完成，datasourceId={}，同步表数量={}, 结果={}",
                     datasourceId, dbTables.size(), success ? "成功" : "失败");
        } catch (Exception e) {
            log.error("元数据同步失败，datasourceId={}", datasourceId, e);
            throw new CustomException("元数据同步失败：" + e.getMessage(), e);
        }
    }

    /**
     * 获取数据源下已同步的数据库表列表
     *
     * @param datasourceId 数据源ID
     * @return 数据库表列表，格式：dbName.tableName
     */
    private List<String> getExistingDbTables(Long datasourceId) {
        List<MetaDbTableEntity> metaDbTables = metaTableMapper.listMetaDbTable(datasourceId, null, null);

        return metaDbTables.stream()
                .map(t -> t.getDbName() + "." + t.getTableName())
                .collect(Collectors.toList());
    }
}
