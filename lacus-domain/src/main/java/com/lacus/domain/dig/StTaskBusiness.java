package com.lacus.domain.dig;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.dig.entity.StJobEntity;
import com.lacus.dao.dig.entity.StTaskEntity;
import com.lacus.dao.dig.entity.StTaskRelationEntity;
import com.lacus.domain.dig.dto.JobDag;
import com.lacus.domain.dig.dto.JobTaskInfo;
import com.lacus.domain.dig.dto.Node;
import com.lacus.domain.dig.dto.Relation;
import com.lacus.domain.dig.dto.StTaskConfig;
import com.lacus.service.dig.IStJobService;
import com.lacus.service.dig.IStTaskRelationService;
import com.lacus.service.dig.IStTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StTaskBusiness {

    @Autowired
    private IStJobService stJobService;

    @Autowired
    private IStTaskService stTaskService;

    @Autowired
    private IStTaskRelationService stTaskRelationService;

    public StTaskEntity saveOrUpdateTask(StTaskConfig command) {
        try {
            StTaskEntity stTask = new StTaskEntity();
            stTask.setTaskId(command.getTaskId());
            stTask.setTaskName(command.getTaskName());
            stTask.setJobId(command.getJobId());
            stTask.setConnectorType(command.getConnectorType());
            stTask.setConnectorName(command.getConnectorName());
            stTask.setConnectionConfig(command.getConnectorConfig());

            if (ObjectUtils.isNotEmpty(command.getTaskId())) {
                StTaskEntity stTaskEntity = stTaskService.getById(command.getTaskId());
                if (ObjectUtils.isEmpty(stTaskEntity)) {
                    stTaskService.save(stTask);
                } else {
                    stTaskService.updateById(stTask);
                }
            }
            return stTask;
        } catch (Exception e) {
            throw new CustomException("保存任务节点出错", e);
        }
    }

    public StTaskConfig getTaskById(String taskId) {
        try {
            StTaskEntity task = stTaskService.getById(taskId);
            if (ObjectUtils.isEmpty(task)) {
                log.error("任务节点 {} 不存在", taskId);
                return null;
            }
            return convertStTaskConfig(task);
        } catch (Exception e) {
            throw new CustomException("获取任务节点出错", e);
        }
    }

    private static StTaskConfig convertStTaskConfig(StTaskEntity task) {
        try {
            StTaskConfig config = new StTaskConfig();
            config.setTaskId(task.getTaskId());
            config.setTaskName(task.getTaskName());
            config.setJobId(task.getJobId());
            config.setConnectorType(task.getConnectorType());
            config.setConnectorName(task.getConnectorName());
            config.setConnectorConfig(task.getConnectionConfig());
            config.setPosition(JSON.parseObject(task.getPosition()));
            return config;
        } catch (Exception e) {
            throw new CustomException("任务节点转换出错", e);
        }
    }

    /**
     * 删除指定ID的任务。
     *
     * @param taskId 任务的唯一标识符
     * @throws CustomException 如果任务不存在或删除过程中发生错误，则抛出此异常
     */
    public void deleteTask(String taskId) {
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

    /**
     * Saves the given JobDag to the system, updating both the job and its associated task relations.
     * This method ensures that the task relations are up-to-date with the provided DAG, removing any
     * outdated relations and adding new ones as necessary. It also updates the position of tasks within
     * the job based on the provided plugins in the DAG.
     *
     * @param dag the JobDag object containing the job and its associated task plugins and relations to be saved
     */
    public void saveDag(JobDag dag) {
        try {
            Long jobId = dag.getJobId();
            updateJob(dag, jobId);

            List<Node> plugins = dag.getPlugins();
            List<StTaskRelationEntity> relations = dag.getRelations().stream().map(item -> {
                StTaskRelationEntity relation = new StTaskRelationEntity();
                relation.setJobId(jobId);
                relation.setSourceTaskId(item.getSourceTaskId());
                relation.setSinkTaskId(item.getSinkTaskId());
                return relation;
            }).collect(Collectors.toList());

            stTaskRelationService.removeByJobId(jobId);
            if (ObjectUtils.isNotEmpty(relations)) {
                stTaskRelationService.saveBatch(relations);
            }
            List<StTaskEntity> allTaskListByJobId = stTaskService.getTaskListByJobId(jobId);
            List<String> allTaskId = allTaskListByJobId.stream()
                    .map(StTaskEntity::getTaskId)
                    .collect(Collectors.toList());
            if (ObjectUtils.isNotEmpty(plugins)) {
                List<StTaskEntity> taskEntityList = new ArrayList<>();
                List<String> newTaskIds = new ArrayList<>();
                for (Node plugin : plugins) {
                    String taskId = plugin.getTaskId();
                    JSONObject position = plugin.getPosition();
                    StTaskEntity stTaskEntity = new StTaskEntity();
                    stTaskEntity.setTaskId(taskId);
                    stTaskEntity.setPosition(JSONObject.toJSONString(position));
                    taskEntityList.add(stTaskEntity);
                    newTaskIds.add(taskId);
                }
                Set<String> newTaskIdSet = new HashSet<>(newTaskIds);
                List<String> notExistTaskIds = allTaskId.stream()
                        .filter(id -> !newTaskIdSet.contains(id))
                        .collect(Collectors.toList());
                stTaskService.removeBatchByIds(notExistTaskIds);
                stTaskService.updateBatchById(taskEntityList);
            }
        } catch (Exception e) {
            throw new CustomException("保存任务DAG出错", e);
        }
    }

    /**
     * Updates the job information with the provided JobDag and job ID.
     *
     * @param dag   the JobDag containing the updated job information
     * @param jobId the unique identifier of the job to be updated
     * @throws CustomException if the job does not exist or an error occurs during the update process
     */
    private void updateJob(JobDag dag, Long jobId) {
        String engineName = dag.getEngineName();
        String engineVersion = dag.getEngineVersion();
        String engineParam = dag.getEngineParam();
        StJobEntity job = stJobService.getById(jobId);
        if (ObjectUtils.isEmpty(job)) {
            throw new CustomException("任务不存在：" + jobId);
        }
        job.setEngineName(engineName);
        if (ObjectUtils.isNotEmpty(engineVersion)) {
            job.setEngineVersion(engineVersion);
        } else {
            job.setEngineVersion(null);
        }
        if (ObjectUtils.isNotEmpty(engineParam)) {
            job.setEngineParam(engineParam);
        } else {
            job.setEngineParam(null);
        }
        stJobService.updateById(job);
    }

    public JobTaskInfo getDag(Long jobId) {
        StJobEntity job = stJobService.getById(jobId);
        String engineName = job.getEngineName();
        String engineVersion = job.getEngineVersion();
        String engineParam = job.getEngineParam();
        List<StTaskEntity> tasks = stTaskService.getTaskListByJobId(jobId);
        List<StTaskConfig> taskList = tasks.stream().map(StTaskBusiness::convertStTaskConfig).collect(Collectors.toList());
        List<StTaskRelationEntity> relations = stTaskRelationService.getTaskRelationsByJobId(jobId);
        List<Relation> relationList = relations.stream().map(item -> new Relation(item.getSourceTaskId(), item.getSinkTaskId())).collect(Collectors.toList());
        return new JobTaskInfo(engineName, engineVersion, engineParam, relationList, taskList);
    }
}
