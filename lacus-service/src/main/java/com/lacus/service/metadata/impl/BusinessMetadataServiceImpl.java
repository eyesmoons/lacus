package com.lacus.service.metadata.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.metadata.entity.BusinessMetadataEntity;
import com.lacus.dao.metadata.mapper.BusinessMetadataMapper;
import com.lacus.service.metadata.IBusinessMetadataService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BusinessMetadataServiceImpl extends ServiceImpl<BusinessMetadataMapper, BusinessMetadataEntity>
        implements IBusinessMetadataService {

    @Override
    public List<BusinessMetadataEntity> listByBiz(String bizType, String bizId) {
        LambdaQueryWrapper<BusinessMetadataEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusinessMetadataEntity::getBizType, bizType);
        wrapper.eq(BusinessMetadataEntity::getBizId, bizId);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(String bizType, String bizId, List<BusinessMetadataEntity> items) {
        if (items == null) {
            return;
        }
        for (BusinessMetadataEntity item : items) {
            item.setBizType(bizType);
            item.setBizId(bizId);
            upsert(item);
        }
    }

    private void upsert(BusinessMetadataEntity item) {
        if (StringUtils.isEmpty(item.getObjKey())) {
            return;
        }
        BusinessMetadataEntity existing = getOne(new LambdaQueryWrapper<BusinessMetadataEntity>()
                .eq(BusinessMetadataEntity::getBizType, item.getBizType())
                .eq(BusinessMetadataEntity::getBizId, item.getBizId())
                .eq(BusinessMetadataEntity::getObjKey, item.getObjKey())
                .last("limit 1"));
        if (StringUtils.isEmpty(item.getObjValue())) {
            // 空值即删除该条(前端契约: 值为空串则删除)
            if (existing != null) {
                removeById(existing.getId());
            }
            return;
        }
        if (existing == null) {
            save(item);
        } else {
            existing.setObjValue(item.getObjValue());
            updateById(existing);
        }
    }
}