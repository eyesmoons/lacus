package com.lacus.service.oneapi;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.oneapi.entity.OneApiInfoEntity;

/**
 * <p>
 * api详情表 服务类
 * </p>
 *
 * @author casey
 * @since 2025-03-14
 */
public interface IOneApiInfoService extends IService<OneApiInfoEntity> {
    OneApiInfoEntity queryApiByUrl(String apiUrl);
}
