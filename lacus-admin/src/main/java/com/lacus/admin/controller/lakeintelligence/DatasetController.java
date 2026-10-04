package com.lacus.admin.controller.lakeintelligence;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.domain.lakeintelligence.DatasetBusiness;
import com.lacus.domain.lakeintelligence.command.CreateDatasetRequest;
import com.lacus.domain.lakeintelligence.dto.DatasetDTO;
import com.lacus.domain.lakeintelligence.query.DatasetPageQuery;
import com.lacus.common.core.page.PageDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

/**
 * 数据集管理接口
 *
 * @author shengyu
 * @date 2024/10/26 17:29
 */
@Api(value = "数据集管理", tags = {"湖智-数据集"})
@RestController
@RequestMapping("/lake-intelligence/datasets")
public class DatasetController {

    @Autowired
    private DatasetBusiness datasetBusiness;

    @ApiOperation("数据集列表")
    @GetMapping
    public ResponseDTO<PageDTO> list(@Valid DatasetPageQuery query) {
        return ResponseDTO.ok(datasetBusiness.pageList(query));
    }

    @ApiOperation("创建数据集")
    @PostMapping
    public ResponseDTO<DatasetDTO> createDataset(@RequestBody @Valid CreateDatasetRequest request) {
        return ResponseDTO.ok(datasetBusiness.createDataset(request));
    }

    @ApiOperation("数据源探测")
    @PostMapping("/probe-source")
    public ResponseDTO<Map<String, Object>> probeSource(@RequestParam("uri") String uri) {
        return ResponseDTO.ok(datasetBusiness.probeSource(uri));
    }

    @ApiOperation("预览数据集")
    @GetMapping("/{id}/preview")
    public ResponseDTO<DatasetDTO> preview(@PathVariable("id") Long id) {
        return ResponseDTO.ok(datasetBusiness.detail(id));
    }

    @ApiOperation("删除数据集")
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteDataset(@PathVariable("id") Long id) {
        datasetBusiness.deleteDataset(id);
        return ResponseDTO.ok();
    }

    @ApiOperation("获取数据集类别分布统计")
    @GetMapping("/{id}/class-stats")
    public ResponseDTO<Map<String, Object>> getClassStats(@PathVariable("id") Long id) {
        return ResponseDTO.ok(datasetBusiness.getClassStats(id));
    }
}
