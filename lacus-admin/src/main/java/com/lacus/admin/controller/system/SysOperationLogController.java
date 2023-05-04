package com.lacus.admin.controller.system;

import com.lacus.common.core.base.BaseController;
import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.common.utils.poi.CustomExcelUtil;
import com.lacus.domain.common.command.BulkOperationCommand;
import com.lacus.domain.system.operationlog.dto.OperationLogDTO;
import com.lacus.domain.system.operationlog.OperationLogApplicationService;
import com.lacus.domain.system.operationlog.query.OperationLogQuery;
import com.lacus.core.annotations.AccessLog;
import com.lacus.dao.system.enums.dictionary.BusinessTypeEnum;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志记录
 */
@RestController
@RequestMapping("/operationLog")
public class SysOperationLogController extends BaseController {

    @Autowired
    private OperationLogApplicationService operationLogApplicationService;

    @PreAuthorize("@permission.has('monitor:operlog:list')")
    @GetMapping("/list")
    public ResponseDTO<PageDTO> list(OperationLogQuery query) {
        PageDTO pageDTO = operationLogApplicationService.getOperationLogList(query);
        return ResponseDTO.ok(pageDTO);
    }

    @AccessLog(title = "操作日志", businessType = BusinessTypeEnum.EXPORT)
    @PreAuthorize("@permission.has('monitor:operlog:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, OperationLogQuery query) {
        PageDTO pageDTO = operationLogApplicationService.getOperationLogList(query);
        CustomExcelUtil.writeToResponse(pageDTO.getRows(), OperationLogDTO.class, response);
    }

    @AccessLog(title = "操作日志", businessType = BusinessTypeEnum.DELETE)
    @PreAuthorize("@permission.has('monitor:operlog:remove')")
    @DeleteMapping("/{operationIds}")
    public ResponseDTO<?> remove(@PathVariable List<Long> operationIds) {
        operationLogApplicationService.deleteOperationLog(new BulkOperationCommand<>(operationIds));
        return ResponseDTO.ok();
    }

    @AccessLog(title = "操作日志", businessType = BusinessTypeEnum.CLEAN)
    @PreAuthorize("@permission.has('monitor:operlog:remove')")
    @DeleteMapping("/clean")
    public ResponseDTO<?> clean() {
        return ResponseDTO.fail(ErrorCode.Business.UNSUPPORTED_OPERATION);
    }
}
