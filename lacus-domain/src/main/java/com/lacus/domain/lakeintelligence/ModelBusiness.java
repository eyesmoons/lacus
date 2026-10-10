package com.lacus.domain.lakeintelligence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lacus.common.config.FileStorageConfig;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.lakeintelligence.entity.LakeDatasetEntity;
import com.lacus.dao.lakeintelligence.entity.LakeModelInfoEntity;
import com.lacus.dao.lakeintelligence.entity.LakeTaskEntity;
import com.lacus.domain.lakeintelligence.command.CreateModelRequest;
import com.lacus.domain.lakeintelligence.command.TrainRequest;
import com.lacus.domain.lakeintelligence.dto.ModelInfoDTO;
import com.lacus.domain.lakeintelligence.dto.TaskDTO;
import com.lacus.domain.lakeintelligence.query.ModelPageQuery;
import com.lacus.core.security.AuthenticationUtils;
import com.lacus.service.lakeintelligence.ILakeDatasetService;
import com.lacus.service.lakeintelligence.ILakeModelInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模型管理业务逻辑
 */
@Slf4j
@Service
public class ModelBusiness {

    @Autowired
    private ILakeModelInfoService lakeModelInfoService;

    @Autowired
    private ILakeDatasetService lakeDatasetService;

    @Autowired
    private FileStorageConfig fileStorageConfig;

    @Autowired
    private TrainBusiness trainBusiness;

    @Autowired
    private com.lacus.service.lakeintelligence.ILakeTaskService lakeTaskService;

    /**
     * 创建模型（仅基本信息）
     */
    public ModelInfoDTO createModel(CreateModelRequest request) {
        LakeModelInfoEntity entity = new LakeModelInfoEntity();
        entity.setModelName(request.getModelName());
        entity.setDescription(request.getDescription());
        entity.setDatasetId(request.getDatasetId());
        entity.setStatus("PENDING");
        entity.setModelPath("");
        entity.setLabelFilePath(request.getLabelFilePath());
        entity.setCreatorId(String.valueOf(AuthenticationUtils.getUserId()));
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        entity.setDeleted(0);
        lakeModelInfoService.save(entity);
        return toDTO(entity);
    }

    /**
     * 启动模型训练
     */
    public TaskDTO trainModel(Long modelId, TrainRequest request) {
        LakeModelInfoEntity entity = lakeModelInfoService.getById(modelId);
        if (ObjectUtils.isEmpty(entity)) {
            throw new CustomException("模型[" + modelId + "]不存在");
        }
        request.setModelId(modelId);
        request.setCreatorId(String.valueOf(AuthenticationUtils.getUserId()));
        return trainBusiness.startTraining(request);
    }

    /**
     * 更新模型基本信息
     */
    public ModelInfoDTO updateModel(Long modelId, String modelName, String description) {
        LakeModelInfoEntity entity = lakeModelInfoService.getById(modelId);
        if (ObjectUtils.isEmpty(entity)) {
            throw new CustomException("模型[" + modelId + "]不存在");
        }
        entity.setModelName(modelName);
        entity.setDescription(description);
        entity.setUpdaterId("current-user");
        lakeModelInfoService.updateById(entity);
        return toDTO(entity);
    }

    /**
     * 分页查询模型列表
     */
    public PageDTO pageList(ModelPageQuery query) {
        com.baomidou.mybatisplus.core.metadata.IPage<LakeModelInfoEntity> page =
                lakeModelInfoService.page(query.toPage(), query.toQueryWrapper());
        List<ModelInfoDTO> rows = page.getRecords().stream().map(this::toDTO)
                .collect(java.util.stream.Collectors.toList());
        return new PageDTO(rows, page.getTotal());
    }

    /**
     * 获取模型详情
     */
    public ModelInfoDTO detail(Long modelId) {
        LakeModelInfoEntity entity = lakeModelInfoService.getById(modelId);
        if (ObjectUtils.isEmpty(entity)) {
            throw new CustomException("模型[" + modelId + "]不存在");
        }
        return toDTO(entity);
    }

    /**
     * 下载模型文件
     *
     * @return 模型文件
     */
    public File downloadModel(Long modelId) {
        LakeModelInfoEntity entity = lakeModelInfoService.getById(modelId);
        if (ObjectUtils.isEmpty(entity)) {
            throw new CustomException("模型[" + modelId + "]不存在");
        }
        if (!"TRAINING_COMPLETED".equals(entity.getStatus())) {
            throw new CustomException("模型尚未训练完成，无法下载");
        }
        if (entity.getModelPath() == null || entity.getModelPath().isEmpty()) {
            throw new CustomException("模型文件路径不存在");
        }
        File file = new File(entity.getModelPath());
        if (!file.isFile()) {
            throw new CustomException("模型文件不存在：" + entity.getModelPath());
        }
        // 路径穿越防护：验证文件规范路径位于模型存储目录内
        String modelsRoot = fileStorageConfig.getModelDir();
        try {
            String expectedPrefix = new File(modelsRoot).getCanonicalPath();
            String actualPath = file.getCanonicalPath();
            if (!actualPath.startsWith(expectedPrefix + File.separator) && !actualPath.equals(expectedPrefix)) {
                throw new CustomException("模型文件路径非法，拒绝访问：" + entity.getModelPath());
            }
        } catch (IOException e) {
            throw new CustomException("模型文件路径校验失败：" + e.getMessage());
        }
        // 更新文件大小为实际值
        long actualSize = file.length();
        if (entity.getModelSizeBytes() == null || entity.getModelSizeBytes() != actualSize) {
            entity.setModelSizeBytes(actualSize);
            entity.setUpdateTime(new Date());
            lakeModelInfoService.updateById(entity);
        }
        return file;
    }

    /**
     * 删除模型
     */
    public void deleteModel(Long modelId) {
        LakeModelInfoEntity entity = lakeModelInfoService.getById(modelId);
        if (ObjectUtils.isEmpty(entity)) {
            throw new CustomException("模型[" + modelId + "]不存在");
        }
        // 删除关联的训练任务
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.lacus.dao.lakeintelligence.entity.LakeTaskEntity> taskWrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        taskWrapper.eq(com.lacus.dao.lakeintelligence.entity.LakeTaskEntity::getModelId, modelId);
        lakeTaskService.remove(taskWrapper);
        // 删除模型文件
        if (entity.getModelPath() != null && !entity.getModelPath().isEmpty()) {
            File file = new File(entity.getModelPath());
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    log.warn("模型文件删除失败：{}", entity.getModelPath());
                }
            }
        }
        lakeModelInfoService.removeById(modelId);
        log.info("模型[{}]已删除", modelId);
    }

    /**
     * 模型→任务 级联数据：{modelId, modelName, tasks:[{taskId, taskName, status, modelPath}]}
     * taskType 非空时按数据集的类型过滤模型
     */
    public List<Map<String, Object>> modelTree(String taskType) {
        LambdaQueryWrapper<LakeModelInfoEntity> mw = new LambdaQueryWrapper<>();
        if (taskType != null && !taskType.isEmpty()) {
            LambdaQueryWrapper<LakeDatasetEntity> dw = new LambdaQueryWrapper<>();
            dw.eq(LakeDatasetEntity::getTaskType, taskType);
            List<Long> datasetIds = lakeDatasetService.list(dw).stream()
                    .map(LakeDatasetEntity::getDatasetId).collect(java.util.stream.Collectors.toList());
            if (datasetIds.isEmpty()) {
                return new ArrayList<>();
            }
            mw.in(LakeModelInfoEntity::getDatasetId, datasetIds);
        }
        mw.orderByDesc(LakeModelInfoEntity::getModelId);
        List<Map<String, Object>> tree = new ArrayList<>();
        for (LakeModelInfoEntity model : lakeModelInfoService.list(mw)) {
            Map<String, Object> node = new HashMap<>();
            node.put("modelId", model.getModelId());
            node.put("modelName", model.getModelName());
            LambdaQueryWrapper<LakeTaskEntity> tw = new LambdaQueryWrapper<>();
            tw.eq(LakeTaskEntity::getModelId, model.getModelId());
            tw.orderByDesc(LakeTaskEntity::getTaskId);
            List<Map<String, Object>> tasks = new ArrayList<>();
            for (LakeTaskEntity task : lakeTaskService.list(tw)) {
                Map<String, Object> tn = new HashMap<>();
                tn.put("taskId", task.getTaskId());
                tn.put("taskName", task.getTaskName());
                tn.put("status", task.getStatus());
                tn.put("modelPath", task.getModelPath());
                tasks.add(tn);
            }
            node.put("tasks", tasks);
            tree.add(node);
        }
        return tree;
    }

    private ModelInfoDTO toDTO(LakeModelInfoEntity entity) {
        ModelInfoDTO dto = new ModelInfoDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setStatus(entity.getStatus());
        if (entity.getDatasetId() != null) {
            LakeDatasetEntity dataset = lakeDatasetService.getById(entity.getDatasetId());
            if (dataset != null) {
                dto.setDatasetName(dataset.getDatasetName());
            }
        }
        return dto;
    }

    public List<Map<String, Object>> listArchitectures(String taskType) {
        List<Map<String, Object>> architectures = new ArrayList<>();
        if ("CLASSIFICATION".equals(taskType)) {
            architectures.add(new HashMap<String, Object>() {{
                put("id", "cnn_classifier");
                put("name", "CNN 分类器");
                put("taskType", "CLASSIFICATION");
            }});
        } else if ("SIMILARITY".equals(taskType)) {
            architectures.add(new HashMap<String, Object>() {{
                put("id", "similarity_autoencoder");
                put("name", "相似度自编码器");
                put("taskType", "SIMILARITY");
            }});
        } else {
            architectures.add(new HashMap<String, Object>() {{
                put("id", "similarity_autoencoder");
                put("name", "相似度自编码器");
                put("taskType", "SIMILARITY");
            }});
            architectures.add(new HashMap<String, Object>() {{
                put("id", "cnn_classifier");
                put("name", "CNN 分类器");
                put("taskType", "CLASSIFICATION");
            }});
        }
        return architectures;
    }
}
