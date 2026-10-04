package com.lacus.domain.lakeintelligence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.lakeintelligence.entity.LakeDatasetEntity;
import com.lacus.domain.lakeintelligence.command.CreateDatasetRequest;
import com.lacus.domain.lakeintelligence.dto.DatasetDTO;
import com.lacus.domain.lakeintelligence.feign.MlServiceFeign;
import com.lacus.domain.lakeintelligence.query.DatasetPageQuery;
import com.lacus.enums.DatasetStatus;
import com.lacus.service.lakeintelligence.ILakeDatasetService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据集业务逻辑
 */
@Slf4j
@Service
public class DatasetBusiness {

    @Autowired
    private ILakeDatasetService lakeDatasetService;

    @Autowired
    private MlServiceFeign mlServiceFeign;

    /**
     * 分页查询数据集列表
     */
    public PageDTO pageList(DatasetPageQuery query) {
        return new PageDTO(lakeDatasetService.page(query.toPage(), query.toQueryWrapper()));
    }

    /**
     * 创建数据集
     *
     * <p>支持通过 task_type 参数路由到不同的数据集校验逻辑：
     * IMAGE_SIMILARITY 仅需图片目录；IMAGE_CLASSIFICATION 需要标签信息（CSV 或子目录结构）。</p>
     */
    public DatasetDTO createDataset(CreateDatasetRequest request) {
        if (lakeDatasetService.isDatasetNameDuplicated(null, request.getDatasetName())) {
            throw new CustomException("数据集名称[" + request.getDatasetName() + "]已存在");
        }
        // 根据 task_type 路由校验逻辑
        validateDatasetByTaskType(request.getTaskType(), request.getSourceConfig());

        LakeDatasetEntity entity = new LakeDatasetEntity();
        entity.setDatasetName(request.getDatasetName());
        entity.setDescription(request.getDescription());
        entity.setStorageSource(request.getStorageSource());
        entity.setSourceConfig(request.getSourceConfig());
        entity.setTaskType(request.getTaskType());
        entity.setStatus(DatasetStatus.PROCESSING.getCode());
        entity.setCreatorId(request.getCreatorId());
        entity.setCreateTime(new java.util.Date());
        entity.setUpdateTime(new java.util.Date());
        entity.setDeleted(0);
        lakeDatasetService.save(entity);
        return toDTO(entity);
    }

    /**
     * 根据任务类型校验数据集是否满足要求
     *
     * @param taskType     任务类型
     * @param sourceConfig 数据源配置 JSON
     */
    private void validateDatasetByTaskType(String taskType, String sourceConfig) {
        if (taskType == null || taskType.isEmpty()) {
            return;
        }
        if ("IMAGE_CLASSIFICATION".equalsIgnoreCase(taskType)) {
            validateClassificationDataset(sourceConfig);
        }
        // IMAGE_SIMILARITY 暂无特殊校验，预留扩展点
    }

    /**
     * 校验分类数据集是否包含标签信息
     *
     * <p>要求 source_config 中提供 labels_file（CSV 标签文件）或 class_dirs（按类别分组的子目录）。</p>
     */
    private void validateClassificationDataset(String sourceConfig) {
        if (sourceConfig == null || sourceConfig.isEmpty()) {
            throw new CustomException("分类数据集需要提供数据源配置（含标签信息）");
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<?, ?> config = mapper.readValue(sourceConfig, Map.class);
            boolean hasLabels = config.containsKey("labels_file") && config.get("labels_file") != null
                    && !config.get("labels_file").toString().isEmpty();
            boolean hasClassDirs = config.containsKey("class_dirs") && config.get("class_dirs") != null;
            if (!hasLabels && !hasClassDirs) {
                throw new CustomException("分类数据集需要提供 labels_file（标签CSV）或 class_dirs（按类别子目录）");
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("解析数据源配置失败：" + e.getMessage());
        }
    }

    /**
     * 数据源探测
     */
    public Map<String, Object> probeSource(String uri) {
        Map<String, Object> request = new HashMap<>();
        request.put("uri", uri);
        try {
            Map<String, Object> response = mlServiceFeign.probeSource(request);
            if (response == null || (response.get("code") != null && Integer.valueOf(-1).equals(response.get("code")))) {
                throw new CustomException("数据源探测失败：" + (response != null ? response.get("message") : "无响应"));
            }
            return response;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("数据源探测异常：" + e.getMessage());
        }
    }

    /**
     * 删除数据集
     */
    public void deleteDataset(Long datasetId) {
        LakeDatasetEntity entity = lakeDatasetService.getById(datasetId);
        if (ObjectUtils.isEmpty(entity)) {
            throw new CustomException("数据集[" + datasetId + "]不存在");
        }
        lakeDatasetService.removeById(datasetId);
        log.info("数据集[{}]已删除", datasetId);
    }

    /**
     * 获取数据集详情
     */
    public DatasetDTO detail(Long datasetId) {
        LakeDatasetEntity entity = lakeDatasetService.getById(datasetId);
        if (ObjectUtils.isEmpty(entity)) {
            throw new CustomException("数据集[" + datasetId + "]不存在");
        }
        return toDTO(entity);
    }

    private DatasetDTO toDTO(LakeDatasetEntity entity) {
        DatasetDTO dto = new DatasetDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
