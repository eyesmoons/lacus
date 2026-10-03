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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.File;

/**
 * 模型管理接口
 *
 * @author shengyu
 * @date 2024/10/26 17:29
 */
@Api(value = "模型管理", tags = {"湖智-模型"})
@RestController
@RequestMapping("/api/lake-intelligence/models")
public class ModelController {

    @Autowired
    private ModelBusiness modelBusiness;

    @ApiOperation("模型列表")
    @GetMapping
    @PreAuthorize("@permission.has('lakeintelligence:model:list')")
    public ResponseDTO<PageDTO> list(@Valid ModelPageQuery query) {
        return ResponseDTO.ok(modelBusiness.pageList(query));
    }

    @ApiOperation("下载模型")
    @GetMapping("/{id}/download")
    @PreAuthorize("@permission.has('lakeintelligence:model:download')")
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
    @PreAuthorize("@permission.has('lakeintelligence:model:delete')")
    public ResponseDTO<?> deleteModel(@PathVariable("id") Long id) {
        modelBusiness.deleteModel(id);
        return ResponseDTO.ok();
    }
}
