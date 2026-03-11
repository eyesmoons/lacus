package com.lacus.admin.controller.dig;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.domain.dig.StJobBusiness;
import com.lacus.domain.dig.command.AddStJobCommand;
import com.lacus.domain.dig.command.UpdateStJobCommand;
import com.lacus.domain.dig.model.StJobModel;
import com.lacus.domain.dig.query.StJobQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(value = "数据集成任务定义", tags = {"数据集成任务定义管理"})
@RestController
@RequestMapping("/st/job")
public class StJobDefineController {

    @Autowired
    private StJobBusiness stJobBusiness;

    @ApiOperation("查询任务定义列表")
    @GetMapping("/list")
    public ResponseDTO<?> listJobs(StJobQuery query) {
        PageDTO page = stJobBusiness.pageList(query);
        return ResponseDTO.ok(page);
    }

    @ApiOperation("新增任务定义")
    @PostMapping
    public ResponseDTO<StJobModel> addJob(@RequestBody AddStJobCommand command) {
        return ResponseDTO.ok(stJobBusiness.addJob(command));
    }

    @ApiOperation("修改任务定义")
    @PutMapping
    public ResponseDTO<Void> updateJob(@RequestBody @Valid UpdateStJobCommand command) {
        stJobBusiness.updateJob(command);
        return ResponseDTO.ok();
    }

    @ApiOperation("发布任务")
    @PostMapping("/publish/{jobId}")
    public ResponseDTO<Void> publishJob(@PathVariable("jobId") Long jobId) {
        stJobBusiness.publishJob(jobId, 1);
        return ResponseDTO.ok();
    }

    @ApiOperation("取消发布任务")
    @PostMapping("/unpublish/{jobId}")
    public ResponseDTO<Void> unPublishJob(@PathVariable("jobId") Long jobId) {
        stJobBusiness.publishJob(jobId, 0);
        return ResponseDTO.ok();
    }

    @ApiOperation("删除任务定义")
    @DeleteMapping("/{jobId}")
    public ResponseDTO<Void> deleteJob(@PathVariable Long jobId) {
        stJobBusiness.deleteJob(jobId);
        return ResponseDTO.ok();
    }

    @ApiOperation("任务详情")
    @GetMapping("/{jobId}")
    public ResponseDTO<?> detail(@PathVariable Long jobId) {
        return ResponseDTO.ok(stJobBusiness.detail(jobId));
    }

    @ApiOperation("获取任务配置")
    @GetMapping("/config/{jobId}")
    public ResponseDTO<String> getJobHocon(@PathVariable("jobId") Long jobId) {
        String hocon = stJobBusiness.getJobHocon(jobId);
        // 对 password 进行脱敏处理
        if (hocon != null) {
            hocon = hocon.replaceAll("password\\s*=\\s*(\"[^\"]*\"|[^\\s,{}]+)", "password = \"******\"");
        }
        return ResponseDTO.ok(hocon);
    }
}
