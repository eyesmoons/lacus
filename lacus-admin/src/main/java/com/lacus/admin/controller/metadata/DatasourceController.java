package com.lacus.admin.controller.metadata;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.core.annotations.AccessLog;
import com.lacus.domain.metadata.datasource.DatasourceBusiness;
import com.lacus.domain.metadata.datasource.command.AddMetaDatasourceCommand;
import com.lacus.domain.metadata.datasource.command.UpdateMetaDatasourceCommand;
import com.lacus.domain.metadata.datasource.dto.MetaDatasourceDTO;
import com.lacus.domain.metadata.datasource.model.MetaDatasourceModel;
import com.lacus.domain.metadata.datasource.query.DatasourceQuery;
import com.lacus.enums.DatasourceTypeEnum;
import com.lacus.enums.dictionary.BusinessTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import java.util.List;
import java.util.Map;

@Api(value = "数据源管理", tags = {"数据源定义"})
@RestController
@RequestMapping("/metadata/datasource")
public class DatasourceController {

    private final DatasourceBusiness datasourceBusiness;

    public DatasourceController(DatasourceBusiness datasourceBusiness) {
        this.datasourceBusiness = datasourceBusiness;
    }

    @ApiOperation("数据源列表")
    @PreAuthorize("@permission.has('metadata:datasource:list')")
    @GetMapping("/list")
    public ResponseDTO<List<MetaDatasourceModel>> list(
            @RequestParam(value = "datasourceName", required = false) String datasourceName,
            @RequestParam(value = "sourceType", required = false) String sourceType) {
        List<MetaDatasourceModel> list = datasourceBusiness.getDatasourceList(datasourceName, sourceType);
        return ResponseDTO.ok(list);
    }

    @ApiOperation("数据源列表")
    @PreAuthorize("@permission.has('metadata:datasource:list')")
    @GetMapping("/pageList")
    public ResponseDTO<PageDTO> pageList(DatasourceQuery query) {
        PageDTO page = datasourceBusiness.pageList(query);
        return ResponseDTO.ok(page);
    }

    @ApiOperation("新建数据源")
    @PreAuthorize("@permission.has('metadata:datasource:add')")
    @AccessLog(title = "数据源管理", businessType = BusinessTypeEnum.ADD)
    @PostMapping
    public ResponseDTO<?> add(@RequestBody @Validated AddMetaDatasourceCommand addCommand) {
        datasourceBusiness.addDatasource(addCommand);
        return ResponseDTO.ok();
    }

    @ApiOperation("修改数据源")
    @PreAuthorize("@permission.has('metadata:datasource:edit')")
    @AccessLog(title = "数据源管理", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping
    public ResponseDTO<?> edit(@Validated @RequestBody UpdateMetaDatasourceCommand updateCommand) {
        datasourceBusiness.updateDatasource(updateCommand);
        return ResponseDTO.ok();
    }

    @ApiOperation("删除数据源")
    @PreAuthorize("@permission.has('metadata:datasource:remove')")
    @AccessLog(title = "数据源管理", businessType = BusinessTypeEnum.DELETE)
    @DeleteMapping("/{datasourceIds}")
    public ResponseDTO<?> remove(@PathVariable @NotNull List<Long> datasourceIds) {
        datasourceBusiness.removeDatasource(datasourceIds);
        return ResponseDTO.ok();
    }

    @ApiOperation("数据源详情")
    @PreAuthorize("@permission.has('metadata:datasource:query')")
    @GetMapping(value = "/{datasourceId}")
    public ResponseDTO<MetaDatasourceDTO> getInfo(@PathVariable Long datasourceId) {
        MetaDatasourceDTO dto = datasourceBusiness.getDatasourceInfo(datasourceId);
        return ResponseDTO.ok(dto);
    }

    @ApiOperation("测试数据源")
    @GetMapping(value = "/test/{datasourceId}")
    public ResponseDTO<Boolean> testConnection(@PathVariable Long datasourceId) {
        Boolean result = datasourceBusiness.testConnection(datasourceId);
        return ResponseDTO.ok(result);
    }

    @ApiOperation("数据源类型下拉框")
    @GetMapping("/types")
    public ResponseDTO<List<Map<String, Object>>> datasourceTypeList() {
        List<Map<String, Object>> list = DatasourceTypeEnum.listAll();
        return ResponseDTO.ok(list);
    }

    @PreAuthorize("@permission.has('metadata:datasource:edit')")
    @AccessLog(title = "数据源管理", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping("/{datasourceId}/status")
    public ResponseDTO<Boolean> changeStatus(@PathVariable Long datasourceId, @RequestBody UpdateMetaDatasourceCommand command) {
        return ResponseDTO.ok(datasourceBusiness.changeStatus(datasourceId, command.getStatus()));
    }
}
