package com.lacus.domain.dig;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.dig.entity.StJobEntity;
import com.lacus.dao.dig.entity.StJobInstanceEntity;
import com.lacus.dao.dig.entity.StTaskEntity;
import com.lacus.dao.dig.entity.StTaskRelationEntity;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.datasource.model.ConnectionParam;
import com.lacus.service.dig.IStJobInstanceService;
import com.lacus.service.dig.IStJobService;
import com.lacus.service.dig.IStTaskRelationService;
import com.lacus.service.dig.IStTaskService;
import com.lacus.service.metadata.IMetaDataSourceService;
import com.lacus.st.interfaces.StComponentInterface;
import com.lacus.st.loader.StComponentLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StJobInstanceBusiness {

    private static final Logger logger = LoggerFactory.getLogger(StJobInstanceBusiness.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 从组件元数据获取 DAG 中的连接器 key（@StComponent.connectorKey），未配置则用 connectorName。
     */
    private String getDagConnectorKey(String connectorName) {
        Map<String, Object> meta = stComponentLoader.getComponentMetadata(connectorName);
        if (meta != null && meta.containsKey("connectorKey")) {
            String key = (String) meta.get("connectorKey");
            if (StringUtils.isNotBlank(key)) {
                return key;
            }
        }
        return connectorName;
    }

    @Autowired
    private IStJobService stJobService;

    @Autowired
    private IStTaskService stTaskService;

    @Autowired
    private IStTaskRelationService stTaskRelationService;

    @Autowired
    private IStJobInstanceService stJobInstanceService;

    @Autowired
    private IMetaDataSourceService dataSourceService;

    @Autowired
    private StComponentLoader stComponentLoader;

    public StJobInstanceEntity createInstance(Long jobId, String jobConfig) {
        try {
            StJobEntity job = stJobService.getById(jobId);
            if (job == null) {
                throw new CustomException("任务[" + jobId + "]不存在");
            }

            // 创建任务实例实体
            StJobInstanceEntity instance = new StJobInstanceEntity();
            instance.setInstanceId(null); // 由数据库自动生成
            instance.setJobId(jobId);
            instance.setInstanceName(job.getJobName() + "_" + System.currentTimeMillis());
            instance.setJobConfig(jobConfig);
            instance.setStatus(0); // 0表示运行中
            instance.setStartTime(new Date());

            // 保存任务实例
            boolean saved = stJobInstanceService.save(instance);
            if (!saved) {
                throw new CustomException("创建任务实例失败");
            }

            logger.info("Successfully created job instance {} for job {}", instance.getInstanceId(), jobId);
            return instance;

        } catch (Exception e) {
            logger.error("Failed to create job instance for job {}", jobId, e);
            throw new CustomException("创建任务实例失败", e);
        }
    }

    public String buildJobJson(StJobEntity job, List<StTaskEntity> taskList, List<StTaskRelationEntity> taskRelationList) {
        try {
            logger.debug("Building job HOCON for job {} with {} tasks and {} relations", job.getJobId(), taskList.size(), taskRelationList.size());

            // 构建Seatunnel任务JSON配置
            JSONObject jobJson = new JSONObject();

            // 添加env配置
            JSONObject env = new JSONObject();
            env.put("parallelism", 1); // 默认并行度
            env.put("job.mode", "BATCH"); // 默认批处理模式
            jobJson.put("env", env);

            // 分离不同类型的task
            List<StTaskEntity> sources = taskList.stream()
                    .filter(task -> "SOURCE".equals(task.getConnectorType()))
                    .collect(Collectors.toList());

            List<StTaskEntity> transforms = taskList.stream()
                    .filter(task -> "TRANSFORM".equals(task.getConnectorType()))
                    .collect(Collectors.toList());

            List<StTaskEntity> sinks = taskList.stream()
                    .filter(task -> "SINK".equals(task.getConnectorType()))
                    .collect(Collectors.toList());

            // 添加source配置
            if (!sources.isEmpty()) {
                JSONObject sourcesObject = new JSONObject();
                for (StTaskEntity source : sources) {
                    JSONObject sourceConfig = JSON.parseObject(source.getConnectionConfig());
                    // 如果配置中包含datasourceId，则替换为实际的连接参数
                    Long datasourceId = null;
                    if (sourceConfig.containsKey("datasourceId")) {
                        datasourceId = sourceConfig.getLong("datasourceId");
                        sourceConfig.remove("datasourceId");
                        // 从数据源服务获取实际连接参数
                        JSONObject actualParams = getActualConnectionParams(datasourceId);
                        if (actualParams.containsKey("database")) {
                            actualParams.remove("database");
                        }
                        // 将实际参数合并到配置中
                        sourceConfig.putAll(actualParams);
                    }

                    // 使用组件特定的buildTaskConfig方法构建配置
                    try {
                        StComponentInterface component = stComponentLoader.createComponent(source.getConnectorName());
                        if (component != null) {
                            // 调用组件特定的buildTaskConfig方法
                            sourceConfig = component.buildTaskConfig(sourceConfig, datasourceId);
                        }
                    } catch (Exception e) {
                        logger.warn("调用组件 {} 的buildTaskConfig方法失败: {}", source.getConnectorName(), e.getMessage());
                        // 如果调用失败，继续使用原始配置
                    }
                    sourcesObject.put(getDagConnectorKey(source.getConnectorName()), sourceConfig);
                }
                jobJson.put("source", sourcesObject);
            }

            // 添加transform配置
            if (!transforms.isEmpty()) {
                JSONObject transformsObject = new JSONObject();
                for (StTaskEntity transform : transforms) {
                    JSONObject transformConfig = JSON.parseObject(transform.getConnectionConfig());

                    // 使用组件特定的buildTaskConfig方法构建配置
                    try {
                        StComponentInterface component = stComponentLoader.createComponent(transform.getConnectorName());
                        if (component != null) {
                            // 调用组件特定的buildTaskConfig方法
                            transformConfig = component.buildTaskConfig(transformConfig, null);
                        }
                    } catch (Exception e) {
                        logger.warn("调用组件 {} 的buildTaskConfig方法失败: {}", transform.getConnectorName(), e.getMessage());
                        // 如果调用失败，继续使用原始配置
                    }
                    transformsObject.put(getDagConnectorKey(transform.getConnectorName()), transformConfig);
                }
                jobJson.put("transform", transformsObject);
            }

            // 添加sink配置
            if (!sinks.isEmpty()) {
                JSONObject sinksObject = new JSONObject();
                for (StTaskEntity sink : sinks) {
                    JSONObject sinkConfig = JSON.parseObject(sink.getConnectionConfig());
                    // 如果配置中包含datasourceId，则替换为实际的连接参数
                    Long datasourceId = null;
                    if (sinkConfig.containsKey("datasourceId")) {
                        datasourceId = sinkConfig.getLong("datasourceId");
                        sinkConfig.remove("datasourceId");
                        // 从数据源服务获取实际连接参数
                        JSONObject actualParams = getActualConnectionParams(datasourceId);
                        if (actualParams.containsKey("database")) {
                            actualParams.remove("database");
                        }
                        // 将实际参数合并到配置中
                        sinkConfig.putAll(actualParams);
                    }

                    // 使用组件特定的buildTaskConfig方法构建配置
                    try {
                        StComponentInterface component = stComponentLoader.createComponent(sink.getConnectorName());
                        if (component != null) {
                            // 调用组件特定的buildTaskConfig方法
                            sinkConfig = component.buildTaskConfig(sinkConfig, datasourceId);
                        }
                    } catch (Exception e) {
                        logger.warn("调用组件 {} 的buildTaskConfig方法失败: {}", sink.getConnectorName(), e.getMessage());
                        // 如果调用失败，继续使用原始配置
                    }
                    sinksObject.put(getDagConnectorKey(sink.getConnectorName()), sinkConfig);
                }
                jobJson.put("sink", sinksObject);
            }

            String jobConfigStr = formatToHocon(jobJson);
            logger.debug("Generated job config HOCON: \n{}", jobConfigStr);

            return jobConfigStr;

        } catch (Exception e) {
            logger.error("Failed to build job HOCON for job {}", job.getJobId(), e);
            throw new CustomException("构建任务配置失败", e);
        }
    }

    /**
     * 将JSONObject转换为HOCON格式字符串
     */
    private String formatToHocon(JSONObject json) {
        StringBuilder sb = new StringBuilder();
        // 按照SeaTunnel规范顺序排列：env, source, transform, sink
        if (json.containsKey("env")) {
            appendHocon(sb, "env", json.get("env"), 0);
            sb.append("\n");
        }
        if (json.containsKey("source")) {
            appendHocon(sb, "source", json.get("source"), 0);
            sb.append("\n");
        }
        if (json.containsKey("transform")) {
            appendHocon(sb, "transform", json.get("transform"), 0);
            sb.append("\n");
        }
        if (json.containsKey("sink")) {
            appendHocon(sb, "sink", json.get("sink"), 0);
            sb.append("\n");
        }
        return sb.toString();
    }

    private void appendHocon(StringBuilder sb, String key, Object value, int indent) {
        String indentStr = StringUtils.repeat("  ", indent);
        if (value instanceof Map) {
            sb.append(indentStr).append(key).append(" {\n");
            Map<String, Object> map = (Map<String, Object>) value;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                appendHocon(sb, entry.getKey(), entry.getValue(), indent + 1);
            }
            sb.append(indentStr).append("}\n");
        } else if (value instanceof List) {
            sb.append(indentStr).append(key).append(" = [\n");
            List<Object> list = (List<Object>) value;
            for (int i = 0; i < list.size(); i++) {
                Object item = list.get(i);
                if (item instanceof Map) {
                    sb.append(indentStr).append("  {\n");
                    Map<String, Object> itemMap = (Map<String, Object>) item;
                    for (Map.Entry<String, Object> entry : itemMap.entrySet()) {
                        appendHocon(sb, entry.getKey(), entry.getValue(), indent + 2);
                    }
                    sb.append(indentStr).append("  }");
                } else {
                    sb.append(indentStr).append("  ").append(formatValue(item));
                }
                if (i < list.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append(indentStr).append("]\n");
        } else {
            sb.append(indentStr).append(key).append(" = ").append(formatValue(value)).append("\n");
        }
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return String.valueOf(value);
        }
        if (value instanceof String) {
            String str = (String) value;
            // Handle booleans
            if ("true".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str)) {
                return str.toLowerCase();
            }
            // If it's a string, we ALWAYS quote it, unless it's a boolean.
            // This ensures "123456" stays as "123456" and not 123456.
            return "\"" + str.replace("\"", "\\\"") + "\"";
        }
        // For other types, convert to string and quote.
        return "\"" + String.valueOf(value).replace("\"", "\\\"") + "\"";
    }


    /**
     * 根据数据源ID获取实际连接参数
     *
     * @param datasourceId 数据源ID
     * @return 包含实际连接参数的JSONObject
     */
    private JSONObject getActualConnectionParams(Long datasourceId) {
        try {
            logger.debug("Fetching connection parameters for datasource ID: {}", datasourceId);

            // 从数据源服务获取数据源实体
            MetaDatasourceEntity datasourceEntity = dataSourceService.getById(datasourceId);
            if (datasourceEntity == null) {
                throw new CustomException("数据源ID " + datasourceId + " 不存在");
            }

            // 解析连接参数
            ConnectionParam connectionParam = JSON.parseObject(datasourceEntity.getConnectionParams(), ConnectionParam.class);

            // 构建实际连接参数JSON对象
            JSONObject actualParams = new JSONObject();

            // 添加主机名
            if (connectionParam.getHost() != null) {
                actualParams.put("host", connectionParam.getHost());
            }

            // 添加端口
            if (connectionParam.getPort() != null) {
                actualParams.put("port", connectionParam.getPort());
            }

            // 添加数据库名
            if (connectionParam.getDatabase() != null) {
                actualParams.put("database", connectionParam.getDatabase());
            }

            // 添加用户名
            if (connectionParam.getUsername() != null) {
                actualParams.put("username", connectionParam.getUsername());
            }

            // 添加密码
            if (connectionParam.getPassword() != null) {
                actualParams.put("password", connectionParam.getPassword());
            }

            // 添加其他参数
            if (connectionParam.getParamValues() != null) {
                for (Map.Entry<String, Object> entry : connectionParam.getParamValues().entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    // 避免重复添加已经处理过的参数
                    if (!actualParams.containsKey(key) &&
                            !"host".equals(key) && !"port".equals(key) &&
                            !"database".equals(key) && !"username".equals(key) && !"user".equals(key) &&
                            !"password".equals(key)) {
                        actualParams.put(key, value);
                    }
                }
            }

            // 根据数据源类型构建JDBC URL（如果需要）
            String dataSourceType = datasourceEntity.getType();
            if (dataSourceType != null) {
                // 特殊处理Doris数据源，Seatunnel Doris连接器需要特定的参数格式
                if ("DORIS".equalsIgnoreCase(dataSourceType)) {
                    // 为Doris连接器构建fenodes参数 (host:port格式)
                    String host = connectionParam.getHost();
                    Integer port = connectionParam.getPort();
                    if (host != null && port != null) {
                        actualParams.put("fenodes", host + ":" + port);
                    }

                    // 添加query-port参数
                    Integer queryPort = connectionParam.getValue("query-port");
                    if (queryPort != null) {
                        actualParams.put("query-port", queryPort);
                    } else {
                        // 如果没有明确指定query-port，默认使用9030
                        actualParams.put("query-port", 9030);
                    }

                    // 保持username和password参数
                    if (connectionParam.getUsername() != null) {
                        actualParams.put("user", connectionParam.getUsername());
                    }
                    if (connectionParam.getPassword() != null) {
                        actualParams.put("password", connectionParam.getPassword());
                    }

                    // 移除标准的host/port参数，避免冲突
                    actualParams.remove("host");
                    actualParams.remove("port");
                } else {
                    // 对于其他数据源类型，构建JDBC URL
                    String jdbcUrl = buildJdbcUrl(dataSourceType.toUpperCase(), connectionParam);
                    if (jdbcUrl != null) {
                        actualParams.put("url", jdbcUrl);
                    }
                }
            }

            logger.debug("Retrieved actual connection parameters for datasource {}: {}", datasourceId, actualParams);
            return actualParams;

        } catch (Exception e) {
            logger.error("Failed to fetch connection parameters for datasource ID: {}", datasourceId, e);
            throw new CustomException("获取数据源连接参数失败", e);
        }
    }

    /**
     * 根据数据源类型构建JDBC URL
     *
     * @param dataSourceType  数据源类型
     * @param connectionParam 连接参数
     * @return JDBC URL字符串
     */
    private String buildJdbcUrl(String dataSourceType, ConnectionParam connectionParam) {
        try {
            String host = connectionParam.getHost();
            Integer port = connectionParam.getPort();
            String database = connectionParam.getDatabase();
            String params = connectionParam.getParams();

            if (host == null || port == null || database == null) {
                return null;
            }

            switch (dataSourceType) {
                case "MYSQL":
                    String mysqlParams = params != null ? params : "useUnicode=true&characterEncoding=UTF-8&useSSL=false";
                    return String.format("jdbc:mysql://%s:%d/%s?%s", host, port, database, mysqlParams);
                case "ORACLE":
                    String oracleParams = params != null ? params : "";
                    return String.format("jdbc:oracle:thin:@%s:%d:%s%s", host, port, database,
                            oracleParams.isEmpty() ? "" : "?" + oracleParams);
                case "SQLSERVER":
                    String sqlserverParams = params != null ? params : "encrypt=false";
                    return String.format("jdbc:sqlserver://%s:%d;database=%s;%s", host, port, database, sqlserverParams);
                case "CLICKHOUSE":
                    String clickhouseParams = params != null ? params : "";
                    String protocol = connectionParam.getValue("protocol");
                    if (protocol == null) protocol = "http";
                    return String.format("jdbc:%s://%s:%d/%s%s", protocol, host, port, database,
                            clickhouseParams.isEmpty() ? "" : "?" + clickhouseParams);
                case "DORIS":
                    String dorisParams = params != null ? params : "";
                    return String.format("jdbc:mysql://%s:%d/%s?%s", host, port, database, dorisParams);
                case "STARROCKS":
                    String starrocksParams = params != null ? params : "";
                    return String.format("jdbc:mysql://%s:%d/%s?%s", host, port, database, starrocksParams);
                default:
                    // 对于不明确支持的类型，尝试构建基本的JDBC URL
                    return String.format("jdbc:%s://%s:%d/%s", dataSourceType.toLowerCase(), host, port, database);
            }
        } catch (Exception e) {
            logger.warn("Failed to build JDBC URL for data source type {}: {}", dataSourceType, e.getMessage());
            return null;
        }
    }

    private String createJobConfig(Long jobId) throws JsonProcessingException {
        return null;
    }
}
