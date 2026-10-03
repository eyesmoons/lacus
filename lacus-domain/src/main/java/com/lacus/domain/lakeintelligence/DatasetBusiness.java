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
     */
    public DatasetDTO createDataset(CreateDatasetRequest request) {
        if (lakeDatasetService.isDatasetNameDuplicated(null, request.getDatasetName())) {
            throw new CustomException("数据集名称[" + request.getDatasetName() + "]已存在");
        }
        LakeDatasetEntity entity = new LakeDatasetEntity();
        entity.setDatasetName(request.getDatasetName());
        entity.setDescription(request.getDescription());
        entity.setStorageSource(request.getStorageSource());
        entity.setSourceConfig(request.getSourceConfig());
        entity.setStatus(DatasetStatus.PROCESSING.getCode());
        entity.setCreatorId(request.getCreatorId());
        entity.setCreateTime(new java.util.Date());
        entity.setUpdateTime(new java.util.Date());
        entity.setDeleted(0);
        lakeDatasetService.save(entity);
        return toDTO(entity);
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
