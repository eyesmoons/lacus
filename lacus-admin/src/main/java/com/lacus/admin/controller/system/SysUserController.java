package com.lacus.admin.controller.system;

import cn.hutool.core.collection.ListUtil;
import com.lacus.common.core.base.BaseController;
import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.utils.poi.CustomExcelUtil;
import com.lacus.domain.common.command.BulkOperationCommand;
import com.lacus.domain.system.user.query.SearchUserQuery;
import com.lacus.domain.system.user.dto.UserDTO;
import com.lacus.domain.system.user.dto.UserDetailDTO;
import com.lacus.domain.system.user.UserApplicationService;
import com.lacus.domain.system.user.dto.UserInfoDTO;
import com.lacus.domain.system.user.command.AddUserCommand;
import com.lacus.domain.system.user.command.ChangeStatusCommand;
import com.lacus.domain.system.user.command.ResetPasswordCommand;
import com.lacus.domain.system.user.command.UpdateUserCommand;
import com.lacus.core.annotations.AccessLog;
import com.lacus.core.web.domain.login.LoginUser;
import com.lacus.core.security.AuthenticationUtils;
import com.lacus.dao.system.enums.dictionary.BusinessTypeEnum;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户信息
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {

    @Autowired
    private UserApplicationService userApplicationService;

    /**
     * 获取用户列表
     */
    @PreAuthorize("@permission.has('system:user:list') AND @dataScope.checkDeptId(#query.deptId)")
    @GetMapping("/list")
    public ResponseDTO<PageDTO> list(SearchUserQuery query) {
        PageDTO page = userApplicationService.getUserList(query);
        return ResponseDTO.ok(page);
    }

    @AccessLog(title = "用户管理", businessType = BusinessTypeEnum.EXPORT)
    @PreAuthorize("@permission.has('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SearchUserQuery query) {
        PageDTO userList = userApplicationService.getUserList(query);
        CustomExcelUtil.writeToResponse(userList.getRows(), UserDTO.class, response);
    }

    @AccessLog(title = "用户管理", businessType = BusinessTypeEnum.IMPORT)
    @PreAuthorize("@permission.has('system:user:import')")
    @PostMapping("/importData")
    public ResponseDTO<?> importData(MultipartFile file) {
        List<?> commands = CustomExcelUtil.readFromRequest(AddUserCommand.class, file);

        for (Object command : commands) {
            AddUserCommand addUserCommand = (AddUserCommand) command;
            userApplicationService.addUser(addUserCommand);
        }
        return ResponseDTO.ok();
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        CustomExcelUtil.writeToResponse(ListUtil.toList(new AddUserCommand()), AddUserCommand.class, response);
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@permission.has('system:user:query')")
    @GetMapping(value = {"/", "/{userId}"})
    public ResponseDTO<UserDetailDTO> getUserDetailInfo(@PathVariable(value = "userId", required = false) Long userId) {
        UserDetailDTO userDetailInfo = userApplicationService.getUserDetailInfo(userId);
        return ResponseDTO.ok(userDetailInfo);
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@permission.has('system:user:add') AND @dataScope.checkDeptId(#command.deptId)")
    @AccessLog(title = "用户管理", businessType = BusinessTypeEnum.ADD)
    @PostMapping
    public ResponseDTO<?> add(@Validated @RequestBody AddUserCommand command) {
        userApplicationService.addUser(command);
        return ResponseDTO.ok();
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@permission.has('system:user:edit') AND @dataScope.checkUserId(#command.userId)")
    @AccessLog(title = "用户管理", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping
    public ResponseDTO<?> edit(@Validated @RequestBody UpdateUserCommand command) {
        userApplicationService.updateUser(command);
        return ResponseDTO.ok();
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@permission.has('system:user:remove') AND @dataScope.checkUserIds(#userIds)")
    @AccessLog(title = "用户管理", businessType = BusinessTypeEnum.DELETE)
    @DeleteMapping("/{userIds}")
    public ResponseDTO<?> remove(@PathVariable List<Long> userIds) {
        BulkOperationCommand<Long> bulkDeleteCommand = new BulkOperationCommand(userIds);
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        userApplicationService.deleteUsers(loginUser, bulkDeleteCommand);
        return ResponseDTO.ok();
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@permission.has('system:user:resetPwd') AND @dataScope.checkUserId(#userId)")
    @AccessLog(title = "用户管理", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping("/{userId}/password/reset")
    public ResponseDTO<?> resetPassword(@PathVariable Long userId, @RequestBody ResetPasswordCommand command) {
        command.setUserId(userId);
        userApplicationService.resetUserPassword(command);
        return ResponseDTO.ok();
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@permission.has('system:user:edit') AND @dataScope.checkUserId(#command.userId)")
    @AccessLog(title = "用户管理", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping("/{userId}/status")
    public ResponseDTO<?> changeStatus(@PathVariable Long userId, @RequestBody ChangeStatusCommand command) {
        command.setUserId(userId);
        userApplicationService.changeUserStatus(command);
        return ResponseDTO.ok();
    }

    /**
     * 根据用户编号获取授权角色
     */
    @PreAuthorize("@permission.has('system:user:query')")
    @GetMapping("/{userId}/role")
    public ResponseDTO<UserInfoDTO> getRoleOfUser(@PathVariable("userId") Long userId) {
        UserInfoDTO userWithRole = userApplicationService.getUserWithRole(userId);
        return ResponseDTO.ok(userWithRole);
    }



}
