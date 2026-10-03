package com.lacus.admin.controller.lakeintelligence;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 湖智模块页面路由（Thymeleaf）
 *
 * @author shengyu
 * @date 2024/10/26 17:29
 */
@Api(value = "湖智页面", tags = {"湖智-页面"})
@Controller
@RequestMapping("/lake-intelligence")
public class PageController {

    @ApiOperation("数据集上传页")
    @GetMapping("/dataset/upload")
    @PreAuthorize("@permission.has('lakeintelligence:page:dataset-upload')")
    public String datasetUploadPage() {
        return "lakeintelligence/dataset-upload";
    }

    @ApiOperation("数据集预览页")
    @GetMapping("/dataset/{id}/preview")
    @PreAuthorize("@permission.has('lakeintelligence:page:dataset-preview')")
    public String datasetPreviewPage(@PathVariable("id") Long id) {
        return "lakeintelligence/dataset-preview";
    }

    @ApiOperation("训练配置页")
    @GetMapping("/training/new")
    @PreAuthorize("@permission.has('lakeintelligence:page:training-new')")
    public String trainingNewPage() {
        return "lakeintelligence/training-new";
    }

    @ApiOperation("训练进度页")
    @GetMapping("/training/{id}")
    @PreAuthorize("@permission.has('lakeintelligence:page:training-progress')")
    public String trainingProgressPage(@PathVariable("id") Long id) {
        return "lakeintelligence/training-progress";
    }

    @ApiOperation("向量构建页")
    @GetMapping("/vector-build")
    @PreAuthorize("@permission.has('lakeintelligence:page:vector-build')")
    public String vectorBuildPage() {
        return "lakeintelligence/vector-build";
    }

    @ApiOperation("相似检索页")
    @GetMapping("/search")
    @PreAuthorize("@permission.has('lakeintelligence:page:search')")
    public String searchPage() {
        return "lakeintelligence/search";
    }

    @ApiOperation("模型管理页")
    @GetMapping("/models")
    @PreAuthorize("@permission.has('lakeintelligence:page:models')")
    public String modelsPage() {
        return "lakeintelligence/models";
    }
}
