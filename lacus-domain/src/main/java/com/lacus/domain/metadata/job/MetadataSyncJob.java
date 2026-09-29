package com.lacus.domain.metadata.job;

import com.lacus.common.exception.CustomException;
import com.lacus.core.datasource.DynamicDataSourceContextHolder;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.dao.metadata.entity.SchemaDbEntity;
import com.lacus.dao.metadata.entity.SchemaTableEntity;
import com.lacus.dao.metadata.mapper.MetaTableMapper;
import com.lacus.dao.metadata.mapper.MysqlSchemaMapper;
import com.lacus.domain.metadata.schema.SyncSchemaBusiness;
import com.lacus.service.metadata.IMetaDataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private MysqlSchemaMapper mysqlSchemaMapper;

    /**
     * 同步指定数据源的元数据
     *
     * @param datasourceId 数据源 ID
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

            // 2. 获取数据源中的所有数据库列表（包括未创建的）- 优化版本，避免连接过多
            List<String> allDbTables = getAllDbTablesFromSourceOptimized(datasourceId);

            if (allDbTables.isEmpty()) {
                log.warn("数据源中没有数据库：datasourceId={}", datasourceId);
                return;
            }

            log.info("待同步的表数量：{}", allDbTables.size());

            // 3. 分批同步，避免一次性占用过多连接
            int batchSize = 50; // 每批同步 50 张表
            for (int i = 0; i < allDbTables.size(); i += batchSize) {
                int end = Math.min(i + batchSize, allDbTables.size());
                List<String> batchTables = allDbTables.subList(i, end);

                log.info("正在同步批次 {}/{}, 表数量：{}",
                        (i / batchSize) + 1,
                        (allDbTables.size() + batchSize - 1) / batchSize,
                        batchTables.size());

                boolean success = syncSchemaBusiness.syncDbTables(datasourceId, batchTables);

                if (!success) {
                    log.error("批次同步失败，批次：{}", (i / batchSize) + 1);
                }

                // 每批之间短暂休眠，让连接有机会释放
                if (end < allDbTables.size()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            log.info("数据源元数据同步完成，datasourceId={}，同步表总数={}",
                    datasourceId, allDbTables.size());
        } catch (Exception e) {
            log.error("元数据同步失败，datasourceId={}", datasourceId, e);
            throw new CustomException("元数据同步失败：" + e.getMessage(), e);
        }
    }

    /**
     * 从数据源获取所有数据库的表列表（优化版本）
     * 使用批量查询代替循环查询，避免创建过多数据库连接
     *
     * @param datasourceId 数据源 ID
     * @return 数据库表列表，格式：dbName.tableName
     */
    private List<String> getAllDbTablesFromSourceOptimized(Long datasourceId) {
        try {
            DynamicDataSourceContextHolder.setDataSourceId(datasourceId);

            // 1. 获取所有数据库列表
            List<SchemaDbEntity> allDbs = mysqlSchemaMapper.listAllSchemaDb();

            if (allDbs.isEmpty()) {
                log.warn("数据源中没有数据库：datasourceId={}", datasourceId);
                return new ArrayList<>();
            }

            // 2. 批量获取所有数据库的所有表（关键优化：一次查询代替 N 次查询）
            List<String> allDbTables = new ArrayList<>();

            // 构建数据库名称列表用于 IN 查询
            List<String> dbNames = new ArrayList<>();
            for (SchemaDbEntity db : allDbs) {
                dbNames.add(db.getSchemaName());
            }

            // 一次性查询所有数据库的所有表（使用 INFORMATION_SCHEMA）
            List<SchemaTableEntity> allTables = mysqlSchemaMapper.listAllTablesByDatabases(dbNames);

            for (SchemaTableEntity table : allTables) {
                allDbTables.add(table.getTableSchema() + "." + table.getTableName());
            }

            log.info("批量获取到 {} 个数据库，共 {} 张表", dbNames.size(), allTables.size());

            return allDbTables;
        } catch (Exception e) {
            log.error("获取数据源表信息失败，datasourceId={}", datasourceId, e);
            throw new CustomException("获取数据源表信息失败：" + e.getMessage(), e);
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }
}
