package com.lacus.service.dataserver.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.dao.dataserver.entity.DataServerEntity;
import com.lacus.dao.dataserver.mapper.DataServerMapper;
import com.lacus.service.dataserver.IDataServerService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DataServerServiceImpl extends ServiceImpl<DataServerMapper, DataServerEntity> implements IDataServerService {

    @Override
    public boolean checkUrlUnique(String apiUrl) {
        LambdaQueryWrapper<DataServerEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataServerEntity::getApiUrl, apiUrl);
        List<DataServerEntity> list = this.list(wrapper);
        return CollectionUtils.isNotEmpty(list);
    }

    @Override
    public void add(DataServerEntity dataServerEntity) {
        this.saveOrUpdate(dataServerEntity);
    }


}
