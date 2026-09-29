package com.lacus.service.oneapi;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.oneapi.entity.OneApiInfoEntity;

public interface IOneApiInfoService extends IService<OneApiInfoEntity> {
    OneApiInfoEntity queryApiByUrl(String apiUrl);
}
