package com.lacus.admin.controller.lakeintelligence;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.domain.lakeintelligence.ModelBusiness;
import com.lacus.domain.lakeintelligence.dto.ModelInfoDTO;
import com.lacus.domain.lakeintelligence.query.ModelPageQuery;
import com.lacus.common.core.page.PageDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 模型管理接口
 *
 * @author shengyu
 * @date 2024/10/26 17:29
 */
@Api(value = "模型管理", tags = {"湖智-模型"})
@RestController
@RequestMapping("/lake-intelligence/models")
public class ModelController {

    @Autowired
    private ModelBusiness modelBusiness;

    @ApiOperation("模型列表")
    @GetMapping
    public ResponseDTO<PageDTO> list(@Valid ModelPageQuery query) {
        return ResponseDTO.ok(modelBusiness.pageList(query));
    }

    @ApiOperation("创建模型")
    @PostMapping
    public ResponseDTO<ModelInfoDTO> create(@RequestBody Map<String, Object> payload) {
        String modelName = (String) payload.get("modelName");
        String description = (String) payload.get("description");
        return ResponseDTO.ok(modelBusiness.createModel(modelName, description));
    }

    @ApiOperation("更新模型")
    @PutMapping("/{id}")
    public ResponseDTO<ModelInfoDTO> update(@PathVariable("id") Long id, @RequestBody Map<String, Object> payload) {
        String modelName = (String) payload.get("modelName");
        String description = (String) payload.get("description");
        return ResponseDTO.ok(modelBusiness.updateModel(id, modelName, description));
    }

    @ApiOperation("下载模型")
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable("id") Long id) {
        File file = modelBusiness.downloadModel(id);
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }

    @ApiOperation("删除模型")
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteModel(@PathVariable("id") Long id) {
        modelBusiness.deleteModel(id);
        return ResponseDTO.ok();
    }

    @ApiOperation("获取可用模型架构列表")
    @GetMapping("/architectures")
    public ResponseDTO<List<Map<String, Object>>> listArchitectures(@RequestParam(required = false) String taskType) {
        return ResponseDTO.ok(modelBusiness.listArchitectures(taskType));
    }
}
