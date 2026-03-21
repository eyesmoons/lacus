package com.lacus.domain.dataquality;

import com.lacus.common.exception.CustomException;
import com.lacus.dao.dataquality.entity.DqCheckResultEntity;
import com.lacus.dao.dataquality.entity.DqExecutionLogEntity;
import com.lacus.dao.dataquality.entity.DqRuleEntity;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.service.dataquality.IDqCheckResultService;
import com.lacus.service.dataquality.IDqExecutionLogService;
import com.lacus.service.dataquality.IDqRuleService;
import com.lacus.service.metadata.IMetaDataSourceService;
import com.lacus.utils.CommonPropertyUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

import static com.lacus.common.constant.Constants.HADOOP_CONF_DIR;
import static com.lacus.common.constant.Constants.JAVA_HOME;
import static com.lacus.common.constant.Constants.SPARK_CLIENT_HOME;

/**
 * 数据质量任务提交业务逻辑
 * 通过 SparkLauncher 将 DataQualityConfiguration JSON 提交到 lacus-dataquality.jar 执行
 */
@Slf4j
@Service
public class DqTaskBusiness {

    /**
     * lacus-dataquality fat jar 的路径配置 key（在 common.properties 中配置）
     */
    private static final String DQ_JAR_PATH_KEY = "dataquality.jar.path";

    /**
     * 数据质量引擎主类
     */
    private static final String DQ_MAIN_CLASS = "com.lacus.dataquality.DataQualityEngine";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private IDqRuleService dqRuleService;

    @Autowired
    private IDqExecutionLogService dqExecutionLogService;

    @Autowired
    private IDqCheckResultService dqCheckResultService;

    @Autowired
    private IMetaDataSourceService metaDataSourceService;

    @Autowired
    private DqRuleBusiness dqRuleBusiness;

    /**
     * 提交数据质量规则执行任务
     *
     * @param ruleId 规则ID
     * @return 执行记录ID
     */
    public Long submitTask(Long ruleId) {
        DqRuleEntity rule = dqRuleService.getById(ruleId);
        if (ObjectUtils.isEmpty(rule)) {
            throw new CustomException("规则[" + ruleId + "]不存在");
        }
        if (!Integer.valueOf(1).equals(rule.getEnabled())) {
            throw new CustomException("规则[" + rule.getRuleName() + "]已禁用，无法提交");
        }
        if (ObjectUtils.isEmpty(rule.getRuleConfig())) {
            throw new CustomException("规则[" + rule.getRuleName() + "]配置为空，无法提交");
        }

        // 创建执行记录
        DqExecutionLogEntity logEntity = new DqExecutionLogEntity();
        logEntity.setRuleId(ruleId);
        logEntity.setRuleName(rule.getRuleName());
        logEntity.setStatus("SUBMITTED");
        logEntity.setStartTime(new Date());
        logEntity.setCreateTime(new Date());
        // 写入数据源/库/表/字段信息快照
        logEntity.setDatasourceId(rule.getDatasourceId());
        logEntity.setDbName(rule.getDbName());
        logEntity.setTableName(rule.getTableName());
        logEntity.setFieldNames(rule.getFieldNames());
        if (rule.getDatasourceId() != null) {
            try {
                MetaDatasourceEntity ds = metaDataSourceService.getById(rule.getDatasourceId());
                if (ds != null) {
                    logEntity.setDatasourceName(ds.getDatasourceName());
                }
            } catch (Exception e) {
                log.warn("Failed to load datasource name for ruleId={}", ruleId, e);
            }
        }
        dqExecutionLogService.save(logEntity);

        Long logId = logEntity.getId();
        log.info("DQ task submitted, ruleId={}, logId={}", ruleId, logId);

        // 异步提交 Spark 任务
        submitSparkAsync(rule, logId);

        return logId;
    }

    /**
     * 查询执行状态
     */
    public DqExecutionLogEntity getTaskStatus(Long logId) {
        DqExecutionLogEntity logEntity = dqExecutionLogService.getById(logId);
        if (ObjectUtils.isEmpty(logEntity)) {
            throw new CustomException("执行记录[" + logId + "]不存在");
        }
        return logEntity;
    }

    /**
     * 停止任务（将状态标记为 STOPPED）
     */
    public void stopTask(Long logId) {
        DqExecutionLogEntity logEntity = dqExecutionLogService.getById(logId);
        if (ObjectUtils.isEmpty(logEntity)) {
            throw new CustomException("执行记录[" + logId + "]不存在");
        }
        String currentStatus = logEntity.getStatus();
        if ("SUCCESS".equals(currentStatus) || "FAILED".equals(currentStatus) || "STOPPED".equals(currentStatus)) {
            throw new CustomException("任务已处于终态[" + currentStatus + "]，无法停止");
        }
        logEntity.setStatus("STOPPED");
        logEntity.setEndTime(new Date());
        dqExecutionLogService.updateById(logEntity);
        log.info("DQ task stopped, logId={}", logId);
    }

    /**
     * 异步提交 Spark 任务
     */
    @Async
    public void submitSparkAsync(DqRuleEntity rule, Long logId) {
        try {
            // 构建 Spark 环境变量
            Map<String, String> env = new HashMap<>();
            env.put("HADOOP_CONF_DIR", CommonPropertyUtils.getString(HADOOP_CONF_DIR));
            env.put("JAVA_HOME", CommonPropertyUtils.getString(JAVA_HOME));

            // 获取 DQ jar 路径
            String dqJarPath = CommonPropertyUtils.getString(DQ_JAR_PATH_KEY);
            if (ObjectUtils.isEmpty(dqJarPath)) {
                throw new CustomException("dataquality.jar.path 未配置，请在 common.properties 中添加");
            }

            // 动态构建包含 DQ_RESULT writer 的 Spark 配置（注入 logId / ruleId / JDBC 连接信息）
            String sparkConfigJson = dqRuleBusiness.buildSparkConfig(rule, logId, rule.getId());

            // 将本次执行的配置 JSON 快照和 HDFS 错误路径持久化到执行记录
            DqExecutionLogEntity snapshot = dqExecutionLogService.getById(logId);
            if (snapshot != null) {
                snapshot.setTaskConfig(sparkConfigJson);
                snapshot.setErrorDataPath(dqRuleBusiness.buildErrorDataPath(rule.getId(), logId));
                dqExecutionLogService.updateById(snapshot);
            }

            // 解析 sparkParams（前端配置的资源参数）
            Map<String, Object> sparkParamsMap = parseSparkParams(rule.getSparkParams());
            String deployModeStr   = getStr(sparkParamsMap, "deployMode",     "YARN_CLIENT");
            String masterAddress   = getStr(sparkParamsMap, "masterAddress",  "");
            String queue           = getStr(sparkParamsMap, "queue",          "");
            String otherParams     = getStr(sparkParamsMap, "otherParams",    "");
            int driverCores    = getInt(sparkParamsMap, "driverCores",    1);
            int driverMemory   = getInt(sparkParamsMap, "driverMemory",   1);
            int numExecutors   = getInt(sparkParamsMap, "numExecutors",   1);
            int executorMemory = getInt(sparkParamsMap, "executorMemory", 1);
            int executorCores  = getInt(sparkParamsMap, "executorCores",  1);

            // 根据 deployMode 枚举推导 master 地址（与 SparkOperationServiceImpl.getMaster 保持一致）
            String master;
            String actualDeployMode = null; // SparkLauncher.setDeployMode 只接受 client/cluster
            switch (deployModeStr.toUpperCase()) {
                case "LOCAL":
                    master = "local[*]";
                    break;
                case "STANDALONE_CLIENT":
                    master = "spark://" + masterAddress;
                    actualDeployMode = "client";
                    break;
                case "STANDALONE_CLUSTER":
                    master = "spark://" + masterAddress;
                    actualDeployMode = "cluster";
                    break;
                case "YARN_CLIENT":
                    master = "yarn";
                    actualDeployMode = "client";
                    break;
                case "YARN_CLUSTER":
                    master = "yarn";
                    actualDeployMode = "cluster";
                    break;
                case "K8S_CLIENT":
                    master = "k8s://" + masterAddress;
                    actualDeployMode = "client";
                    break;
                case "K8S_CLUSTER":
                    master = "k8s://" + masterAddress;
                    actualDeployMode = "cluster";
                    break;
                default:
                    master = "yarn";
                    actualDeployMode = "client";
                    log.warn("Unknown deployMode '{}', fallback to yarn/client", deployModeStr);
            }

            // 构建 SparkLauncher
            SparkLauncher launcher = new SparkLauncher(env)
                    .setSparkHome(CommonPropertyUtils.getString(SPARK_CLIENT_HOME))
                    .setAppName("DQ-" + rule.getRuleName())
                    .setMainClass(DQ_MAIN_CLASS)
                    .setAppResource(dqJarPath)
                    .setMaster(master)
                    .setConf("spark.sql.crossJoin.enabled", "true")
                    .setConf("spark.driver.cores",   String.valueOf(driverCores))
                    .setConf("spark.driver.memory",  driverMemory + "g")
                    .setConf("spark.executor.instances", String.valueOf(numExecutors))
                    .setConf("spark.executor.memory", executorMemory + "g")
                    .setConf("spark.executor.cores",  String.valueOf(executorCores))
                    .addAppArgs(sparkConfigJson)
                    .setVerbose(true)
                    .redirectError();

            // 非 local 模式设置 deployMode
            if (actualDeployMode != null) {
                launcher.setDeployMode(actualDeployMode);
            }

            // yarn 模式开启等待完成
            if (master.startsWith("yarn")) {
                launcher.setConf("spark.yarn.submit.waitAppCompletion", "true");
            }

            // yarn 队列（选填，仅 YARN 模式有意义）
            if (StringUtils.isNotBlank(queue) && master.startsWith("yarn")) {
                launcher.setConf("spark.yarn.queue", queue);
            }

            // 其他参数（--conf key=value 行，每行一条）
            if (StringUtils.isNotBlank(otherParams)) {
                for (String line : otherParams.split("\n")) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("--conf ")) {
                        String kv = trimmed.substring(7).trim();
                        int eqIdx = kv.indexOf('=');
                        if (eqIdx > 0) {
                            launcher.setConf(kv.substring(0, eqIdx).trim(), kv.substring(eqIdx + 1).trim());
                        }
                    }
                }
            }

            // 更新状态为 RUNNING
            updateLogStatus(logId, "RUNNING", null, null, null);

            // 用于收集进程输出的滚动缓冲区（保留最后 300 行，避免 OOM）
            final ConcurrentLinkedDeque<String> outputBuffer = new ConcurrentLinkedDeque<>();
            final int MAX_LINES = 300;

            // 使用 StringBuilder 累积完整日志（用于持久化）
            final StringBuilder fullLogBuffer = new StringBuilder();
            final Object logLock = new Object(); // 锁对象，保证线程安全

            // 使用 launch() 获取 Process 以便捕获完整输出流
            launcher.redirectError(); // 将 stderr 合并到 stdout
            Process process = launcher.launch();
            log.info("DQ Spark process launched, logId={}", logId);

            // 异步读取进程输出（stdout 已合并 stderr）
            Thread logReaderThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("[DQ-{}] {}", logId, line);
                        
                        // 添加到滚动缓冲区（用于失败时构建摘要）
                        outputBuffer.addLast(line);
                        while (outputBuffer.size() > MAX_LINES) {
                            outputBuffer.pollFirst();
                        }
                        
                        // 添加到完整日志缓冲区（用于持久化）
                        synchronized (logLock) {
                            fullLogBuffer.append(line).append("\n");
                        }
                    }
                } catch (IOException e) {
                    log.warn("DQ log reader interrupted, logId={}", logId, e);
                }
            }, "dq-log-reader-" + logId);
            logReaderThread.setDaemon(true);
            logReaderThread.start();

            // 等待进程结束并根据退出码更新状态
            try {
                int exitCode = process.waitFor();
                logReaderThread.join(5000); // 最多等待 5 秒让日志读完
                
                // 获取完整日志
                String fullLog;
                synchronized (logLock) {
                    fullLog = fullLogBuffer.toString();
                }
                
                String finalStatus;
                String errorMsg = null;
                if (exitCode == 0) {
                    finalStatus = "SUCCESS";
                    log.info("DQ Spark job finished successfully, logId={}", logId);
                    // 回填 dq_check_result.pass_flag（按 operator 比较 actual_value vs expected_value）
                    fillCheckResultPassFlag(logId);
                    // 回填 dq_execution_log 的 pass_flag 和 result_value
                    fillLogPassFlagAndResultValue(logId);
                } else {
                    finalStatus = "FAILED";
                    errorMsg = buildErrorSummary(outputBuffer, "exitCode=" + exitCode);
                    log.error("DQ Spark job FAILED (exitCode={}), logId={}, error summary:\n{}", exitCode, logId, errorMsg);
                }
                updateLogStatus(logId, finalStatus, null, new Date(), fullLog);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                String errorMsg = "任务等待被中断：" + e.getMessage();
                log.error("DQ Spark job wait interrupted, logId={}", logId, e);
                updateLogStatus(logId, "FAILED", null, new Date(), errorMsg);
            }

        } catch (IOException e) {
            String msg = buildExceptionMessage("提交Spark任务失败", e);
            log.error("Failed to submit DQ Spark job, logId={}", logId, e);
            updateLogStatus(logId, "FAILED", null, new Date(), msg);
        } catch (Exception e) {
            String msg = buildExceptionMessage("提交DQ任务时发生异常", e);
            log.error("Unexpected error while submitting DQ task, logId={}", logId, e);
            updateLogStatus(logId, "FAILED", null, new Date(), msg);
        }
    }

    /**
     * 从输出缓冲区构建错误摘要，优先提取含 ERROR/Exception 的关键行
     */
    private String buildErrorSummary(ConcurrentLinkedDeque<String> buffer, String suffix) {
        if (buffer.isEmpty()) {
            return "Spark任务失败，无可用输出日志。(" + suffix + ")";
        }
        // 优先收集含关键字的行
        String keyLines = buffer.stream()
                .filter(l -> l.contains("ERROR") || l.contains("Exception")
                        || l.contains("Error") || l.contains("WARN") || l.contains("Caused by"))
                .collect(Collectors.joining("\n"));
        // 再拼上最后 50 行作为上下文
        String tailLines = buffer.stream()
                .skip(Math.max(0, buffer.size() - 50))
                .collect(Collectors.joining("\n"));
        String combined = (keyLines.isEmpty() ? tailLines : keyLines + "\n---\n" + tailLines);
        return combined + "\n[" + suffix + "]";
    }

    /**
     * 构建异常错误描述（防止 getMessage() 为 null）
     */
    private String buildExceptionMessage(String prefix, Exception e) {
        String msg = e.getMessage();
        if (msg == null || msg.isEmpty()) {
            // 取 stacktrace 前 3 行
            StackTraceElement[] st = e.getStackTrace();
            StringBuilder sb = new StringBuilder(e.getClass().getName());
            for (int i = 0; i < Math.min(3, st.length); i++) {
                sb.append("\n  at ").append(st[i]);
            }
            msg = sb.toString();
        }
        return prefix + ": " + msg;
    }

    /**
     * 解析 sparkParams JSON 字符串为 Map，解析失败返回空 Map
     */
    private Map<String, Object> parseSparkParams(String sparkParamsJson) {
        if (StringUtils.isBlank(sparkParamsJson)) return new HashMap<>();
        try {
            return MAPPER.readValue(sparkParamsJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse sparkParams, use defaults. json={}", sparkParamsJson, e);
            return new HashMap<>();
        }
    }

    private String getStr(Map<String, Object> map, String key, String defaultVal) {
        Object v = map.get(key);
        return (v != null && !v.toString().isEmpty()) ? v.toString() : defaultVal;
    }

    private int getInt(Map<String, Object> map, String key, int defaultVal) {
        Object v = map.get(key);
        if (v == null) return defaultVal;
        try { return Integer.parseInt(v.toString()); } catch (Exception e) { return defaultVal; }
    }

    /**
     * 任务成功后，根据 actual_value vs expected_value + operator 回填 pass_flag
     */
    private void fillCheckResultPassFlag(Long logId) {
        try {
            List<DqCheckResultEntity> results = dqCheckResultService.listByLogId(logId);
            if (results == null || results.isEmpty()) return;
            for (DqCheckResultEntity r : results) {
                if (r.getActualValue() == null || r.getExpectedValue() == null
                        || r.getOperator() == null || r.getOperator().isEmpty()) {
                    continue;
                }
                boolean pass = evaluatePassFlag(r.getActualValue(), r.getExpectedValue(), r.getOperator());
                r.setPassFlag(pass ? 1 : 0);
                dqCheckResultService.updateById(r);
            }
            log.info("Filled pass_flag for {} check results, logId={}", results.size(), logId);
        } catch (Exception e) {
            log.warn("Failed to fill pass_flag for logId={}", logId, e);
        }
    }

    /**
     * 任务成功后，回填 dq_execution_log 的 pass_flag（全部通过才算通过）和 result_value（第一条明细的实际值）
     */
    private void fillLogPassFlagAndResultValue(Long logId) {
        try {
            List<DqCheckResultEntity> results = dqCheckResultService.listByLogId(logId);
            if (results == null || results.isEmpty()) return;
            DqExecutionLogEntity logEntity = dqExecutionLogService.getById(logId);
            if (logEntity == null) return;
            // pass_flag：所有明细均通过才算整体通过
            boolean allPass = results.stream()
                    .filter(r -> r.getPassFlag() != null)
                    .allMatch(r -> r.getPassFlag() == 1);
            logEntity.setPassFlag(allPass ? 1 : 0);
            // result_value：取第一条明细的 actualValue
            if (results.get(0).getActualValue() != null) {
                logEntity.setResultValue(results.get(0).getActualValue().toPlainString());
            }
            dqExecutionLogService.updateById(logEntity);
            log.info("Filled log pass_flag={}, result_value={}, logId={}", logEntity.getPassFlag(), logEntity.getResultValue(), logId);
        } catch (Exception e) {
            log.warn("Failed to fill log pass_flag/result_value for logId={}", logId, e);
        }
    }

    /**
     * 按操作符比较 actual vs expected，返回是否通过
     */
    private boolean evaluatePassFlag(java.math.BigDecimal actual, java.math.BigDecimal expected, String operator) {
        int cmp = actual.compareTo(expected);
        switch (operator.trim()) {
            case "=":  return cmp == 0;
            case "!=": return cmp != 0;
            case ">":  return cmp > 0;
            case ">=": return cmp >= 0;
            case "<":  return cmp < 0;
            case "<=": return cmp <= 0;
            default:
                log.warn("Unknown operator '{}', treating as pass", operator);
                return true;
        }
    }

    private void updateLogStatus(Long logId, String status, String appId, Date endTime, String logInfo) {
        DqExecutionLogEntity logEntity = dqExecutionLogService.getById(logId);
        if (logEntity == null) {
            log.warn("ExecutionLog not found when updating status: logId={}", logId);
            return;
        }
        logEntity.setStatus(status);
        if (appId != null) {
            logEntity.setSparkAppId(appId);
        }
        if (endTime != null) {
            logEntity.setEndTime(endTime);
        }
        if (logInfo != null) {
            logEntity.setLogInfo(logInfo);
        }
        dqExecutionLogService.updateById(logEntity);
    }
}
