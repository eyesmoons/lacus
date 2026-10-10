package com.lacus.admin.controller.lakeintelligence;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.domain.lakeintelligence.ClassifyBusiness;
import com.lacus.domain.lakeintelligence.dto.ClassifyResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Api(value = "图像分类", tags = {"湖智-分类"})
@RestController
@RequestMapping("/lake-intelligence/classify")
public class ClassifyController {

    @Autowired
    private ClassifyBusiness classifyBusiness;

    @ApiOperation("图像分类推理（支持图片上传）")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseDTO<ClassifyResponse> classify(@RequestPart(value = "image", required = false) MultipartFile image,
                                                  @RequestParam(value = "task_id", required = false) Long taskId) {
        return ResponseDTO.ok(classifyBusiness.classify(image, taskId));
    }

    @ApiOperation("批量图像分类推理")
    @PostMapping(value = "/batch", consumes = "multipart/form-data")
    public ResponseDTO<List<ClassifyResponse>> batchClassify(@RequestPart("images") List<MultipartFile> images,
                                                             @RequestParam(value = "task_id", required = false) Long taskId) {
        return ResponseDTO.ok(classifyBusiness.batchClassify(images, taskId));
    }

    @ApiOperation("获取分类类别列表")
    @GetMapping("/classes")
    public ResponseDTO<List<Map<String, Object>>> listClasses(@RequestParam(value = "model_id", required = false) String modelId) {
        return ResponseDTO.ok(classifyBusiness.listClasses(modelId));
    }
}
