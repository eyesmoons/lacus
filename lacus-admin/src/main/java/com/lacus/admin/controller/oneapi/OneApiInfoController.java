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
import com.lacus.domain.oneapi.dto.ApiParamsDTO;
import com.lacus.domain.oneapi.dto.ApiParseDTO;
import com.lacus.domain.oneapi.dto.ApiTestResp;
import com.lacus.domain.oneapi.query.OneApiInfoQuery;
import com.lacus.enums.ColumnTypeEnum;
import com.lacus.enums.dictionary.BusinessTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

@Api(value = "统一API管理", tags = {"统一API管理"})
@RestController
@RequestMapping("/one/api")
public class OneApiInfoController {

    @Autowired
    private OneApiInfoBusiness oneApiInfoBusiness;

    @ApiOperation("分页查询API列表")
    @PreAuthorize("@permission.has('oneapi:api:list')")
    @GetMapping("/list/paging")
    public ResponseDTO<PageDTO> list(OneApiInfoQuery query) {
        PageDTO pageDTO = oneApiInfoBusiness.pageList(query);
        return ResponseDTO.ok(pageDTO);
    }

    @ApiOperation("新增API")
    @PreAuthorize("@permission.has('oneapi:api:add')")
    @AccessLog(title = "API", businessType = BusinessTypeEnum.ADD)
    @PostMapping
    public ResponseDTO<?> add(@RequestBody ApiAddCommand addCommand) {
        return ResponseDTO.ok(oneApiInfoBusiness.addApi(addCommand));
    }

    @ApiOperation("修改API")
    @PreAuthorize("@permission.has('oneapi:api:edit')")
    @AccessLog(title = "API", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping
    public ResponseDTO<?> edit(@RequestBody ApiUpdateCommand updateCommand) {
        oneApiInfoBusiness.updateApi(updateCommand);
        return ResponseDTO.ok();
    }

    @ApiOperation("获取API详细信息")
    @PreAuthorize("@permission.has('oneapi:api:query')")
    @GetMapping(value = "/{apiId}")
    public ResponseDTO<OneApiInfoEntity> getInfo(@PathVariable @NotNull @Positive Long apiId) {
        return ResponseDTO.ok(oneApiInfoBusiness.getApiInfo(apiId));
    }

    @ApiOperation("删除API")
    @PreAuthorize("@permission.has('oneapi:api:remove')")
    @AccessLog(title = "API", businessType = BusinessTypeEnum.DELETE)
    @DeleteMapping("/{apiIds}")
    public ResponseDTO<?> remove(@PathVariable List<Long> apiIds) {
        oneApiInfoBusiness.deleteApi(new BulkOperationCommand<>(apiIds));
        return ResponseDTO.ok();
    }

    @ApiOperation("解析SQL")
    @PreAuthorize("@permission.has('oneapi:api:list')")
    @PostMapping("/parse")
    public ResponseDTO<ApiParamsDTO> parse(@Validated @RequestBody ApiParseDTO parseDTO) {
        return ResponseDTO.ok(oneApiInfoBusiness.parse(parseDTO));
    }

    @ApiOperation("获取所有字段类型")
    @GetMapping("/colTypeList")
    public ResponseDTO<List<JSONObject>> colTypeList() {
        return ResponseDTO.ok(ColumnTypeEnum.getList());
    }

    @ApiOperation("测试API")
    @PostMapping("/test")
    public ResponseDTO<ApiTestResp> testRun(@Validated @RequestBody ApiInfoDTO apiDTO) {
        return oneApiInfoBusiness.testApi(apiDTO);
    }

    @ApiOperation("在线测试API")
    @PostMapping("/test/online")
    public ResponseDTO<ApiTestResp> onlineTestRun(@RequestBody ApiInfoDTO apiInfoDTO) {
        return oneApiInfoBusiness.onlineTest(apiInfoDTO);
    }

    @ApiOperation("更新API状态")
    @GetMapping("/updateStatus")
    public ResponseDTO<Boolean> updateStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        return ResponseDTO.ok(oneApiInfoBusiness.updateStatus(id, status));
    }
}
