package com.lacus.service.lakeintelligence;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.lakeintelligence.entity.LakeModelInfoEntity;

/**
 * 模型元信息 Service 接口
 */
public interface ILakeModelInfoService extends IService<LakeModelInfoEntity> {

    /**
     * 校验模型名称是否重复
     *
     * @param id        模型ID（更新时传入，新增时传 null）
     * @param modelName 模型名称
     * @return true=重复
     */
    boolean isModelNameDuplicated(Long id, String modelName);
}
