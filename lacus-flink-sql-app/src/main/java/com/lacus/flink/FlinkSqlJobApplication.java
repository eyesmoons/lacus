package com.lacus.flink;

import com.lacus.flink.dto.CheckPointParam;
import com.lacus.flink.dto.FlinkJobParam;
import com.lacus.flink.enums.CheckPointParameterEnums;
import com.lacus.flink.enums.FlinkJobTypeEnum;
import com.lacus.flink.enums.StateBackendEnum;
import com.lacus.flink.parser.SqlFileParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.internal.TableEnvironmentInternal;
import org.apache.flink.table.delegation.Parser;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.SinkModifyOperation;
import org.apache.flink.table.operations.command.SetOperation;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class FlinkSqlJobApplication {

    public static void main(String[] args) {
        try {
            FlinkJobParam flinkJobParam = buildParam(args);
            if (Objects.isNull(flinkJobParam.getJobTypeEnum())) {
                throw new RuntimeException("任务类型不能为空");
            }
            List<String> sqlList = SqlFileParser.parserSql(Files.readAllLines(Paths.get(flinkJobParam.getSqlPath())));
            TableEnvironment tableEnv = null;
            if (FlinkJobTypeEnum.BATCH_SQL.equals(flinkJobParam.getJobTypeEnum())) {
                tableEnv = TableEnvironment.create(EnvironmentSettings.newInstance().inBatchMode().build());
            } else if (FlinkJobTypeEnum.STREAMING_SQL.equals(flinkJobParam.getJobTypeEnum())) {
                StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
                tableEnv = StreamTableEnvironment.create(env, EnvironmentSettings.newInstance().inStreamingMode().build());
                setCheckpoint(env, flinkJobParam.getCheckPointParam());
            }
            JobID jobID = executeSql(sqlList, tableEnv);
            log.info("任务提交成功: {}", jobID);
        } catch (Exception e) {
            log.error("任务执行失败：", e);
        }
    }

    /**
     * 执行sql
     *
     * @param sqlList  sql集合
     * @param tableEnv table环境
     */
    public static JobID executeSql(List<String> sqlList, TableEnvironment tableEnv) {
        Parser parser = ((TableEnvironmentInternal) tableEnv).getParser();
        List<ModifyOperation> modifyOperationList = new ArrayList<>();
        for (String sql : sqlList) {
            Operation operation = parser.parse(sql).get(0);
            log.info("operation: {}", operation.getClass().getSimpleName());
            switch (operation.getClass().getSimpleName()) {
                case "SetOperation":
                    SetOperation setOperation = (SetOperation) operation;
                    if (setOperation.getKey().isPresent() && setOperation.getValue().isPresent()) {
                        tableEnv.getConfig().getConfiguration().setString(setOperation.getKey().get(), setOperation.getValue().get());
                    }
                    break;
                case "DropTableOperation":
                case "DropCatalogFunctionOperation":
                case "DropCatalogOperation":
                case "DropDatabaseOperation":
                case "DropViewOperation":
                case "CreateTableOperation":
                case "CreateViewOperation":
                case "DropTempSystemFunctionOperation":
                case "AlterCatalogFunctionOperation":
                case "UseCatalogOperation":
                case "UseDatabaseOperation":
                case "LoadModuleOperation":
                case "UnloadModuleOperation":
                case "CreateDatabaseOperation":
                case "CreateCatalogOperation":
                case "CreateTableASOperation":
                case "CreateCatalogFunctionOperation":
                case "CreateTempSystemFunctionOperation":
                case "AlterTableOperation":
                case "AlterViewOperation":
                case "AlterDatabaseOperation":
                case "NopOperation":
                    ((TableEnvironmentInternal) tableEnv).executeInternal(parser.parse(sql).get(0));
                    break;
                case "SinkModifyOperation":
                    modifyOperationList.add((SinkModifyOperation) operation);
                    break;
                case "ShowTablesOperation":
                case "ShowCreateTableOperation":
                case "ShowCurrentCatalogOperation":
                case "ShowCurrentDatabaseOperation":
                case "ShowDatabasesOperation":
                case "ShowFunctionsOperation":
                case "ShowModulesOperation":
                case "ShowPartitionsOperation":
                case "ShowViewsOperation":
                case "ExplainOperation":
                case "ShowCatalogsOperation":
                case "DescribeTableOperation":
                    tableEnv.executeSql(sql).print();
                    break;
                case "BeginStatementSetOperation":
                case "EndStatementSetOperation":
                    break;
                default:
                    throw new RuntimeException("不支持的操作, operation: " + operation.getClass().getSimpleName() + ", sql: " + sql);
            }
        }
        TableResult tableResult = ((TableEnvironmentInternal) tableEnv).executeInternal(modifyOperationList);
        if (tableResult.getJobClient().isPresent()) {
            return tableResult.getJobClient().get().getJobID();
        }
        throw new RuntimeException("获取JobID失败");
    }

    /**
     * 构建参数
     *
     * @param args 参数
     */
    private static FlinkJobParam buildParam(String[] args) {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        String sqlPath = parameterTool.get("sql");
        if (StringUtils.isEmpty(sqlPath)) {
            throw new NullPointerException("-sql参数为必填项");
        }
        FlinkJobParam.FlinkJobParamBuilder paramBuilder = FlinkJobParam.builder()
                .sqlPath(sqlPath)
                .checkPointParam(buildCheckPointParam(parameterTool));
        String type = parameterTool.get("jobType");
        if (StringUtils.isNotEmpty(type)) {
            paramBuilder.jobTypeEnum(FlinkJobTypeEnum.getJobTypeEnum(type));
        }
        return paramBuilder.build();
    }

    private static CheckPointParam buildCheckPointParam(ParameterTool parameterTool) {
        String checkpointDir = parameterTool.get(CheckPointParameterEnums.checkpointDir.name(), "");
        if (StringUtils.isEmpty(checkpointDir)) {
            return null;
        }
        String checkpointingMode = parameterTool.get(CheckPointParameterEnums.checkpointingMode.name(), CheckpointingMode.EXACTLY_ONCE.name());
        String checkpointInterval = parameterTool.get(CheckPointParameterEnums.checkpointInterval.name(), "");
        String checkpointTimeout = parameterTool.get(CheckPointParameterEnums.checkpointTimeout.name(), "");
        String tolerableCheckpointFailureNumber = parameterTool.get(CheckPointParameterEnums.tolerableCheckpointFailureNumber.name(), "");
        String externalizedCheckpointCleanup = parameterTool.get(CheckPointParameterEnums.externalizedCheckpointCleanup.name(), "");
        String stateBackendType = parameterTool.get(CheckPointParameterEnums.stateBackendType.name(), "");
        String enableIncremental = parameterTool.get(CheckPointParameterEnums.enableIncremental.name(), "");
        CheckPointParam checkPointParam = new CheckPointParam();
        checkPointParam.setCheckpointDir(checkpointDir);
        checkPointParam.setCheckpointingMode(checkpointingMode);
        if (StringUtils.isNotEmpty(checkpointInterval)) {
            checkPointParam.setCheckpointInterval(Long.parseLong(checkpointInterval));
        }
        if (StringUtils.isNotEmpty(checkpointTimeout)) {
            checkPointParam.setCheckpointTimeout(Long.parseLong(checkpointTimeout));
        }
        if (StringUtils.isNotEmpty(tolerableCheckpointFailureNumber)) {
            checkPointParam.setTolerableCheckpointFailureNumber(Integer.parseInt(tolerableCheckpointFailureNumber));
        }
        if (StringUtils.isNotEmpty(externalizedCheckpointCleanup)) {
            checkPointParam.setExternalizedCheckpointCleanup(externalizedCheckpointCleanup);
        }
        checkPointParam.setStateBackendEnum(StateBackendEnum.getStateBackend(stateBackendType));
        if (StringUtils.isNotEmpty(enableIncremental)) {
            checkPointParam.setEnableIncremental(Boolean.parseBoolean(enableIncremental.trim()));
        }
        log.info("checkPointParam: {}", checkPointParam);
        return checkPointParam;
    }

    public static void setCheckpoint(StreamExecutionEnvironment env, CheckPointParam checkPointParam) {
        if (Objects.isNull(checkPointParam)) {
            log.warn("未开启Checkpoint");
            return;
        }
        if (StringUtils.isEmpty(checkPointParam.getCheckpointDir())) {
            throw new RuntimeException("checkpoint目录不存在");
        }

        // 默认每60s保存一次checkpoint
        env.enableCheckpointing(checkPointParam.getCheckpointInterval());
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        if (StringUtils.isEmpty(checkPointParam.getCheckpointingMode()) || CheckpointingMode.EXACTLY_ONCE.name().equalsIgnoreCase(checkPointParam.getCheckpointingMode())) {
            checkpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        } else {
            checkpointConfig.setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);
        }

        // 默认超时10 minutes.
        checkpointConfig.setCheckpointTimeout(checkPointParam.getCheckpointTimeout());
        // checkpoint最小间隔
        checkpointConfig.setMinPauseBetweenCheckpoints(500);
        // 同一时间只允许进行一个检查点
        checkpointConfig.setMaxConcurrentCheckpoints(2);
        // 设置失败次数
        checkpointConfig.setTolerableCheckpointFailureNumber(checkPointParam.getTolerableCheckpointFailureNumber());
        // 设置后端状态
        setStateBackend(env, checkPointParam);

        if (checkPointParam.getExternalizedCheckpointCleanup() != null) {
            if (checkPointParam.getExternalizedCheckpointCleanup().equalsIgnoreCase(CheckpointConfig.ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION.name())) {
                env.getCheckpointConfig().setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION);
            } else if (checkPointParam.getExternalizedCheckpointCleanup().equalsIgnoreCase(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION.name())) {
                env.getCheckpointConfig().setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
            }
        }
    }

    private static void setStateBackend(StreamExecutionEnvironment env, CheckPointParam checkPointParam) {
        switch (checkPointParam.getStateBackendEnum()) {
            case MEMORY:
                env.setStateBackend(new HashMapStateBackend());
                break;
            case FILE:
                env.setStateBackend(new HashMapStateBackend());
                env.getCheckpointConfig().setCheckpointStorage(new FileSystemCheckpointStorage(checkPointParam.getCheckpointDir()));
                break;
            case ROCKSDB:
                if (checkPointParam.getEnableIncremental() != null) {
                    env.setStateBackend(new EmbeddedRocksDBStateBackend(checkPointParam.getEnableIncremental()));
                } else {
                    env.setStateBackend(new EmbeddedRocksDBStateBackend());
                }
                env.getCheckpointConfig().setCheckpointStorage(new FileSystemCheckpointStorage(checkPointParam.getCheckpointDir()));
                break;
            default:
                throw new RuntimeException("不支持的StateBackend: " + checkPointParam.getStateBackendEnum());
        }
    }
}
