package com.lacus.admin.controller.lakeintelligence;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.domain.lakeintelligence.TrainBusiness;
import com.lacus.domain.lakeintelligence.command.TrainRequest;
import com.lacus.domain.lakeintelligence.dto.ProgressResponse;
import com.lacus.domain.lakeintelligence.dto.TaskDTO;
import com.lacus.domain.lakeintelligence.query.TaskPageQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 训练任务管理接口
 *
 * @author shengyu
 * @date 2024/10/26 17:29
 */
@Api(value = "训练任务管理", tags = {"湖智-训练"})
@RestController
@RequestMapping("/lake-intelligence/tasks")
public class TrainController {

    @Autowired
    private TrainBusiness trainBusiness;

    @ApiOperation("任务列表")
    @GetMapping
    public ResponseDTO<PageDTO> list(@Valid TaskPageQuery query) {
        return ResponseDTO.ok(trainBusiness.pageList(query));
    }

    @ApiOperation("启动训练")
    @PostMapping
    public ResponseDTO<TaskDTO> startTraining(@RequestBody @Valid TrainRequest request) {
        return ResponseDTO.ok(trainBusiness.startTraining(request));
    }

    @ApiOperation("查询训练进度")
    @GetMapping("/{id}/progress")
    public ResponseDTO<ProgressResponse> getProgress(@PathVariable("id") Long id) {
        return ResponseDTO.ok(trainBusiness.getProgress(id));
    }

    @ApiOperation("取消训练")
    @PostMapping("/{id}/cancel")
    public ResponseDTO<?> cancelTraining(@PathVariable("id") Long id) {
        trainBusiness.cancelTraining(id);
        return ResponseDTO.ok();
    }

    @ApiOperation("获取任务超参配置 Schema")
    @GetMapping("/hyperparam-schema")
    public ResponseDTO<Map<String, Object>> getHyperparamSchema(@RequestParam("task_type") String taskType) {
        return ResponseDTO.ok(trainBusiness.getHyperparamSchema(taskType));
    }
}
