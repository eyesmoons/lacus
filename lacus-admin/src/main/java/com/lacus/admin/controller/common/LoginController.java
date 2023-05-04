package com.lacus.admin.controller.common;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.lacus.admin.response.UserPermissionDTO;
import com.lacus.admin.request.LoginDTO;
import com.lacus.common.config.LacusConfig;
import com.lacus.common.constant.Constants.Token;
import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.exception.error.ErrorCode.Business;
import com.lacus.domain.system.menu.MenuApplicationService;
import com.lacus.domain.system.menu.dto.RouterDTO;
import com.lacus.domain.system.user.command.AddUserCommand;
import com.lacus.domain.system.user.dto.UserDTO;
import com.lacus.core.cache.map.MapCache;
import com.lacus.core.web.domain.login.CaptchaDTO;
import com.lacus.core.web.domain.login.LoginUser;
import com.lacus.core.web.service.LoginService;
import com.lacus.core.security.AuthenticationUtils;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页
 */
@RestController
public class LoginController {

    private final LoginService loginService;

    private final MenuApplicationService menuApplicationService;
    /**
     * 系统基础配置
     */
    private final LacusConfig lacusConfig;

    public LoginController(LoginService loginService,
        MenuApplicationService menuApplicationService, LacusConfig lacusConfig) {
        this.loginService = loginService;
        this.menuApplicationService = menuApplicationService;
        this.lacusConfig = lacusConfig;
    }

    /**
     * 访问首页，提示语
     */
    @RequestMapping("/")
    public String index() {
        return StrUtil.format("欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问。",
            lacusConfig.getName(), lacusConfig.getVersion());
    }

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public ResponseDTO<CaptchaDTO> getCaptchaImg() {
        CaptchaDTO captchaImg = loginService.getCaptchaImg();
        return ResponseDTO.ok(captchaImg);
    }

    /**
     * 登录方法
     *
     * @param loginDTO 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public ResponseDTO<Map> login(@RequestBody LoginDTO loginDTO) {
        // 生成令牌
        String token = loginService.login(loginDTO.getUsername(), loginDTO.getPassword(), loginDTO.getCode(),
            loginDTO.getUuid());

        return ResponseDTO.ok(MapUtil.of(Token.TOKEN_FIELD, token));
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getLoginUserInfo")
    public ResponseDTO<?> getLoginUserInfo() {
        LoginUser loginUser = AuthenticationUtils.getLoginUser();

        UserPermissionDTO permissionDTO = new UserPermissionDTO();
        permissionDTO.setUser(new UserDTO(loginUser.getEntity()));
        permissionDTO.setRoleKey(loginUser.getRoleInfo().getRoleKey());
        permissionDTO.setPermissions(loginUser.getRoleInfo().getMenuPermissions());
        permissionDTO.setDictTypes(MapCache.dictionaryCache());
        return ResponseDTO.ok(permissionDTO);
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("/getRouters")
    public ResponseDTO<List<RouterDTO>> getRouters() {
        Long userId = AuthenticationUtils.getUserId();
        List<RouterDTO> routerTree = menuApplicationService.getRouterTree(userId);
        return ResponseDTO.ok(routerTree);
    }


    @PostMapping("/register")
    public ResponseDTO<?> register(@RequestBody AddUserCommand command) {
        return ResponseDTO.fail(Business.UNSUPPORTED_OPERATION);
    }

}
