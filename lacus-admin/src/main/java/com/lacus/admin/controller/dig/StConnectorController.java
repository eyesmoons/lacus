package com.lacus.admin.controller.dig;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.domain.dig.StComponentConnectorBusiness;
import com.lacus.domain.dig.resp.StConnectorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.seatunnel.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(value = "数据集成连接器管理", tags = {"数据集成连接器管理"})
@RestController
@RequestMapping("/st/connector")
public class StConnectorController {

    @Autowired
    private StComponentConnectorBusiness stComponentConnectorBusiness;

    @ApiOperation("查询source组件")
    @GetMapping("/sources")
    public ResponseDTO<List<StConnectorInfo>> listSources() {
        return ResponseDTO.ok(stComponentConnectorBusiness.listSources());
    }

    @ApiOperation("查询transform组件")
    @GetMapping("/transforms")
    public ResponseDTO<List<StConnectorInfo>> listTransforms() {
        return ResponseDTO.ok(stComponentConnectorBusiness.listTransforms());
    }

    @ApiOperation("查询sink组件")
    @GetMapping("/sinks")
    public ResponseDTO<List<StConnectorInfo>> listSinks() {
        return ResponseDTO.ok(stComponentConnectorBusiness.listSinks());
    }

    @ApiOperation("根据组件名称查询动态表单配置")
    @GetMapping("/form")
    public ResponseDTO<String> getConnectorFormStructure(
            @ApiParam(value = "connector type", required = true) @RequestParam String connectorType,
            @ApiParam(value = "connector name", required = true) @RequestParam
            String connectorName) {
        Map<String, Object> formStructure = stComponentConnectorBusiness.getConnectorFormStructure(connectorType, connectorName);
        return ResponseDTO.ok(JsonUtils.toJsonString(formStructure));
    }
}
