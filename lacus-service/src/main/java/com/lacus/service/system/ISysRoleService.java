package com.lacus.service.system;

import com.lacus.dao.system.entity.SysRoleEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 角色信息表 服务类
 */
public interface ISysRoleService extends IService<SysRoleEntity> {


    /**
     * 校验角色名称是否唯一
     * @return 结果
     */
    boolean isRoleNameDuplicated(Long roleId, String roleName);

    /**
     * 校验角色权限是否唯一
     * @return 结果
     */
    boolean isRoleKeyDuplicated(Long roleId, String roleKey);


    /**
     * 检测角色是否分配给用户
     * @param roleId
     * @return
     */
    boolean isAssignedToUsers(Long roleId);


}
