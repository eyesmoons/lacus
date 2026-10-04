package com.lacus.service.lakeintelligence;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.lakeintelligence.entity.LakeDatasetEntity;

/**
 * 图片库元信息 Service 接口
 */
public interface ILakeDatasetService extends IService<LakeDatasetEntity> {

    /**
     * 校验数据集名称是否重复
     *
     * @param id         数据集ID（更新时传入，新增时传 null）
     * @param datasetName 数据集名称
     * @return true=重复
     */
    boolean isDatasetNameDuplicated(Long id, String datasetName);
}
