package com.lacus.service.system.impl;

import com.lacus.service.system.ISysRoleMenuService;
import com.lacus.dao.system.entity.SysRoleMenuEntity;
import com.lacus.dao.system.mapper.SysRoleMenuMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 角色和菜单关联表 服务实现类
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenuEntity> implements
        ISysRoleMenuService {

}
