package com.lacus.admin.controller.lakeintelligence;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.domain.lakeintelligence.TaskBusiness;
import com.lacus.domain.lakeintelligence.dto.TaskDTO;
import com.lacus.domain.lakeintelligence.query.TaskPageQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(value = "任务管理", tags = {"湖智-任务"})
@RestController
@RequestMapping("/api/lake-intelligence/tasks")
public class TaskController {

    @Autowired
    private TaskBusiness taskBusiness;

    @ApiOperation("任务列表")
    @GetMapping
    public ResponseDTO<PageDTO> list(@Valid TaskPageQuery query) {
        return ResponseDTO.ok(taskBusiness.pageList(query));
    }

    @ApiOperation("任务详情")
    @GetMapping("/{id}")
    public ResponseDTO<TaskDTO> detail(@PathVariable("id") Long id) {
        return ResponseDTO.ok(taskBusiness.detail(id));
    }

    @ApiOperation("训练进度")
    @GetMapping("/{id}/progress")
    public ResponseDTO<?> getProgress(@PathVariable("id") Long id) {
        return ResponseDTO.ok(taskBusiness.getProgress(id));
    }
}
