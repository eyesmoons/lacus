package com.lacus.admin.controller.system;

import com.lacus.common.core.base.BaseController;
import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.utils.poi.CustomExcelUtil;
import com.lacus.domain.system.role.command.AddRoleCommand;
import com.lacus.domain.system.role.query.AllocatedRoleQuery;
import com.lacus.domain.system.role.dto.RoleDTO;
import com.lacus.domain.system.role.RoleBusiness;
import com.lacus.domain.system.role.query.RoleQuery;
import com.lacus.domain.system.role.query.UnallocatedRoleQuery;
import com.lacus.domain.system.role.command.UpdateDataScopeCommand;
import com.lacus.domain.system.role.command.UpdateRoleCommand;
import com.lacus.domain.system.role.command.UpdateStatusCommand;
import com.lacus.core.annotations.AccessLog;
import com.lacus.core.web.domain.login.LoginUser;
import com.lacus.core.security.AuthenticationUtils;
import com.lacus.enums.dictionary.BusinessTypeEnum;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
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
 * 角色信息
 */
@RestController
@RequestMapping("/system/role")
@Validated
public class SysRoleController extends BaseController {

    @Autowired
    private RoleBusiness roleBusiness;

    @PreAuthorize("@permission.has('system:role:list')")
    @GetMapping("/list")
    public ResponseDTO<PageDTO> list(RoleQuery query) {
        PageDTO pageDTO = roleBusiness.getRoleList(query);
        return ResponseDTO.ok(pageDTO);
    }

    @AccessLog(title = "角色管理", businessType = BusinessTypeEnum.EXPORT)
    @PreAuthorize("@permission.has('system:role:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, RoleQuery query) {
        PageDTO pageDTO = roleBusiness.getRoleList(query);
        CustomExcelUtil.writeToResponse(pageDTO.getRows(), RoleDTO.class, response);
    }

    /**
     * 根据角色编号获取详细信息
     */
    @PreAuthorize("@permission.has('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public ResponseDTO<?> getInfo(@PathVariable @NotNull Long roleId) {
        RoleDTO roleInfo = roleBusiness.getRoleInfo(roleId);
        return ResponseDTO.ok(roleInfo);
    }

    /**
     * 新增角色
     */
    @PreAuthorize("@permission.has('system:role:add')")
    @AccessLog(title = "角色管理", businessType = BusinessTypeEnum.ADD)
    @PostMapping
    public ResponseDTO<?> add(@RequestBody AddRoleCommand addCommand) {
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        roleBusiness.addRole(addCommand, loginUser);
        return ResponseDTO.ok();
    }

    /**
     * 新增角色
     */
    @PreAuthorize("@permission.has('system:role:remove')")
    @AccessLog(title = "角色管理", businessType = BusinessTypeEnum.ADD)
    @DeleteMapping(value = "/{roleId}")
    public ResponseDTO<?> remove(@PathVariable("roleId")List<Long> roleIds) {
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        roleBusiness.deleteRoleByBulk(roleIds, loginUser);
        return ResponseDTO.ok();
    }

    /**
     * 修改保存角色
     */
    @PreAuthorize("@permission.has('system:role:edit')")
    @AccessLog(title = "角色管理", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping
    public ResponseDTO<?> edit(@Validated @RequestBody UpdateRoleCommand updateCommand) {
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        roleBusiness.updateRole(updateCommand, loginUser);
        return ResponseDTO.ok();
    }

    /**
     * 修改保存数据权限
     */
    @PreAuthorize("@permission.has('system:role:edit')")
    @AccessLog(title = "角色管理", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping("/{roleId}/dataScope")
    public ResponseDTO<?> dataScope(@PathVariable("roleId")Long roleId, @RequestBody UpdateDataScopeCommand command) {
        command.setRoleId(roleId);

        roleBusiness.updateDataScope(command);
        return ResponseDTO.ok();
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@permission.has('system:role:edit')")
    @AccessLog(title = "角色管理", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping("/{roleId}/status")
    public ResponseDTO<?> changeStatus(@PathVariable("roleId")Long roleId, @RequestBody UpdateStatusCommand command) {
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        command.setRoleId(roleId);
        roleBusiness.updateStatus(command, loginUser);
        return ResponseDTO.ok();
    }

    /**
     * 查询已分配用户角色列表
     */
    @PreAuthorize("@permission.has('system:role:list')")
    @GetMapping("/{roleId}/allocated/list")
    public ResponseDTO<PageDTO> allocatedUserList(@PathVariable("roleId")Long roleId, AllocatedRoleQuery query) {
        query.setRoleId(roleId);
        PageDTO page = roleBusiness.getAllocatedUserList(query);
        return ResponseDTO.ok(page);
    }

    /**
     * 查询未分配用户角色列表
     */
    @PreAuthorize("@permission.has('system:role:list')")
    @GetMapping("/{roleId}/unallocated/list")
    public ResponseDTO<PageDTO> unallocatedUserList(@PathVariable("roleId")Long roleId, UnallocatedRoleQuery query) {
        query.setRoleId(roleId);
        PageDTO page = roleBusiness.getUnallocatedUserList(query);
        return ResponseDTO.ok(page);
    }

    /**
     * 取消授权用户
     */
    @PreAuthorize("@permission.has('system:role:edit')")
    @AccessLog(title = "角色管理", businessType = BusinessTypeEnum.GRANT)
    @DeleteMapping("/{roleId}/user/grant")
    public ResponseDTO<?> deleteRoleOfUser(@PathVariable("roleId")Long roleId, @RequestBody Long userId) {
        roleBusiness.deleteRoleOfUser(userId);
        return ResponseDTO.ok();
    }

    /**
     * 批量取消授权用户
     */
    @PreAuthorize("@permission.has('system:role:edit')")
    @AccessLog(title = "角色管理", businessType = BusinessTypeEnum.GRANT)
    @DeleteMapping("/users/{userIds}/grant/bulk")
    public ResponseDTO<?> deleteRoleOfUserByBulk(@PathVariable("userIds") List<Long> userIds) {
        roleBusiness.deleteRoleOfUserByBulk(userIds);
        return ResponseDTO.ok();
    }

    /**
     * 批量选择用户授权
     */
    @PreAuthorize("@permission.has('system:role:edit')")
    @AccessLog(title = "角色管理", businessType = BusinessTypeEnum.GRANT)
    @PostMapping("/{roleId}/users/{userIds}/grant/bulk")
    public ResponseDTO<?> addRoleForUserByBulk(@PathVariable("roleId") Long roleId,
        @PathVariable("userIds") List<Long> userIds) {
        roleBusiness.addRoleOfUserByBulk(roleId, userIds);
        return ResponseDTO.ok();
    }
}
