package com.lacus.service.system;

import com.lacus.dao.system.entity.SysConfigEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 参数配置表 服务类
 */
public interface ISysConfigService extends IService<SysConfigEntity> {

    String getConfigValueByKey(String key);

}
