package com.lacus.admin.controller.dig;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.domain.dig.StConnectorBusiness;
import com.lacus.domain.dig.resp.ConnectorInfo;
import com.lacus.enums.StConnectorStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.seatunnel.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Api(value = "数据集成连接器管理", tags = {"数据集成连接器管理"})
@RestController
@RequestMapping("/st/connector")
public class StConnectorController {

    @Autowired
    private StConnectorBusiness stConnectorBusiness;

    @ApiOperation("查询source组件")
    @GetMapping("/sources")
    public ResponseDTO<List<ConnectorInfo>> listSources(@RequestParam(defaultValue = "ALL") StConnectorStatus status) throws IOException {
        return ResponseDTO.ok(stConnectorBusiness.listSources(status));
    }

    @ApiOperation("查询transform组件")
    @GetMapping("/transforms")
    public ResponseDTO<List<ConnectorInfo>> listTransforms() throws IOException {
        return ResponseDTO.ok(stConnectorBusiness.listTransforms());
    }

    @ApiOperation("查询sink组件")
    @GetMapping("/sinks")
    public ResponseDTO<List<ConnectorInfo>> listSinks(@RequestParam(defaultValue = "ALL") StConnectorStatus status) throws IOException {
        return ResponseDTO.ok(stConnectorBusiness.listSinks(status));
    }

    @ApiOperation("根据组件名称查询动态表单配置")
    @GetMapping("/form")
    public ResponseDTO<String> getConnectorFormStructure(
            @ApiParam(value = "connector type", required = true) @RequestParam String connectorType,
            @ApiParam(value = "connector name", required = true) @RequestParam
            String connectorName) {
        return ResponseDTO.ok(
                JsonUtils.toJsonString(
                        stConnectorBusiness.getConnectorFormStructure(connectorType, connectorName)));
    }
}
