package com.lacus.domain.dig;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.dig.entity.StTaskEntity;
import com.lacus.dao.dig.entity.StTaskRelationEntity;
import com.lacus.domain.dig.dto.DatabaseTableSchema;
import com.lacus.domain.dig.dto.DatasourceConfig;
import com.lacus.domain.dig.dto.JobDag;
import com.lacus.domain.dig.dto.JobTaskInfo;
import com.lacus.domain.dig.dto.Relation;
import com.lacus.domain.dig.dto.SourceFieldsConfig;
import com.lacus.domain.dig.dto.StTaskConfig;
import com.lacus.service.dig.IStTaskRelationService;
import com.lacus.service.dig.IStTaskService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.seatunnel.common.constants.PluginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StTaskBusiness {

    @Autowired
    private IStTaskService stTaskService;

    @Autowired
    private IStTaskRelationService stTaskRelationService;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public void saveOrUpdateTask(StTaskConfig command) {
        try {
            StTaskEntity stTask = new StTaskEntity();
            String transformConfig = "";
            if (Objects.equals(command.getConnectorType(), PluginType.TRANSFORM)) {
                transformConfig = OBJECT_MAPPER.writeValueAsString(command.getTransformConfig());
            }
            stTask.setTaskName(command.getTaskName());
            stTask.setJobId(command.getJobId());
            stTask.setConnectorType(command.getConnectorType());
            stTask.setConnectorName(command.getConnectorName());
            stTask.setDatasourceId(command.getDatasourceId());
            stTask.setTaskConfig(command.getTaskConfig());
            stTask.setDatasourceConfig(ObjectUtils.isEmpty(command.getDatasourceConfig()) ? null : OBJECT_MAPPER.writeValueAsString(command.getDatasourceConfig()));
            stTask.setDatasourceConfig(transformConfig);
            stTask.setDatasourceConfig(ObjectUtils.isEmpty(command.getSinkFieldsConfig()) ? null : OBJECT_MAPPER.writeValueAsString(command.getSinkFieldsConfig()));

            if (ObjectUtils.isNotEmpty(command.getTaskId())) {
                StTaskEntity stTaskEntity = stTaskService.getById(command.getTaskId());
                if (ObjectUtils.isEmpty(stTaskEntity)) {
                    throw new CustomException("任务节点 " + command.getTaskId() + " 不存在");
                }
                stTaskService.updateById(stTask);
            } else {
                stTaskService.save(stTask);
            }
        } catch (Exception e) {
            throw new CustomException("保存任务节点出错", e);
        }
    }

    public StTaskConfig getTaskById(Long taskId) {
        try {
            StTaskEntity task = stTaskService.getById(taskId);
            if (ObjectUtils.isEmpty(task)) {
                throw new CustomException("任务节点 " + taskId + " 不存在");
            }
            return convertStTaskConfig(task);
        } catch (Exception e) {
            throw new CustomException("获取任务节点出错", e);
        }
    }

    private static StTaskConfig convertStTaskConfig(StTaskEntity task) {
        try {
            StTaskConfig config = new StTaskConfig();
            config.setTaskName(task.getTaskName());
            config.setJobId(task.getJobId());
            config.setConnectorType(task.getConnectorType());
            config.setConnectorName(task.getConnectorName());
            config.setDatasourceId(task.getDatasourceId());
            config.setTaskConfig(task.getTaskConfig());
            config.setDatasourceConfig(ObjectUtils.isEmpty(task.getDatasourceConfig()) ? null : OBJECT_MAPPER.readValue(task.getDatasourceConfig(), DatasourceConfig.class));
            config.setSourceFieldsConfig(ObjectUtils.isEmpty(task.getSourceFieldsConfig()) ? null : OBJECT_MAPPER.readValue(task.getSourceFieldsConfig(), SourceFieldsConfig.class));
            config.setTransformConfig(ObjectUtils.isEmpty(task.getTransformConfig()) ? null : OBJECT_MAPPER.readValue(task.getTransformConfig(), new TypeReference<Map<String, Object>>() {
            }));
            config.setSinkFieldsConfig(ObjectUtils.isEmpty(task.getSinkFieldsConfig()) ? null : OBJECT_MAPPER.readValue(task.getSinkFieldsConfig(), new TypeReference<List<DatabaseTableSchema>>() {
            }));
            return config;
        } catch (Exception e) {
            throw new CustomException("任务节点转换出错", e);
        }
    }

    public void deleteTask(Long taskId) {
        try {
            StTaskEntity task = stTaskService.getById(taskId);
            if (ObjectUtils.isEmpty(task)) {
                throw new CustomException("任务节点 " + taskId + " 不存在");
            }
            stTaskService.removeById(taskId);
        } catch (Exception e) {
            throw new CustomException("删除任务节点出错", e);
        }
    }

    public void saveDag(JobDag dag) {
        try {
            List<StTaskRelationEntity> relations = dag.getRelations().stream().map(item -> {
                StTaskRelationEntity relation = new StTaskRelationEntity();
                relation.setSourceTaskId(item.getSourceTaskId());
                relation.setSinkTaskId(item.getSinkTaskId());
                return relation;
            }).collect(Collectors.toList());

            if (ObjectUtils.isNotEmpty(relations)) {
                stTaskRelationService.saveBatch(relations);
            }
        } catch (Exception e) {
            throw new CustomException("保存任务DAG出错", e);
        }
    }

    public JobTaskInfo getDag(Long jobId) {
        List<StTaskEntity> tasks = stTaskService.getTaskListByJobId(jobId);
        List<StTaskConfig> taskList = tasks.stream().map(StTaskBusiness::convertStTaskConfig).collect(Collectors.toList());
        List<StTaskRelationEntity> relations = stTaskRelationService.getTaskRelationsByJobId(jobId);
        List<Relation> relationList = relations.stream().map(item -> new Relation(item.getSourceTaskId(), item.getSinkTaskId())).collect(Collectors.toList());
        return new JobTaskInfo(relationList, taskList);
    }
}
