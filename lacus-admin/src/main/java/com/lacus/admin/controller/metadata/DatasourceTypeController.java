package com.lacus.admin.controller.metadata;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.core.annotations.AccessLog;
import com.lacus.domain.metadata.datasourceType.DatasourceTypeBusiness;
import com.lacus.domain.metadata.datasourceType.command.AddMetaDatasourceTypeCommand;
import com.lacus.domain.metadata.datasourceType.command.UpdateMetaDatasourceTypeCommand;
import com.lacus.domain.metadata.datasourceType.dto.DatasourceTypeDTO;
import com.lacus.domain.metadata.datasourceType.model.MetaDatasourceTypeModel;
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
import java.util.List;

@Api(value = "数据源管理", tags = {"数据源类型"})
@RestController
@RequestMapping("/metadata/datasource/type")
public class DatasourceTypeController {

    @Autowired
    private DatasourceTypeBusiness datasourceTypeBusiness;

    @ApiOperation("数据源类型列表")
    @PreAuthorize("@permission.has('metadata:datasourceType:list')")
    @GetMapping("/list")
    public ResponseDTO<List<MetaDatasourceTypeModel>> list(@RequestParam(value = "typeName", required = false) String typeName) {
        List<MetaDatasourceTypeModel> list = datasourceTypeBusiness.list(typeName);
        return ResponseDTO.ok(list);
    }

    @ApiOperation("新建数据源类型")
    @PreAuthorize("@permission.has('metadata:datasourceType:add')")
    @AccessLog(title = "数据源类型管理", businessType = BusinessTypeEnum.ADD)
    @PostMapping
    public ResponseDTO<?> add(@RequestBody @Validated AddMetaDatasourceTypeCommand addCommand) {
        datasourceTypeBusiness.add(addCommand);
        return ResponseDTO.ok();
    }

    @ApiOperation("修改数据源类型")
    @PreAuthorize("@permission.has('metadata:datasourceType:edit')")
    @AccessLog(title = "数据源类型管理", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping
    public ResponseDTO<?> edit(@Validated @RequestBody UpdateMetaDatasourceTypeCommand updateCommand) {
        datasourceTypeBusiness.update(updateCommand);
        return ResponseDTO.ok();
    }

    @ApiOperation("删除数据源类型")
    @PreAuthorize("@permission.has('metadata:datasourceType:remove')")
    @AccessLog(title = "数据源类型管理", businessType = BusinessTypeEnum.DELETE)
    @DeleteMapping("/{datasourceTypeIds}")
    public ResponseDTO<?> remove(@PathVariable @NotNull List<Long> datasourceTypeIds) {
        datasourceTypeBusiness.remove(datasourceTypeIds);
        return ResponseDTO.ok();
    }

    @ApiOperation("数据源类型详情")
    @PreAuthorize("@permission.has('metadata:datasourceType:query')")
    @GetMapping(value = "/{typeId}")
    public ResponseDTO<DatasourceTypeDTO> detail(@PathVariable Long typeId) {
        DatasourceTypeDTO dto = datasourceTypeBusiness.getDatasourceTypeInfo(typeId);
        return ResponseDTO.ok(dto);
    }
}
