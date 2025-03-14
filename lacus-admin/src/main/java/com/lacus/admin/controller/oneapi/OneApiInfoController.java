package com.lacus.admin.controller.oneapi;

import com.alibaba.fastjson2.JSONObject;
import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.core.annotations.AccessLog;
import com.lacus.dao.oneapi.entity.OneApiInfoEntity;
import com.lacus.domain.common.command.BulkOperationCommand;
import com.lacus.domain.oneapi.OneApiInfoBusiness;
import com.lacus.domain.oneapi.command.ApiAddCommand;
import com.lacus.domain.oneapi.command.ApiUpdateCommand;
import com.lacus.domain.oneapi.dto.ApiInfoDTO;
import com.lacus.domain.oneapi.dto.ApiParseDTO;
import com.lacus.domain.oneapi.dto.RunningTestResponse;
import com.lacus.domain.oneapi.query.OneApiInfoQuery;
import com.lacus.enums.ColumnTypeEnum;
import com.lacus.enums.dictionary.BusinessTypeEnum;
import com.lacus.service.oneapi.dto.ApiParamsDTO;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * 统一api
 */
@RestController
@RequestMapping("/one/api")
public class OneApiInfoController {

    @Autowired
    private OneApiInfoBusiness oneApiInfoBusiness;

    /**
     * 分页查询API列表
     */
    @PreAuthorize("@permission.has('oneapi:api:list')")
    @GetMapping("/list/paging")
    public ResponseDTO<PageDTO> list(OneApiInfoQuery query) {
        PageDTO pageDTO = oneApiInfoBusiness.pageList(query);
        return ResponseDTO.ok(pageDTO);
    }

    /**
     * 新增API
     */
    @PreAuthorize("@permission.has('oneapi:api:add')")
    @AccessLog(title = "API", businessType = BusinessTypeEnum.ADD)
    @PostMapping
    public ResponseDTO<?> add(@RequestBody ApiAddCommand addCommand) {
        return ResponseDTO.ok(oneApiInfoBusiness.addApi(addCommand));
    }

    /**
     * 修改API
     */
    @PreAuthorize("@permission.has('oneapi:api:edit')")
    @AccessLog(title = "API", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping
    public ResponseDTO<?> edit(@RequestBody ApiUpdateCommand updateCommand) {
        oneApiInfoBusiness.updateApi(updateCommand);
        return ResponseDTO.ok();
    }

    /**
     * 根据API ID获取详细信息
     */
    @PreAuthorize("@permission.has('oneapi:api:query')")
    @GetMapping(value = "/{apiId}")
    public ResponseDTO<OneApiInfoEntity> getInfo(@PathVariable @NotNull @Positive Long apiId) {
        return ResponseDTO.ok(oneApiInfoBusiness.getApiInfo(apiId));
    }

    /**
     * 删除API
     */
    @PreAuthorize("@permission.has('oneapi:api:remove')")
    @AccessLog(title = "API", businessType = BusinessTypeEnum.DELETE)
    @DeleteMapping("/{apiIds}")
    public ResponseDTO<?> remove(@PathVariable List<Long> apiIds) {
        oneApiInfoBusiness.deleteApi(new BulkOperationCommand<>(apiIds));
        return ResponseDTO.ok();
    }

    /**
     * 解析接口
     */
    @PreAuthorize("@permission.has('oneapi:api:list')")
    @PostMapping("/parse")
    public ResponseDTO<ApiParamsDTO> parse(@Validated @RequestBody ApiParseDTO parseDTO) {
        return ResponseDTO.ok(oneApiInfoBusiness.parse(parseDTO));
    }

    /**
     * 获取所有字段类型
     */
    @GetMapping("/colTypeList")
    public ResponseDTO<List<JSONObject>> colTypeList() {
        return ResponseDTO.ok(ColumnTypeEnum.getList());
    }

    /**
     * 测试运行
     */
    @PostMapping("/testRun")
    public ResponseDTO<RunningTestResponse> testRun(@Validated @RequestBody ApiInfoDTO apiDTO) {
        return oneApiInfoBusiness.testRun(apiDTO);
    }

    /**
     * 在线测试运行
     */
    @PostMapping("onlineTestRun")
    public ResponseDTO<RunningTestResponse> onlineTestRun(@RequestBody ApiInfoDTO apiInfoDTO) {
        return oneApiInfoBusiness.onlineTestRun(apiInfoDTO);
    }

    /**
     * 更新状态
     */
    @GetMapping("/updateStatus")
    public ResponseDTO<Boolean> updateStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        return ResponseDTO.ok(oneApiInfoBusiness.updateStatus(id, status));
    }
}

