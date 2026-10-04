package com.lacus.admin.controller.lakeintelligence;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.domain.lakeintelligence.ClassifyBusiness;
import com.lacus.domain.lakeintelligence.dto.ClassifyResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图像分类推理接口
 *
 * @author shengyu
 * @date 2024/10/26 17:29
 */
@Api(value = "图像分类", tags = {"湖智-分类"})
@RestController
@RequestMapping("/api/lake-intelligence/classify")
public class ClassifyController {

    @Autowired
    private ClassifyBusiness classifyBusiness;

    @ApiOperation("图像分类推理（支持图片上传）")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseDTO<ClassifyResponse> classify(@RequestPart(value = "image", required = false) MultipartFile image,
                                                  @RequestParam(value = "model_id", required = false) String modelId) {
        return ResponseDTO.ok(classifyBusiness.classify(image, modelId));
    }
}
