package com.lacus.domain.lakeintelligence;

import com.lacus.common.exception.CustomException;
import com.lacus.dao.lakeintelligence.entity.LakeDatasetEntity;
import com.lacus.dao.lakeintelligence.entity.LakeVectorIndexEntity;
import com.lacus.domain.lakeintelligence.command.BuildVectorRequest;
import com.lacus.domain.lakeintelligence.dto.ProgressResponse;
import com.lacus.domain.lakeintelligence.dto.VectorIndexDTO;
import com.lacus.domain.lakeintelligence.feign.MlServiceFeign;
import com.lacus.enums.TaskStatus;
import com.lacus.service.lakeintelligence.ILakeDatasetService;
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

    /**
     * 构建向量库
     */
    public VectorIndexDTO buildVectors(BuildVectorRequest request) {
        if (lakeVectorIndexService.isIndexNameDuplicated(null, request.getIndexName())) {
            throw new CustomException("向量库名称[" + request.getIndexName() + "]已存在");
        }
        // 构建 ML 服务请求
        Map<String, Object> mlRequest = new HashMap<>();
        mlRequest.put("dataset_uri", resolveDatasetUri(request.getDatasetId()));
        mlRequest.put("collection_name", request.getCollectionName());
        mlRequest.put("batch_size", request.getBatchSize());

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
        entity.setDatasetId(request.getDatasetId());
        entity.setModelId(request.getModelId());
        entity.setDistanceMetric(request.getDistanceMetric());
        entity.setBuildStatus("BUILDING");
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
        return response;
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

    private VectorIndexDTO toDTO(LakeVectorIndexEntity entity) {
        VectorIndexDTO dto = new VectorIndexDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public List<VectorIndexDTO> listCollections() {
        List<LakeVectorIndexEntity> entities = lakeVectorIndexService.list();
        return entities.stream().map(this::toDTO).collect(java.util.stream.Collectors.toList());
    }
}
