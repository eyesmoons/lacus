package com.lacus.admin.controller.dataquality;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.domain.dataquality.DqScheduleBusiness;
import com.lacus.domain.dataquality.command.DqScheduleCommand;
import com.lacus.domain.dataquality.query.DqScheduleQuery;
import com.lacus.domain.dataquality.vo.DqRuleOptionVO;
import com.lacus.domain.dataquality.vo.DqScheduleVO;
import com.lacus.domain.monitor.alert.AlertCenterBusiness;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertGroupDTO;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertGroupQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.List;
import java.util.Map;

@Api(value = "数据质量调度管理", tags = {"数据质量调度管理"})
@RestController
@RequestMapping("/dq/schedule")
public class DqScheduleController {

    @Autowired
    private DqScheduleBusiness dqScheduleBusiness;

    @Autowired
    private AlertCenterBusiness alertCenterBusiness;

    @ApiOperation("调度列表")
    @PreAuthorize("@permission.has('dq:schedule:list')")
    @GetMapping("/list")
    public ResponseDTO<?> list(DqScheduleQuery query) {
        return ResponseDTO.ok(dqScheduleBusiness.list(query));
    }

    @ApiOperation("调度详情")
    @PreAuthorize("@permission.has('dq:schedule:edit')")
    @GetMapping("/{jobId}")
    public ResponseDTO<DqScheduleVO> detail(@PathVariable Long jobId) {
        return ResponseDTO.ok(dqScheduleBusiness.detail(jobId));
    }

    @ApiOperation("新增调度")
    @PreAuthorize("@permission.has('dq:schedule:add')")
    @PostMapping
    public ResponseDTO<Long> add(@RequestBody @Valid DqScheduleCommand command) throws SchedulerException {
        return ResponseDTO.ok(dqScheduleBusiness.add(command));
    }

    @ApiOperation("编辑调度")
    @PreAuthorize("@permission.has('dq:schedule:edit')")
    @PutMapping
    public ResponseDTO<Void> edit(@RequestBody @Valid DqScheduleCommand command) throws SchedulerException {
        dqScheduleBusiness.edit(command);
        return ResponseDTO.ok();
    }

    @ApiOperation("暂停")
    @PreAuthorize("@permission.has('dq:schedule:pause')")
    @PostMapping("/pause")
    public ResponseDTO<Void> pause(@RequestBody Map<String, Long> body) throws SchedulerException {
        dqScheduleBusiness.pause(body.get("jobId"));
        return ResponseDTO.ok();
    }

    @ApiOperation("恢复")
    @PreAuthorize("@permission.has('dq:schedule:resume')")
    @PostMapping("/resume")
    public ResponseDTO<Void> resume(@RequestBody Map<String, Long> body) throws SchedulerException {
        dqScheduleBusiness.resume(body.get("jobId"));
        return ResponseDTO.ok();
    }

    @ApiOperation("立即执行")
    @PreAuthorize("@permission.has('dq:schedule:run')")
    @PostMapping("/run")
    public ResponseDTO<Void> run(@RequestBody Map<String, Long> body) throws SchedulerException {
        dqScheduleBusiness.run(body.get("jobId"));
        return ResponseDTO.ok();
    }

    @ApiOperation("删除")
    @PreAuthorize("@permission.has('dq:schedule:delete')")
    @DeleteMapping("/{jobId}")
    public ResponseDTO<Void> delete(@PathVariable Long jobId) throws SchedulerException {
        dqScheduleBusiness.delete(jobId);
        return ResponseDTO.ok();
    }

    @ApiOperation("可选规则列表（未被绑定）")
    @PreAuthorize("@permission.has('dq:schedule:add')")
    @GetMapping("/rules")
    public ResponseDTO<List<DqRuleOptionVO>> optionalRules() {
        return ResponseDTO.ok(dqScheduleBusiness.optionalRules());
    }

    @ApiOperation("cron表达式校验")
    @PreAuthorize("@permission.has('dq:schedule:add')")
    @GetMapping("/cron-validate")
    public ResponseDTO<Boolean> validateCron(@RequestParam String cron) {
        return ResponseDTO.ok(dqScheduleBusiness.validateCron(cron));
    }

    @ApiOperation("告警组选项列表（供规则表单选择器）")
    @PreAuthorize("@permission.has('dq:schedule:add')")
    @GetMapping("/alert-groups")
    @SuppressWarnings("unchecked")
    public ResponseDTO<List<AlertGroupDTO>> alertGroupOptions() {
        AlertGroupQuery query = new AlertGroupQuery();
        query.setEnabled(true);
        List<AlertGroupDTO> rows = (List<AlertGroupDTO>) (List<?>) alertCenterBusiness.listGroups(query).getRows();
        return ResponseDTO.ok(rows);
    }
}
