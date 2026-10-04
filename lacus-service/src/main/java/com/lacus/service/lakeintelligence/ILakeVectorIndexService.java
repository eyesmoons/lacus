package com.lacus.service.lakeintelligence;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.lakeintelligence.entity.LakeVectorIndexEntity;

/**
 * 向量库索引 Service 接口
 */
public interface ILakeVectorIndexService extends IService<LakeVectorIndexEntity> {

    /**
     * 校验向量库名称是否重复
     *
     * @param id         向量库ID（更新时传入，新增时传 null）
     * @param indexName  向量库名称
     * @return true=重复
     */
    boolean isIndexNameDuplicated(Long id, String indexName);
}
