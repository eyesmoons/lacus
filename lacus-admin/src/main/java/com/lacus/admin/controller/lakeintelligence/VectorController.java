package com.lacus.admin.controller.lakeintelligence;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.domain.lakeintelligence.VectorIndexBusiness;
import com.lacus.domain.lakeintelligence.command.BuildVectorRequest;
import com.lacus.domain.lakeintelligence.dto.ProgressResponse;
import com.lacus.domain.lakeintelligence.dto.VectorIndexDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 向量库管理接口
 *
 * @author shengyu
 * @date 2024/10/26 17:29
 */
@Api(value = "向量库管理", tags = {"湖智-向量库"})
@RestController
@RequestMapping("/api/lake-intelligence/vectors")
public class VectorController {

    @Autowired
    private VectorIndexBusiness vectorIndexBusiness;

    @ApiOperation("构建向量库")
    @PostMapping("/build")
    public ResponseDTO<VectorIndexDTO> buildVectors(@RequestBody @Valid BuildVectorRequest request) {
        return ResponseDTO.ok(vectorIndexBusiness.buildVectors(request));
    }

    @ApiOperation("查询构建进度")
    @GetMapping("/{id}/progress")
    public ResponseDTO<ProgressResponse> getBuildProgress(@PathVariable("id") Long id) {
        return ResponseDTO.ok(vectorIndexBusiness.getBuildProgress(id));
    }
}
