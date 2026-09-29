package com.lacus.service.metadata;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.metadata.entity.BusinessMetadataEntity;

import java.util.List;

public interface IBusinessMetadataService extends IService<BusinessMetadataEntity> {

    List<BusinessMetadataEntity> listByBiz(String bizType, String bizId);

    void saveBatch(String bizType, String bizId, List<BusinessMetadataEntity> items);
}