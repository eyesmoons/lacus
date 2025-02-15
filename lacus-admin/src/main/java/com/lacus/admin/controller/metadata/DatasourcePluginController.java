package com.lacus.admin.controller.metadata;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.dao.dto.MetaDatasourcePluginDTO;
import com.lacus.dao.entity.MetaDatasourcePlugin;
import com.lacus.service.metadata.IMetaDatasourcePluginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "数据源插件接口")
@RestController
@RequestMapping("/metadata/datasource/plugin")
public class DatasourcePluginController {

    @Autowired
    private IMetaDatasourcePluginService pluginService;

    @ApiOperation("分页查询数据源插件")
    @GetMapping("/pageList")
    public ResponseDTO<PageDTO> pagePlugins(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("插件名称") @RequestParam(required = false) String name,
            @ApiParam("插件类型") @RequestParam(required = false) Integer type) {

        Page<MetaDatasourcePlugin> page = new Page<>(pageNum, pageSize);
        Page<MetaDatasourcePlugin> pageResult = pluginService.pagePlugins(page, name, type);
        List<MetaDatasourcePluginDTO> records = pageResult.getRecords().stream()
                .map(MetaDatasourcePluginDTO::from)
                .collect(Collectors.toList());
        PageDTO pageDTO = new PageDTO(records, pageResult.getTotal());
        return ResponseDTO.ok(pageDTO);
    }

    @ApiOperation("查询所有数据源插件")
    @GetMapping("/list")
    public ResponseDTO<List<MetaDatasourcePluginDTO>> listPlugins(
            @ApiParam("插件名称") @RequestParam(required = false) String name,
            @ApiParam("插件类型") @RequestParam(required = false) Integer type) {
        List<MetaDatasourcePlugin> plugins = pluginService.listPlugins(name, type);
        List<MetaDatasourcePluginDTO> dtoList = plugins.stream()
                .map(MetaDatasourcePluginDTO::from)
                .collect(Collectors.toList());
        return ResponseDTO.ok(dtoList);
    }

    @ApiOperation("根据名称查询数据源插件")
    @GetMapping("/{name}")
    public ResponseDTO<MetaDatasourcePluginDTO> getPluginByName(
            @ApiParam("插件名称") @PathVariable String name) {

        MetaDatasourcePlugin plugin = pluginService.getPluginByName(name);
        return plugin != null ? ResponseDTO.ok(MetaDatasourcePluginDTO.from(plugin)) : ResponseDTO.ok();
    }
}
