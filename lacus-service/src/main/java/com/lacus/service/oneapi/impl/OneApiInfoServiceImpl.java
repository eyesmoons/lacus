package com.lacus.service.oneapi.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.oneapi.entity.OneApiInfoEntity;
import com.lacus.dao.oneapi.mapper.OneApiInfoMapper;
import com.lacus.service.oneapi.IOneApiInfoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * api详情表 服务实现类
 * </p>
 *
 * @author casey
 * @since 2025-03-14
 */
@Service
public class OneApiInfoServiceImpl extends ServiceImpl<OneApiInfoMapper, OneApiInfoEntity> implements IOneApiInfoService {

    @Override
    public OneApiInfoEntity queryApiByUrl(String apiUrl) {
        LambdaQueryWrapper<OneApiInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OneApiInfoEntity::getApiUrl, apiUrl);
        return this.getOne(queryWrapper);
    }
}
