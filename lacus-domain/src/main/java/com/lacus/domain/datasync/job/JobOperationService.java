package com.lacus.domain.datasync.job;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.common.utils.hdfs.HdfsUtil;
import com.lacus.common.utils.yarn.FlinkConf;
import com.lacus.common.utils.yarn.FlinkJobDetail;
import com.lacus.common.utils.yarn.FlinkParams;
import com.lacus.common.utils.yarn.YarnUtil;
import com.lacus.dao.datasync.entity.*;
import com.lacus.dao.datasync.enums.FlinkStatusEnum;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.domain.datasync.job.model.DataSyncJobConf;
import com.lacus.domain.datasync.job.model.FlinkJobSource;
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

    private static final String JOB_MAIN_CLASS = "com.lacus.job.flink.impl.DataSyncJob";

    @Value("${flink.jar-name}")
    private String flinkJobJarName;

    @Value("${flink.job-jars-path}")
    private String jarHdfsPath;

    @Value("${flink.conf-path}")
    private String flinkConfPath;

    @Value("${hdfs.defaultFS}")
    private String defaultHdfs;

    @Value("${kafka.bootstrapServers}")
    private String bootstrapServers;

    @Value("${hdfs.username}")
    private String hadoopUserName;

    @Value("${flink.lib-path}")
    private String flinkLibs;

    @Value("${flink.dist-jar-path}")
    private String flinkDistJar;

    /**
     * 启动任务
     *
     * @param jobId 任务ID
     * @param syncType  启动方式
     * @param timeStamp 指定时间戳
     */
    public void submitJob(Long jobId, String syncType, String timeStamp) {
        DataSyncJobEntity job = jobService.getById(jobId);
        if (ObjectUtils.isEmpty(job)) {
            throw new ApiException(ErrorCode.Internal.DB_INTERNAL_ERROR, "未查询到任务信息");
        }

        String jobName = job.getJobName();
        FlinkParams flinkParams = new FlinkParams();
        flinkParams.setMasterMemoryMB(job.getJobManager() * 1024);
        flinkParams.setTaskManagerMemoryMB(job.getTaskManager() * 1024);
        flinkParams.setJobName(jobName);

        // 构建任务json
        DataSyncJobConf jobConf = buildJobConf(job, syncType, timeStamp);
        log.info("jobConf：{}", JSON.toJSONString(jobConf));
        String flinkJobPath = getJobJarPath(flinkJobJarName);
        try {
            String sourceAppId = YarnUtil.deployOnYarn(
                    JOB_MAIN_CLASS,
                    new String[]{jobName, JSON.toJSONString(jobConf)},
                    jobName,
                    flinkParams,
                    flinkJobPath,
                    flinkConfPath,
                    "",
                    defaultHdfs,
                    hadoopUserName,
                    flinkLibs,
                    flinkDistJar
            );
            if (Objects.nonNull(sourceAppId)) {
                createInstance(jobId, sourceAppId, syncType);
            }
        } catch (Exception e) {
            log.error("任务提交失败", e);
        }
    }

    private void createInstance(Long jobId, String applicationId, String syncType) {
        try {
            FlinkJobDetail flinkJobDetail = monitorService.flinkJobDetail(applicationId);
            instanceService.saveInstance(jobId, syncType, applicationId, flinkJobDetail);
        } catch (Exception e) {
            instanceService.failInstance(jobId, syncType, applicationId);
        }
    }

    private String getJobJarPath(String jarName) {
        String fsPrefix = ObjectUtils.isEmpty(defaultHdfs) ? HdfsUtil.DEFAULT_HDFS : defaultHdfs;
        String hdfsJarPath = fsPrefix + jarHdfsPath + jarName;
        if (!HdfsUtil.exists(defaultHdfs, hadoopUserName, hdfsJarPath)) {
            throw new RuntimeException("找不到路径:" + hdfsJarPath);
        }
        return hdfsJarPath;
    }

    private DataSyncJobConf buildJobConf(DataSyncJobEntity job, String syncType, String timeStamp) {
        FlinkJobSource source = new FlinkJobSource();
        List<DataSyncSourceTableEntity> sourceTables = sourceTableService.listByJobIds(Collections.singletonList(job.getJobId()));
        String sourceDbName = sourceTables.get(0).getSourceDbName();
        List<String> sourceTableNames = sourceTables.stream().map(DataSyncSourceTableEntity::getSourceTableName).collect(Collectors.toList());
        MetaDatasourceEntity metaDatasource = dataSourceService.getById(job.getSourceDatasourceId());
        if (ObjectUtils.isNotEmpty(metaDatasource)) {
            source.setHostname(metaDatasource.getIp());
            source.setPort(metaDatasource.getPort());
            source.setUsername(metaDatasource.getUsername());
            source.setPassword(metaDatasource.getPassword());
            source.setDatabaseList(Collections.singletonList(sourceDbName));
            source.setTableList(sourceTableNames);
            source.setSyncType(syncType);
            if (ObjectUtils.isNotEmpty(timeStamp)) {
                source.setTimeStamp(Long.valueOf(timeStamp));
            }
            source.setBootStrapServers(bootstrapServers);
            source.setTopic("data_sync_topic_" + job.getJobId());
            source.setGroupId("data_sync_group_" + job.getJobId());
        }

        FlinkTaskSink sink = new FlinkTaskSink();
        MetaDatasourceEntity sourceDatasource = dataSourceService.getById(job.getSourceDatasourceId());
        MetaDatasourceEntity sinkDatasource = dataSourceService.getById(job.getSinkDatasourceId());
        List<DataSyncSinkTableEntity> sinkTableEntities = sinkTableService.listByJobId(job.getJobId());
        if (ObjectUtils.isNotEmpty(sourceDatasource)) {
            sink.setSinkType(sinkDatasource.getType());
        }
        FlinkTaskEngine engine = new FlinkTaskEngine();
        if (ObjectUtils.isNotEmpty(sinkDatasource)) {
            engine.setIp(sinkDatasource.getIp());
            engine.setPort(sinkDatasource.getPort());
            engine.setUserName(sinkDatasource.getUsername());
            engine.setPassword(sinkDatasource.getPassword());
            if (ObjectUtils.isNotEmpty(sinkTableEntities)) {
                engine.setDbName(sinkTableEntities.get(0).getSinkDbName());
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
                    String sourceTable = savedTable.getSourceTableName();
                    columnJson.put("sinkTable", savedTable.getSinkTableName());
                    columnJson.put("format", "json");
                    columnJson.put("max_filter_ratio", "1.0");
                    columnJson.put("strip_outer_array", true);
                    columnJson.put("columns", String.join(",", columns));
                    columnJson.put("jsonpaths", "[" + String.join(",", jsonPaths) + "]");
                    columnMap.put(sourceTable, columnJson);
                }
            }
            engine.setColumnMap(columnMap);
        }
        sink.setEngine(engine);

        FlinkConf flinkConf = new FlinkConf();
        flinkConf.setJobName(job.getJobName());
        flinkConf.setMaxBatchInterval(job.getWindowSize());
        flinkConf.setMaxBatchSize(job.getMaxSize() * 1024 * 1024);
        flinkConf.setMaxBatchRows(job.getMaxCount() * 10000);

        DataSyncJobConf jobConf = new DataSyncJobConf();
        jobConf.setFlinkConf(flinkConf);
        jobConf.setSource(source);
        jobConf.setSink(sink);
        return jobConf;
    }

    public void stopJob(Long jobId) {
        // 停止 flink job
        doStop(jobId);
    }

    /**
     * 停止 flink 任务
     *
     * @param jobId 任务ID
     */
    private void doStop(Long jobId) {
        DataSyncJobInstanceEntity lastInstance = instanceService.getLastInstanceByJobId(jobId);
        if (ObjectUtils.isNotEmpty(lastInstance)) {
            // 状态为失败，直接停止
            if (Objects.equals(FlinkStatusEnum.FAILED.getName(), lastInstance.getStatus())) {
                doStopWithoutSavePoint(lastInstance);
            } else {
                doStopWithSavePoint(lastInstance);
            }
        }
    }

    /**
     * 正常停止任务，并保存save point
     *
     * @param instance 任务实例
     */
    private void doStopWithSavePoint(DataSyncJobInstanceEntity instance) {
        String applicationId = instance.getApplicationId();
        String flinkJobId = monitorService.getFlinkJobId(applicationId);
        String savePoint = null;
        for (int i = 0; i < 3; i++) {
            try {
                savePoint = YarnUtil.stopYarnJob(defaultHdfs, hadoopUserName, applicationId, flinkJobId, flinkConfPath);
                log.info("savePoint：{}", savePoint);
                if (ObjectUtils.isNotEmpty(savePoint)) {
                    instance.setStatus(FlinkStatusEnum.STOP.getStatus());
                    instance.setSavepoint(savePoint);
                    instance.updateById();
                    break;
                }
            } catch (Exception e) {
                log.error("savePoint获取失败，重试第[{}]次", i + 1, e);
            }
        }
        if (ObjectUtils.isEmpty(savePoint)) {
            try {
                log.info("savePoint获取失败，cancel任务：{}", applicationId);
                YarnUtil.cancelYarnJob(defaultHdfs, hadoopUserName, applicationId, flinkJobId, flinkConfPath);
                instance.setStatus(FlinkStatusEnum.STOP.getStatus());
                instance.setSavepoint(null);
                instance.updateById();
            } catch (Exception e) {
                log.error("cancel任务失败：{}", applicationId, e);
                instance.setStatus(FlinkStatusEnum.STOP.getStatus());
                instance.setSavepoint(null);
                instance.updateById();
            }
        }
    }

    /**
     * 停止任务
     *
     * @param instance 任务实例
     */
    private void doStopWithoutSavePoint(DataSyncJobInstanceEntity instance) {
        DataSyncJobEntity job = jobService.getById(instance.getJobId());
        if (Objects.isNull(job)) {
            throw new ApiException(ErrorCode.Internal.DB_INTERNAL_ERROR, "任务不存在");
        }
        if (!FlinkStatusEnum.couldStop(instance.getStatus())) {
            log.warn("当前状态无法停止：{}", instance.getStatus());
            instance.setStatus(FlinkStatusEnum.STOP.getStatus());
            instance.updateById();
        } else {
            String applicationId = instance.getApplicationId();
            String flinkJobId = monitorService.getFlinkJobId(applicationId);
            try {
                // 停止 flink 任务
                YarnUtil.cancelYarnJob(defaultHdfs, hadoopUserName, applicationId, flinkJobId, flinkConfPath);
                // 修改任务状态
                instance.setStatus(FlinkStatusEnum.STOP.getStatus());
                instance.setFinishedTime(new Date());
                instance.updateById();
            } catch (Exception e) {
                log.error("flink 任务停止失败：", e);
                throw new ApiException(ErrorCode.Internal.UNKNOWN_ERROR, "flink 任务停止失败");
            }
        }
    }
}
