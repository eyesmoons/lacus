package com.lacus.domain.lakeintelligence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.lakeintelligence.entity.LakeDatasetEntity;
import com.lacus.dao.lakeintelligence.entity.LakeModelInfoEntity;
import com.lacus.dao.lakeintelligence.entity.LakeTaskEntity;
import com.lacus.domain.lakeintelligence.command.TrainRequest;
import com.lacus.domain.lakeintelligence.dto.ProgressResponse;
import com.lacus.domain.lakeintelligence.dto.TaskDTO;
import com.lacus.domain.lakeintelligence.feign.MlServiceFeign;
import com.lacus.domain.lakeintelligence.query.TaskPageQuery;
import com.lacus.enums.TaskStatus;
import com.lacus.enums.TaskType;
import com.lacus.service.lakeintelligence.ILakeDatasetService;
import com.lacus.service.lakeintelligence.ILakeModelInfoService;
import com.lacus.service.lakeintelligence.ILakeTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 训练任务业务逻辑
 */
@Slf4j
@Service
public class TrainBusiness {

    @Autowired
    private ILakeTaskService lakeTaskService;

    @Autowired
    private ILakeDatasetService lakeDatasetService;

    @Autowired
    private ILakeModelInfoService lakeModelInfoService;

    @Autowired
    private MlServiceFeign mlServiceFeign;

    /**
     * 训练任务轮询间隔（毫秒），默认 2000ms
     */
    @Value("${training.poll-interval-ms:2000}")
    private long pollIntervalMs;

    /**
     * 分页查询训练任务列表
     */
    public PageDTO pageList(TaskPageQuery query) {
        return new PageDTO(lakeTaskService.page(query.toPage(), query.toQueryWrapper()));
    }

    /**
     * 启动训练任务
     */
    public TaskDTO startTraining(TrainRequest request) {
        if (lakeTaskService.isTaskNameDuplicated(null, request.getTaskName())) {
            throw new CustomException("任务名称[" + request.getTaskName() + "]已存在");
        }
        // 构建 ML 服务请求
        Map<String, Object> mlRequest = new HashMap<>();
        mlRequest.put("trainer_type", request.getTrainerType());
        mlRequest.put("dataset_uri", resolveDatasetUri(request.getDatasetId()));
        mlRequest.put("epochs", request.getEpochs());
        mlRequest.put("batch_size", request.getBatchSize());
        mlRequest.put("learning_rate", request.getLearningRate());
        mlRequest.put("device", request.getDevice());

        Map<String, Object> response;
        try {
            response = mlServiceFeign.startTrain(mlRequest);
        } catch (Exception e) {
            throw new CustomException("启动训练失败：" + e.getMessage());
        }

        if (response == null || (response.get("code") != null && Integer.valueOf(-1).equals(response.get("code")))) {
            throw new CustomException("启动训练失败：" + (response != null ? response.get("message") : "无响应"));
        }

        // 获取 Python 端返回的 task_id
        String mlTaskId = (String) response.get("task_id");

        // 创建本地任务记录
        LakeTaskEntity entity = new LakeTaskEntity();
        entity.setTaskName(request.getTaskName());
        entity.setTaskType(TaskType.SIMILARITY.getCode());
        entity.setModelId(request.getModelId());
        entity.setDatasetId(request.getDatasetId());
        entity.setStatus(TaskStatus.TRAINING.getCode());
        entity.setTrainingProgress(0);
        entity.setMlTaskId(mlTaskId);  // 保存 Python 端的 task_id
        entity.setCreatorId(request.getCreatorId());
        entity.setStartedAt(new Date());
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        entity.setDeleted(0);
        // 保存超参数
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> hyperParams = new HashMap<>();
            hyperParams.put("trainer_type", request.getTrainerType());
            hyperParams.put("epochs", request.getEpochs());
            hyperParams.put("batch_size", request.getBatchSize());
            hyperParams.put("learning_rate", request.getLearningRate());
            hyperParams.put("device", request.getDevice());
            entity.setHyperParams(mapper.writeValueAsString(hyperParams));
        } catch (Exception e) {
            log.warn("序列化超参数失败", e);
        }
        lakeTaskService.save(entity);

        // 更新模型状态为 TRAINING
        if (request.getModelId() != null) {
            LakeModelInfoEntity model = lakeModelInfoService.getById(request.getModelId());
            if (model != null) {
                model.setStatus("TRAINING");
                model.setTaskId(entity.getTaskId());
                model.setUpdateTime(new Date());
                lakeModelInfoService.updateById(model);
            }
        }

        return toDTO(entity);
    }

    /**
     * 查询训练进度
     */
    public ProgressResponse getProgress(Long taskId) {
        LakeTaskEntity entity = lakeTaskService.getById(taskId);
        if (ObjectUtils.isEmpty(entity)) {
            throw new CustomException("任务[" + taskId + "]不存在");
        }
        ProgressResponse response = new ProgressResponse();
        response.setTaskId(String.valueOf(taskId));
        response.setStatus(entity.getStatus());
        response.setProgress(entity.getTrainingProgress());
        response.setMessage(entity.getErrorMessage());

        // 训练完成时，返回关联模型的最终结果
        if ("COMPLETED".equals(entity.getStatus()) && entity.getModelId() != null) {
            LakeModelInfoEntity model = lakeModelInfoService.getById(entity.getModelId());
            if (model != null) {
                response.setModelId(model.getModelId());
                response.setModelName(model.getModelName());
                response.setModelPath(model.getModelPath());
                response.setFinalLoss(model.getFinalLoss());
                response.setTrainingEpochs(model.getTrainingEpochs());
                response.setModelSizeBytes(model.getModelSizeBytes());
                response.setTrainLoss(model.getFinalLoss() != null ? model.getFinalLoss().doubleValue() : null);
            }
        }

        return response;
    }

    /**
     * 取消训练任务
     */
    public void cancelTraining(Long taskId) {
        LakeTaskEntity entity = lakeTaskService.getById(taskId);
        if (ObjectUtils.isEmpty(entity)) {
            throw new CustomException("任务[" + taskId + "]不存在");
        }
        if (TaskStatus.COMPLETED.getCode().equals(entity.getStatus())) {
            throw new CustomException("任务已完成，无法取消");
        }
        if (TaskStatus.CANCELLED.getCode().equals(entity.getStatus())) {
            throw new CustomException("任务已取消");
        }
        try {
            mlServiceFeign.cancelTrain(String.valueOf(taskId));
        } catch (Exception e) {
            log.warn("调用 ML 服务取消训练失败：{}", e.getMessage());
        }
        entity.setStatus(TaskStatus.CANCELLED.getCode());
        entity.setErrorMessage("用户取消");
        entity.setUpdateTime(new Date());
        lakeTaskService.updateById(entity);
    }

    /**
     * 获取任务详情
     */
    public TaskDTO detail(Long taskId) {
        LakeTaskEntity entity = lakeTaskService.getById(taskId);
        if (ObjectUtils.isEmpty(entity)) {
            throw new CustomException("任务[" + taskId + "]不存在");
        }
        return toDTO(entity);
    }

    /**
     * 定时轮询训练中的任务进度
     */
    @Scheduled(fixedDelayString = "${training.poll-interval-ms:2000}")
    public void pollTrainingProgress() {
        LambdaQueryWrapper<LakeTaskEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LakeTaskEntity::getStatus, TaskStatus.TRAINING.getCode());
        wrapper.eq(LakeTaskEntity::getDeleted, 0);
        List<LakeTaskEntity> trainingTasks = lakeTaskService.list(wrapper);
        for (LakeTaskEntity task : trainingTasks) {
            try {
                String mlTaskId = task.getMlTaskId();
                if (mlTaskId == null || mlTaskId.isEmpty()) {
                    continue;
                }
                Map<String, Object> response = mlServiceFeign.getTrainProgress(mlTaskId);
                if (response == null) {
                    continue;
                }
                String status = (String) response.get("status");
                if ("completed".equalsIgnoreCase(status)) {
                    task.setStatus(TaskStatus.COMPLETED.getCode());
                    task.setTrainingProgress(100);
                    task.setCompletedAt(new Date());
                    // 创建模型记录
                    createModelFromTask(task, response);
                    updateModelStatus(task.getTaskId(), "TRAINING_COMPLETED");
                } else if ("failed".equalsIgnoreCase(status)) {
                    task.setStatus(TaskStatus.FAILED.getCode());
                    task.setErrorMessage((String) response.get("message"));
                    updateModelStatus(task.getTaskId(), "TRAINING_FAILED");
                } else if ("cancelled".equalsIgnoreCase(status)) {
                    task.setStatus(TaskStatus.CANCELLED.getCode());
                } else {
                    // 训练中，更新进度
                    updateModelStatus(task.getTaskId(), "TRAINING");
                    Object epoch = response.get("epoch");
                    Object totalEpochs = response.get("total_epochs");
                    if (epoch instanceof Number && totalEpochs instanceof Number && ((Number) totalEpochs).intValue() > 0) {
                        int progress = (int) (((Number) epoch).doubleValue() / ((Number) totalEpochs).doubleValue() * 100);
                        task.setTrainingProgress(Math.min(progress, 100));
                    }
                }
                task.setUpdateTime(new Date());
                lakeTaskService.updateById(task);
            } catch (Exception e) {
                log.warn("轮询任务[{}]进度失败：{}", task.getTaskId(), e.getMessage());
            }
        }
    }

    /**
     * 同步更新关联模型的状态
     */
    private void updateModelStatus(Long taskId, String modelStatus) {
        LambdaQueryWrapper<LakeModelInfoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LakeModelInfoEntity::getTaskId, taskId);
        LakeModelInfoEntity model = lakeModelInfoService.getOne(wrapper);
        if (model != null) {
            model.setStatus(modelStatus);
            model.setUpdateTime(new Date());
            lakeModelInfoService.updateById(model);
        }
    }

    /**
     * 训练完成后创建模型记录
     */
    private void createModelFromTask(LakeTaskEntity task, Map<String, Object> response) {
        // 如果任务已关联模型，更新现有模型
        if (task.getModelId() != null) {
            LakeModelInfoEntity model = lakeModelInfoService.getById(task.getModelId());
            if (model != null) {
                updateModelFromTask(model, task, response);
            }
            return;
        }

        // 检查是否已存在模型
        LambdaQueryWrapper<LakeModelInfoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LakeModelInfoEntity::getTaskId, task.getTaskId());
        LakeModelInfoEntity existingModel = lakeModelInfoService.getOne(wrapper);
        if (existingModel != null) {
            return;
        }

        // 获取数据集信息
        LakeDatasetEntity dataset = lakeDatasetService.getById(task.getDatasetId());
        if (dataset == null) {
            return;
        }

        // 获取模型路径（从 Python 服务响应中）
        String modelPath = (String) response.get("model_path");
        if (modelPath == null || modelPath.isEmpty()) {
            // 使用默认路径
            modelPath = "/data/lake-intelligence/models/" + task.getTaskId() + "/model.pt";
        }
        log.info("[train] 模型路径: taskId={}, modelPath={}", task.getTaskId(), modelPath);

        // 获取模型架构
        String modelArch = (String) response.get("model_arch");
        if (modelArch == null || modelArch.isEmpty()) {
            modelArch = "SIMILARITY".equals(task.getTaskType()) ? "similarity_autoencoder" : "cnn_classifier";
        }

        // 创建模型实体
        LakeModelInfoEntity model = new LakeModelInfoEntity();
        model.setModelName(task.getTaskName() + "_model");
        model.setTaskId(task.getTaskId());
        model.setDatasetId(task.getDatasetId());
        model.setModelArch(modelArch);
        model.setModelPath(modelPath);
        model.setStatus("TRAINING_COMPLETED");

        // 获取最终损失
        Object finalLoss = response.get("final_loss");
        if (finalLoss instanceof Number) {
            model.setFinalLoss(java.math.BigDecimal.valueOf(((Number) finalLoss).doubleValue()));
        }

        // 获取训练轮数
        Object epochs = response.get("total_epochs");
        if (epochs instanceof Number) {
            model.setTrainingEpochs(((Number) epochs).intValue());
        }

        // 获取模型文件大小
        try {
            java.io.File modelFile = new java.io.File(modelPath);
            if (modelFile.exists()) {
                model.setModelSizeBytes(modelFile.length());
            }
        } catch (Exception e) {
            // 忽略文件大小获取失败
        }

        model.setCreatorId(task.getCreatorId());
        model.setCreateTime(new Date());
        model.setUpdateTime(new Date());
        model.setDeleted(0);

        lakeModelInfoService.save(model);
        log.info("模型创建成功：taskId={}, modelId={}", task.getTaskId(), model.getModelId());
    }

    /**
     * 训练完成后更新已关联的模型记录
     */
    private void updateModelFromTask(LakeModelInfoEntity model, LakeTaskEntity task, Map<String, Object> response) {
        String modelPath = (String) response.get("model_path");
        if (modelPath != null && !modelPath.isEmpty()) {
            model.setModelPath(modelPath);
        }
        log.info("[train] 更新模型路径: taskId={}, modelId={}, modelPath={}", task.getTaskId(), model.getModelId(), model.getModelPath());

        String modelArch = (String) response.get("model_arch");
        if (modelArch != null && !modelArch.isEmpty()) {
            model.setModelArch(modelArch);
        }

        model.setStatus("TRAINING_COMPLETED");

        Object finalLoss = response.get("final_loss");
        if (finalLoss instanceof Number) {
            model.setFinalLoss(java.math.BigDecimal.valueOf(((Number) finalLoss).doubleValue()));
        }

        Object epochs = response.get("total_epochs");
        if (epochs instanceof Number) {
            model.setTrainingEpochs(((Number) epochs).intValue());
        }

        try {
            java.io.File modelFile = new java.io.File(model.getModelPath());
            if (modelFile.exists()) {
                model.setModelSizeBytes(modelFile.length());
            }
        } catch (Exception e) {
            // 忽略文件大小获取失败
        }

        model.setUpdateTime(new Date());
        lakeModelInfoService.updateById(model);
        log.info("模型更新成功：taskId={}, modelId={}", task.getTaskId(), model.getModelId());
    }

    private String resolveDatasetUri(Long datasetId) {
        if (datasetId == null) {
            throw new CustomException("数据集ID不能为空");
        }
        LakeDatasetEntity dataset = lakeDatasetService.getById(datasetId);
        if (dataset == null) {
            throw new CustomException("数据集[" + datasetId + "]不存在");
        }
        String localPath = dataset.getLocalPath();
        // 如果 local_path 为空，尝试从 source_config JSON 中获取
        if (localPath == null || localPath.isEmpty()) {
            String sourceConfig = dataset.getSourceConfig();
            if (sourceConfig != null && !sourceConfig.isEmpty()) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    java.util.Map<?, ?> config = mapper.readValue(sourceConfig, java.util.Map.class);
                    localPath = (String) config.get("localPath");
                } catch (Exception e) {
                    // 解析失败，继续抛出原始错误
                }
            }
        }
        if (localPath == null || localPath.isEmpty()) {
            throw new CustomException("数据集[" + datasetId + "]本地路径不存在，请先上传数据集文件或解析数据集");
        }
        return localPath;
    }

    private TaskDTO toDTO(LakeTaskEntity entity) {
        TaskDTO dto = new TaskDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public Map<String, Object> getHyperparamSchema(String taskType) {
        Map<String, Object> schema = new HashMap<>();
        if ("CLASSIFICATION".equals(taskType)) {
            schema.put("taskType", "CLASSIFICATION");
            List<Map<String, Object>> fields = new ArrayList<>();
            fields.add(new HashMap<String, Object>() {{
                put("name", "epochs");
                put("type", "number");
                put("label", "训练轮数");
                put("default", 20);
                put("min", 1);
                put("max", 200);
                put("required", true);
            }});
            fields.add(new HashMap<String, Object>() {{
                put("name", "learning_rate");
                put("type", "number");
                put("label", "学习率");
                put("default", 0.001);
                put("min", 0.0001);
                put("max", 0.01);
                put("required", true);
            }});
            Map<String, Object> batchField = new HashMap<>();
            batchField.put("name", "batch_size");
            batchField.put("type", "select");
            batchField.put("label", "批次大小");
            batchField.put("default", 32);
            batchField.put("options", Arrays.asList(16, 32, 64));
            batchField.put("required", true);
            fields.add(batchField);
            fields.add(new HashMap<String, Object>() {{
                put("name", "n_classes");
                put("type", "number");
                put("label", "分类数");
                put("default", 5);
                put("min", 2);
                put("max", 100);
                put("required", true);
            }});
            schema.put("fields", fields);
        } else {
            schema.put("taskType", "SIMILARITY");
            List<Map<String, Object>> fields = new ArrayList<>();
            fields.add(new HashMap<String, Object>() {{
                put("name", "epochs");
                put("type", "number");
                put("label", "训练轮数");
                put("default", 30);
                put("min", 1);
                put("max", 200);
                put("required", true);
            }});
            fields.add(new HashMap<String, Object>() {{
                put("name", "learning_rate");
                put("type", "number");
                put("label", "学习率");
                put("default", 0.001);
                put("min", 0.0001);
                put("max", 0.01);
                put("required", true);
            }});
            Map<String, Object> batchField = new HashMap<>();
            batchField.put("name", "batch_size");
            batchField.put("type", "select");
            batchField.put("label", "批次大小");
            batchField.put("default", 32);
            batchField.put("options", Arrays.asList(16, 32, 64));
            batchField.put("required", true);
            fields.add(batchField);
            fields.add(new HashMap<String, Object>() {{
                put("name", "embedding_dim");
                put("type", "number");
                put("label", "嵌入维度");
                put("default", 512);
                put("min", 64);
                put("max", 2048);
                put("required", true);
            }});
            schema.put("fields", fields);
        }
        return schema;
    }
}
