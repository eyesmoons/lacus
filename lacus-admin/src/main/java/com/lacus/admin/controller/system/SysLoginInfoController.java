package com.lacus.admin.controller.system;


import com.lacus.common.core.base.BaseController;
import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.utils.poi.CustomExcelUtil;
import com.lacus.domain.common.command.BulkOperationCommand;
import com.lacus.domain.system.logininfo.dto.LoginInfoDTO;
import com.lacus.domain.system.logininfo.LoginInfoBusiness;
import com.lacus.domain.system.logininfo.query.LoginInfoQuery;
import com.lacus.core.annotations.AccessLog;
import com.lacus.dao.system.enums.dictionary.BusinessTypeEnum;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统访问记录
 */
@RestController
@RequestMapping("/loginInfo")
@Validated
public class SysLoginInfoController extends BaseController {

    @Autowired
    private LoginInfoBusiness loginInfoBusiness;

    @PreAuthorize("@permission.has('monitor:logininfor:list')")
    @GetMapping("/list")
    public ResponseDTO<PageDTO> list(LoginInfoQuery query) {
        PageDTO pageDTO = loginInfoBusiness.getLoginInfoList(query);
        return ResponseDTO.ok(pageDTO);
    }

    @AccessLog(title = "登录日志", businessType = BusinessTypeEnum.EXPORT)
    @PreAuthorize("@permission.has('monitor:logininfor:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, LoginInfoQuery query) {
        PageDTO pageDTO = loginInfoBusiness.getLoginInfoList(query);
        CustomExcelUtil.writeToResponse(pageDTO.getRows(), LoginInfoDTO.class, response);
    }

    @PreAuthorize("@permission.has('monitor:logininfor:remove')")
    @AccessLog(title = "登录日志", businessType = BusinessTypeEnum.DELETE)
    @DeleteMapping("/{infoIds}")
    public ResponseDTO<?> remove(@PathVariable @NotNull @NotEmpty List<Long> infoIds) {
        loginInfoBusiness.deleteLoginInfo(new BulkOperationCommand<>(infoIds));
        return ResponseDTO.ok();
    }

    @PreAuthorize("@permission.has('monitor:logininfor:remove')")
    @AccessLog(title = "登录日志", businessType = BusinessTypeEnum.CLEAN)
    @DeleteMapping("/clean")
    public ResponseDTO<?> clean() {
        return ResponseDTO.fail(ErrorCode.Business.UNSUPPORTED_OPERATION);
    }
}
