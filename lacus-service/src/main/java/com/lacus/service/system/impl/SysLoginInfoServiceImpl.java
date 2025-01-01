package com.lacus.service.system.impl;

import com.lacus.dao.system.entity.SysLoginInfoEntity;
import com.lacus.dao.system.mapper.SysLoginInfoMapper;
import com.lacus.service.system.ISysLoginInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 系统访问记录 服务实现类
 */
@Service
public class SysLoginInfoServiceImpl extends ServiceImpl<SysLoginInfoMapper, SysLoginInfoEntity> implements
        ISysLoginInfoService {

}
