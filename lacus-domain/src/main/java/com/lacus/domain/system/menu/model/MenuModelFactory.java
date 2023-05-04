package com.lacus.domain.system.menu.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.domain.system.menu.command.AddMenuCommand;
import com.lacus.dao.system.entity.SysMenuEntity;
import com.lacus.service.system.ISysMenuService;

/**
 * 菜单模型工厂
 */
public class MenuModelFactory {

    public static MenuModel loadFromDb(Long menuId, ISysMenuService menuService) {
        SysMenuEntity byId = menuService.getById(menuId);
        if (byId == null) {
            throw new ApiException(ErrorCode.Business.OBJECT_NOT_FOUND, menuId, "菜单");
        }
        return new MenuModel(byId);
    }

    public static MenuModel loadFromAddCommand(AddMenuCommand command, MenuModel model) {
        BeanUtil.copyProperties(command, model);
        return model;
    }

}
