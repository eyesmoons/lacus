package com.lacus.domain.datasync.job;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.common.utils.hdfs.HdfsUtil;
import com.lacus.common.utils.yarn.FlinkJobDetail;
import com.lacus.common.utils.yarn.FlinkParams;
import com.lacus.common.utils.yarn.YarnUtil;
import com.lacus.dao.datasync.entity.*;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.domain.datasync.job.dto.SourceConf;
import com.lacus.domain.datasync.job.model.FlinkJobSource;
import com.lacus.domain.datasync.job.model.FlinkSinkJobConf;
import com.lacus.domain.datasync.job.model.FlinkTaskEngine;
import com.lacus.domain.datasync.job.model.FlinkTaskSink;
import com.lacus.service.datasync.*;
import com.lacus.service.metadata.IMetaDataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JobOperationService {

    @Autowired
    private IDataSyncJobService jobService;

    @Autowired
    private IDataSyncJobCatalogService catalogService;

    @Autowired
    private IDataSyncJobInstanceService instanceService;

    @Autowired
    private IDataSyncSourceTableService sourceTableService;

    @Autowired
    private IDataSyncSinkTableService sinkTableService;

    @Autowired
    private IDataSyncTableMappingService tableMappingService;

    @Autowired
    private IDataSyncColumnMappingService columnMappingService;

    @Autowired
    private IMetaDataSourceService dataSourceService;

    @Autowired
    private JobMonitorService monitorService;

    @Value("${kafka.bootstrapServers}")
    private String bootstrapServers;

    @Value("${flink.source-jar-name}")
    private String sourceJobJarName;

    @Value("${flink.sink-jar-name}")
    private String sinkJobJarName;

    @Value("${flink.job-jars-path}")
    private String jarHdfsPath;

    @Value("${flink.conf-path}")
    private String flinkConfPath;

    /**
     * 为了节省服务器资源，所有任务以分组形式启动
     * @param catalogId 任务分组ID
     */
    public void submitJob(String catalogId, String syncType) {
        DataSyncJobCatalogEntity catalogEntity = catalogService.getById(catalogId);
        if (ObjectUtils.isEmpty(catalogEntity)) {
            throw new ApiException(ErrorCode.Internal.DB_INTERNAL_ERROR, "未查询到任务分组信息");
        }

        String catalogName = catalogEntity.getCatalogName();
        FlinkParams flinkParams = new FlinkParams();
        flinkParams.setMasterMemoryMB(catalogEntity.getJobManager());
        flinkParams.setTaskManagerMemoryMB(catalogEntity.getTaskManager());
        flinkParams.setJobName(catalogName);

        List<DataSyncJobEntity> jobs = jobService.listByCatalogId(catalogId);
        // 构建Source任务json
        List<SourceConf> sourceJobConf = buildSourceJobConf(catalogName, jobs, syncType);
        // 构建sink任务json
        List<FlinkSinkJobConf> sinkJobConf = buildSinkJobConf(catalogName, catalogId, jobs, flinkParams);

        String sourceJobPath = getJobJarPath(sourceJobJarName);
        String sinkJobPath = getJobJarPath(sinkJobJarName);
        try {
            String sourceJobName = "source_task_" + catalogName;
            String sinkJobName = "sink_task_" + catalogName;
            String sourceAppId = YarnUtil.deployOnYarn( new String[]{sourceJobName, JSON.toJSONString(sourceJobConf) }, sourceJobName, flinkParams, sourceJobPath, flinkConfPath, "");
            while (Objects.nonNull(sourceAppId)) {
                createInstance(catalogId, 1, sourceAppId, syncType);
                String sinkAppId = YarnUtil.deployOnYarn( new String[]{sinkJobName, JSON.toJSONString(sourceJobConf) }, sinkJobName, flinkParams, sinkJobPath, flinkConfPath, "");
                while (Objects.nonNull(sinkAppId)) {
                    createInstance(catalogId, 2, sinkAppId, syncType);
                }
            }
        } catch (Exception e) {
            log.error("任务提交失败：{}", e.getMessage());
        }
    }

    private void createInstance(String catalogId, Integer type, String applicationId, String syncType) {
        try {
            FlinkJobDetail flinkJobDetail = monitorService.flinkJobDetail(applicationId);
            instanceService.saveInstance(catalogId, type, syncType, applicationId, flinkJobDetail);
        } catch (Exception e) {
            instanceService.failInstance(catalogId, type, syncType, applicationId);
        }
    }

    private String getJobJarPath(String jarName) {
        String fsPrefix = HdfsUtil.DEFAULT_HDFS;
        String hdfsJarPath = fsPrefix + jarHdfsPath + jarName;
        if(!HdfsUtil.exists(hdfsJarPath)){
            throw new RuntimeException("找不到路径:" + hdfsJarPath);
        }
        return hdfsJarPath;
    }

    private List<FlinkSinkJobConf> buildSinkJobConf(String catalogName, String catalogId, List<DataSyncJobEntity> jobs, FlinkParams flinkParams) {
        List<FlinkSinkJobConf> sinkJobConfList = new ArrayList<>();
        for (DataSyncJobEntity job : jobs) {
            FlinkJobSource source = new FlinkJobSource();
            source.setBootstrapServers(bootstrapServers);
            source.setGroupId("data_sync_" + job.getJobId());
            source.setTopics(Collections.singletonList(job.getTopic()));

            FlinkTaskSink sink = new FlinkTaskSink();
            MetaDatasourceEntity sourceDatasource = dataSourceService.getById(job.getSourceDatasourceId());
            MetaDatasourceEntity sinkDatasource = dataSourceService.getById(job.getSinkDatasourceId());
            List<DataSyncSourceTableEntity> sourceTableEntities = sourceTableService.listByJobId(job.getJobId());
            if (ObjectUtils.isNotEmpty(sourceDatasource)) {
                sink.setSinkType(sourceDatasource.getType());
            }
            FlinkTaskEngine engine = new FlinkTaskEngine();
            if (ObjectUtils.isNotEmpty(sinkDatasource)) {
                engine.setIp(sinkDatasource.getIp());
                engine.setPort(sinkDatasource.getPort());
                engine.setUserName(sinkDatasource.getUsername());
                engine.setPassword(sinkDatasource.getPassword());
                if (ObjectUtils.isNotEmpty(sourceTableEntities)) {
                    engine.setDbName(sourceTableEntities.get(0).getSourceDbName());
                }
                Map<String, JSONObject> columnMap = new HashMap<>();
                DataSyncSavedTable syncSavedTableQuery = new DataSyncSavedTable();
                syncSavedTableQuery.setJobId(job.getJobId());
                LinkedList<DataSyncSavedTable> savedTables = tableMappingService.listSavedTables(syncSavedTableQuery);
                if (ObjectUtils.isNotEmpty(savedTables)) {
                    for (DataSyncSavedTable savedTable : savedTables) {
                        DataSyncSavedColumn tpl = new DataSyncSavedColumn();
                        tpl.setJobId(job.getJobId());
                        tpl.setSourceDatasourceId(job.getSourceDatasourceId());
                        tpl.setSinkDatasourceId(job.getSinkDatasourceId());
                        tpl.setSourceDbName(savedTable.getSourceDbName());
                        tpl.setSinkDbName(savedTable.getSinkDbName());
                        tpl.setSourceTableName(savedTable.getSourceTableName());
                        tpl.setSinkTableName(savedTable.getSinkTableName());
                        List<DataSyncSavedColumn> savedColumns = columnMappingService.querySavedColumns(tpl);
                        List<String> columns = new ArrayList<>();
                        List<String> jsonPaths = new ArrayList<>();
                        for (DataSyncSavedColumn savedColumn : savedColumns) {
                            columns.add("`" + savedColumn.getSinkColumnName() + "`");
                            jsonPaths.add("\"$." + savedColumn.getSourceColumnName() + "\"");
                        }

                        JSONObject columnJson = new JSONObject();
                        String sinkDbTable = savedTable.getSinkDbName() + "." + savedTable.getSinkTableName();
                        columnJson.put("sinkTable", savedTable.getSinkTableName());
                        columnJson.put("format", "json");
                        columnJson.put("max_filter_ratio", "1.0");
                        columnJson.put("strip_outer_array", true);
                        columnJson.put("columns", String.join(",", columns));
                        columnJson.put("jsonpaths", "[" + String.join(",", jsonPaths) + "]");
                        columnMap.put(sinkDbTable, columnJson);
                    }
                }
                engine.setColumnMap(columnMap);
            }
            sink.setEngine(engine);

            FlinkSinkJobConf sinkJobConf = new FlinkSinkJobConf();
            sinkJobConf.setFlinkConf(flinkParams);
            sinkJobConf.setSource(source);
            sinkJobConf.setSink(sink);
            sinkJobConfList.add(sinkJobConf);
        }
        return sinkJobConfList;
    }

    private List<SourceConf> buildSourceJobConf(String jobName, List<DataSyncJobEntity> jobs, String syncType) {
        List<SourceConf> sourceConfList = new ArrayList<>();
        List<String> jobIds = jobs.stream().map(DataSyncJobEntity::getJobId).collect(Collectors.toList());
        List<DataSyncSourceTableEntity> sourceTables = sourceTableService.listByJobIds(jobIds);
        Map<String, List<DataSyncSourceTableEntity>> sourceTablesMap = new HashMap<>();
        if (ObjectUtils.isNotEmpty(sourceTables)) {
            sourceTablesMap = sourceTables.stream().collect(Collectors.groupingBy(DataSyncSourceTableEntity::getJobId));
        }
        for (DataSyncJobEntity job : jobs) {
            SourceConf sourceConf = new SourceConf();
            List<DataSyncSourceTableEntity> sourceTableEntities = sourceTablesMap.get(job.getJobId());
            String sourceDbName = sourceTableEntities.get(0).getSourceDbName();
            List<String> sourceTableNames = sourceTableEntities.stream().map(entity -> entity.getSourceDbName() + "." + entity.getSourceTableName()).collect(Collectors.toList());
            MetaDatasourceEntity metaDatasource = dataSourceService.getById(job.getSourceDatasourceId());
            if (ObjectUtils.isNotEmpty(metaDatasource)) {
                sourceConf.setHostname(metaDatasource.getIp());
                sourceConf.setPort(metaDatasource.getPort());
                sourceConf.setUsername(metaDatasource.getUsername());
                sourceConf.setPassword(metaDatasource.getPassword());
                sourceConf.setDatabaseList(Collections.singletonList(sourceDbName));
                sourceConf.setTableList(sourceTableNames);
                sourceConf.setSyncType(syncType);
                sourceConfList.add(sourceConf);
            }
        }
        return sourceConfList;
    }
}
