package com.lacus.domain.lakeintelligence;

import com.lacus.common.exception.CustomException;
import com.lacus.dao.lakeintelligence.entity.LakeDatasetEntity;
import com.lacus.dao.lakeintelligence.entity.LakeModelInfoEntity;
import com.lacus.dao.lakeintelligence.entity.LakeTaskEntity;
import com.lacus.dao.lakeintelligence.entity.LakeVectorIndexEntity;
import com.lacus.domain.lakeintelligence.command.BuildVectorRequest;
import com.lacus.domain.lakeintelligence.dto.ProgressResponse;
import com.lacus.domain.lakeintelligence.dto.VectorIndexDTO;
import com.lacus.domain.lakeintelligence.feign.MlServiceFeign;
import com.lacus.enums.TaskStatus;
import com.lacus.service.lakeintelligence.ILakeDatasetService;
import com.lacus.service.lakeintelligence.ILakeModelInfoService;
import com.lacus.service.lakeintelligence.ILakeVectorIndexService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 向量库业务逻辑
 */
@Slf4j
@Service
public class VectorIndexBusiness {

    @Autowired
    private ILakeVectorIndexService lakeVectorIndexService;

    @Autowired
    private ILakeDatasetService lakeDatasetService;

    @Autowired
    private MlServiceFeign mlServiceFeign;

    @Autowired
    private ILakeModelInfoService lakeModelInfoService;

    @Autowired
    private com.lacus.service.lakeintelligence.ILakeTaskService lakeTaskService;

    /**
     * 构建向量库
     */
    public VectorIndexDTO buildVectors(BuildVectorRequest request) {
        if (lakeVectorIndexService.isIndexNameDuplicated(null, request.getIndexName())) {
            throw new CustomException("向量库名称[" + request.getIndexName() + "]已存在");
        }
        // 集合名直接用"向量库名称"（规整为 ChromaDB 合法名称）
        String collectionName = toCollectionName(request.getIndexName());
        // 构建 ML 服务请求
        Map<String, Object> mlRequest = new HashMap<>();
        mlRequest.put("dataset_uri", resolveDatasetUri(request.getDatasetId()));
        mlRequest.put("collection_name", collectionName);
        mlRequest.put("batch_size", request.getBatchSize());
        mlRequest.put("dataset_id", request.getDatasetId());
        mlRequest.put("distance_metric", request.getDistanceMetric());
        // 解析选中的训练任务，用该次训练产出的模型文件
        if (request.getTaskId() == null) {
            throw new CustomException("请选择训练任务");
        }
        LakeTaskEntity task = lakeTaskService.getById(request.getTaskId());
        if (task == null) {
            throw new CustomException("训练任务[" + request.getTaskId() + "]不存在");
        }
        if (task.getModelPath() == null || task.getModelPath().isEmpty()) {
            throw new CustomException("该训练任务没有可用的模型文件");
        }
        mlRequest.put("model_path", task.getModelPath());

        Map<String, Object> response;
        try {
            response = mlServiceFeign.buildVectors(mlRequest);
        } catch (Exception e) {
            throw new CustomException("构建向量库失败：" + e.getMessage());
        }

        if (response == null || (response.get("code") != null && Integer.valueOf(-1).equals(response.get("code")))) {
            throw new CustomException("构建向量库失败：" + (response != null ? response.get("message") : "无响应"));
        }

        // 创建本地向量库记录
        LakeVectorIndexEntity entity = new LakeVectorIndexEntity();
        entity.setIndexName(request.getIndexName());
        entity.setCollectionName(collectionName);
        entity.setDatasetId(request.getDatasetId());
        entity.setModelId(task.getModelId());
        entity.setDistanceMetric(request.getDistanceMetric());
        entity.setBuildStatus("BUILDING");
        entity.setMlTaskId((String) response.get("task_id"));
        entity.setCreatorId(request.getCreatorId());
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        entity.setDeleted(0);
        lakeVectorIndexService.save(entity);
        return toDTO(entity);
    }

    /**
     * 查询构建进度
     */
    public ProgressResponse getBuildProgress(Long indexId) {
        LakeVectorIndexEntity entity = lakeVectorIndexService.getById(indexId);
        if (ObjectUtils.isEmpty(entity)) {
            throw new CustomException("向量库[" + indexId + "]不存在");
        }
        ProgressResponse response = new ProgressResponse();
        response.setTaskId(String.valueOf(indexId));
        response.setStatus(entity.getBuildStatus());
        response.setMessage(entity.getErrorMessage());

        // 构建中且有 ML 任务时，实时查询构建进度
        String mlTaskId = entity.getMlTaskId();
        if ("BUILDING".equals(entity.getBuildStatus()) && mlTaskId != null && !mlTaskId.isEmpty()) {
            try {
                Map<String, Object> mlResp = mlServiceFeign.getBuildProgress(mlTaskId);
                if (mlResp != null) {
                    Object progress = mlResp.get("progress");
                    Object total = mlResp.get("total");
                    if (progress instanceof Number) {
                        response.setProgress(((Number) progress).intValue());
                    }
                    if (total instanceof Number) {
                        response.setTotal(((Number) total).intValue());
                    }
                    Object message = mlResp.get("message");
                    if (message != null) {
                        response.setMessage(message.toString());
                    }
                    String mapped = mapBuildStatus((String) mlResp.get("status"));
                    if (mapped != null) {
                        if (!mapped.equals(entity.getBuildStatus())) {
                            entity.setBuildStatus(mapped);
                            if ("COMPLETED".equals(mapped) && total instanceof Number) {
                                entity.setTotalVectors(((Number) total).intValue());
                            }
                            entity.setUpdateTime(new Date());
                            lakeVectorIndexService.updateById(entity);
                        }
                        response.setStatus(mapped);
                    }
                }
            } catch (Exception e) {
                log.warn("查询向量构建进度失败: indexId={}, error={}", indexId, e.getMessage());
            }
        }
        return response;
    }

    /**
     * 映射 ML 端构建状态到本地状态
     */
    private String mapBuildStatus(String mlStatus) {
        if (mlStatus == null) {
            return null;
        }
        switch (mlStatus.toLowerCase()) {
            case "building":
                return "BUILDING";
            case "completed":
                return "COMPLETED";
            case "failed":
                return "FAILED";
            case "cancelled":
                return "CANCELLED";
            default:
                return null;
        }
    }

    /**
     * 获取向量库详情
     */
    public VectorIndexDTO detail(Long indexId) {
        LakeVectorIndexEntity entity = lakeVectorIndexService.getById(indexId);
        if (ObjectUtils.isEmpty(entity)) {
            throw new CustomException("向量库[" + indexId + "]不存在");
        }
        return toDTO(entity);
    }

    private String resolveDatasetUri(Long datasetId) {
        if (datasetId == null) {
            throw new CustomException("数据集ID不能为空");
        }
        LakeDatasetEntity dataset = lakeDatasetService.getById(datasetId);
        if (dataset == null) {
            throw new CustomException("数据集[" + datasetId + "]不存在");
        }
        if (dataset.getLocalPath() == null || dataset.getLocalPath().isEmpty()) {
            throw new CustomException("数据集[" + datasetId + "]本地路径不存在");
        }
        return dataset.getLocalPath();
    }

    /**
     * 由向量库名称生成 ChromaDB 合法集合名：
     * 仅保留 [a-zA-Z0-9._-]，去掉首尾非字母数字；过短/过长做兜底与截断。
     */
    private String toCollectionName(String indexName) {
        String base = indexName == null ? "" : indexName.trim();
        String s = base.replaceAll("[^a-zA-Z0-9._-]", "_")
                .replaceAll("^[^a-zA-Z0-9]+", "")
                .replaceAll("[^a-zA-Z0-9]+$", "");
        if (s.length() < 3) {
            s = "col" + Math.abs(base.hashCode());
        }
        if (s.length() > 512) {
            s = s.substring(0, 512);
        }
        return s;
    }

    private VectorIndexDTO toDTO(LakeVectorIndexEntity entity) {
        VectorIndexDTO dto = new VectorIndexDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getDatasetId() != null) {
            LakeDatasetEntity dataset = lakeDatasetService.getById(entity.getDatasetId());
            if (dataset != null) {
                dto.setDatasetName(dataset.getDatasetName());
            }
        }
        if (entity.getModelId() != null) {
            LakeModelInfoEntity model = lakeModelInfoService.getById(entity.getModelId());
            if (model != null) {
                dto.setModelName(model.getModelName());
            }
        }
        return dto;
    }

    public List<VectorIndexDTO> listCollections() {
        List<LakeVectorIndexEntity> entities = lakeVectorIndexService.list();
        return entities.stream().map(this::toDTO).collect(java.util.stream.Collectors.toList());
    }
}
