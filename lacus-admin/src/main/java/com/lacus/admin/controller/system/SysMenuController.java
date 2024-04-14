package com.lacus.admin.controller.system;

import cn.hutool.core.lang.tree.Tree;
import com.lacus.common.core.base.BaseController;
import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.domain.common.dto.TreeSelectedDTO;
import com.lacus.domain.system.menu.command.AddMenuCommand;
import com.lacus.domain.system.menu.dto.MenuDTO;
import com.lacus.domain.system.menu.MenuApplicationService;
import com.lacus.domain.system.menu.query.MenuQuery;
import com.lacus.domain.system.menu.command.UpdateMenuCommand;
import com.lacus.core.annotations.AccessLog;
import com.lacus.core.web.domain.login.LoginUser;
import com.lacus.core.security.AuthenticationUtils;
import com.lacus.dao.system.enums.dictionary.BusinessTypeEnum;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜单信息
 */
@RestController
@RequestMapping("/system/menu")
@Validated
public class SysMenuController extends BaseController {

    @Autowired
    MenuApplicationService menuApplicationService;

    /**
     * 获取菜单列表
     */
    @PreAuthorize("@permission.has('system:menu:list')")
    @GetMapping("/list")
    public ResponseDTO<?> list(MenuQuery query) {
        List<MenuDTO> menuList = menuApplicationService.getMenuList(query);
        return ResponseDTO.ok(menuList);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @PreAuthorize("@permission.has('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    public ResponseDTO<MenuDTO> getInfo(@PathVariable @NotNull @PositiveOrZero Long menuId) {
        MenuDTO menu = menuApplicationService.getMenuInfo(menuId);
        return ResponseDTO.ok(menu);
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/dropdownList")
    public ResponseDTO<?> dropdownList() {
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        List<Tree<Long>> dropdownList = menuApplicationService.getDropdownList(loginUser);
        return ResponseDTO.ok(dropdownList);
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeSelect/{roleId}")
    public ResponseDTO<?> roleMenuTreeSelect(@PathVariable("roleId") Long roleId) {
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        TreeSelectedDTO roleDropdownList = menuApplicationService.getRoleDropdownList(loginUser, roleId);
        return ResponseDTO.ok(roleDropdownList);
    }

    /**
     * 新增菜单
     */
    @PreAuthorize("@permission.has('system:menu:add')")
    @AccessLog(title = "菜单管理", businessType = BusinessTypeEnum.ADD)
    @PostMapping
    public ResponseDTO<?> add(@RequestBody AddMenuCommand addCommand) {
        menuApplicationService.addMenu(addCommand);
        return ResponseDTO.ok();
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("@permission.has('system:menu:edit')")
    @AccessLog(title = "菜单管理", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping
    public ResponseDTO<?> edit(@RequestBody UpdateMenuCommand updateCommand) {
        menuApplicationService.updateMenu(updateCommand);
        return ResponseDTO.ok();
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("@permission.has('system:menu:remove')")
    @AccessLog(title = "菜单管理", businessType = BusinessTypeEnum.DELETE)
    @DeleteMapping("/{menuId}")
    public ResponseDTO<?> remove(@PathVariable("menuId") Long menuId) {
        menuApplicationService.remove(menuId);
        return ResponseDTO.ok();
    }


}
