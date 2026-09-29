package com.lacus.admin.controller.monitor;

import com.lacus.common.core.base.BaseController;
import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.alert.plugin.spi.NotifyResult;
import com.lacus.core.annotations.AccessLog;
import com.lacus.domain.monitor.alert.AlertCenterBusiness;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertExecuteCommand;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertExecuteResultDTO;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertGroupDetailDTO;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertGroupQuery;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertGroupUpsertCommand;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertRecordDetailDTO;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertRecordQuery;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertSendLogDTO;
import com.lacus.domain.monitor.alert.AlertCenterModels.ChannelInstanceDetailDTO;
import com.lacus.domain.monitor.alert.AlertCenterModels.ChannelInstanceQuery;
import com.lacus.domain.monitor.alert.AlertCenterModels.ChannelInstanceTestCommand;
import com.lacus.domain.monitor.alert.AlertCenterModels.ChannelInstanceUpsertCommand;
import com.lacus.domain.monitor.alert.AlertCenterModels.ChannelTypeDTO;
import com.lacus.domain.monitor.alert.AlertCenterModels.OptionDTO;
import com.lacus.enums.dictionary.BusinessTypeEnum;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monitor/alert")
@Validated
public class AlertCenterController extends BaseController {

    @Autowired
    private AlertCenterBusiness alertCenterBusiness;

    @PreAuthorize("@permission.has('monitor:alertChannel:list')")
    @GetMapping("/channelType/list")
    public ResponseDTO<List<ChannelTypeDTO>> listChannelTypes() {
        return ResponseDTO.ok(alertCenterBusiness.listChannelTypes());
    }

    @PreAuthorize("@permission.has('monitor:alertChannel:list')")
    @GetMapping("/channelInstance/list")
    public ResponseDTO<PageDTO> listChannelInstances(ChannelInstanceQuery query) {
        return ResponseDTO.ok(alertCenterBusiness.listChannelInstances(query));
    }

    @PreAuthorize("@permission.has('monitor:alertChannel:query')")
    @GetMapping("/channelInstance/options")
    public ResponseDTO<List<OptionDTO>> listChannelInstanceOptions() {
        return ResponseDTO.ok(alertCenterBusiness.listChannelInstanceOptions());
    }

    @PreAuthorize("@permission.has('monitor:alertChannel:query')")
    @GetMapping("/channelInstance/{id}")
    public ResponseDTO<ChannelInstanceDetailDTO> getChannelInstance(@PathVariable @NotNull Long id) {
        return ResponseDTO.ok(alertCenterBusiness.getChannelInstance(id));
    }

    @PreAuthorize("@permission.has('monitor:alertChannel:add')")
    @AccessLog(title = "告警实例", businessType = BusinessTypeEnum.ADD)
    @PostMapping("/channelInstance")
    public ResponseDTO<?> addChannelInstance(@Valid @RequestBody ChannelInstanceUpsertCommand command) {
        alertCenterBusiness.addChannelInstance(command);
        return ResponseDTO.ok();
    }

    @PreAuthorize("@permission.has('monitor:alertChannel:edit')")
    @AccessLog(title = "告警实例", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping("/channelInstance")
    public ResponseDTO<?> updateChannelInstance(@Valid @RequestBody ChannelInstanceUpsertCommand command) {
        alertCenterBusiness.updateChannelInstance(command);
        return ResponseDTO.ok();
    }

    @PreAuthorize("@permission.has('monitor:alertChannel:remove')")
    @AccessLog(title = "告警实例", businessType = BusinessTypeEnum.DELETE)
    @DeleteMapping("/channelInstance/{ids}")
    public ResponseDTO<?> deleteChannelInstances(@PathVariable @NotEmpty List<Long> ids) {
        alertCenterBusiness.deleteChannelInstances(ids);
        return ResponseDTO.ok();
    }

    @PreAuthorize("@permission.has('monitor:alertChannel:test')")
    @AccessLog(title = "测试告警实例", businessType = BusinessTypeEnum.OTHER)
    @PostMapping("/channelInstance/test")
    public ResponseDTO<NotifyResult> testChannelInstance(@Valid @RequestBody ChannelInstanceTestCommand command) {
        return ResponseDTO.ok(alertCenterBusiness.testChannelInstance(command));
    }

    @PreAuthorize("@permission.has('monitor:alertGroup:list')")
    @GetMapping("/group/list")
    public ResponseDTO<PageDTO> listGroups(AlertGroupQuery query) {
        return ResponseDTO.ok(alertCenterBusiness.listGroups(query));
    }

    @PreAuthorize("@permission.has('monitor:alertGroup:query')")
    @GetMapping("/group/options")
    public ResponseDTO<List<OptionDTO>> listGroupOptions() {
        return ResponseDTO.ok(alertCenterBusiness.listGroupOptions());
    }

    @PreAuthorize("@permission.has('monitor:alertGroup:query')")
    @GetMapping("/group/{id}")
    public ResponseDTO<AlertGroupDetailDTO> getGroup(@PathVariable @NotNull Long id) {
        return ResponseDTO.ok(alertCenterBusiness.getGroup(id));
    }

    @PreAuthorize("@permission.has('monitor:alertGroup:add')")
    @AccessLog(title = "告警组", businessType = BusinessTypeEnum.ADD)
    @PostMapping("/group")
    public ResponseDTO<?> addGroup(@Valid @RequestBody AlertGroupUpsertCommand command) {
        alertCenterBusiness.addGroup(command);
        return ResponseDTO.ok();
    }

    @PreAuthorize("@permission.has('monitor:alertGroup:edit')")
    @AccessLog(title = "告警组", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping("/group")
    public ResponseDTO<?> updateGroup(@Valid @RequestBody AlertGroupUpsertCommand command) {
        alertCenterBusiness.updateGroup(command);
        return ResponseDTO.ok();
    }

    @PreAuthorize("@permission.has('monitor:alertGroup:remove')")
    @AccessLog(title = "告警组", businessType = BusinessTypeEnum.DELETE)
    @DeleteMapping("/group/{ids}")
    public ResponseDTO<?> deleteGroups(@PathVariable @NotEmpty List<Long> ids) {
        alertCenterBusiness.deleteGroups(ids);
        return ResponseDTO.ok();
    }

    @PreAuthorize("@permission.has('monitor:alertRecord:list')")
    @GetMapping("/record/list")
    public ResponseDTO<PageDTO> listRecords(AlertRecordQuery query) {
        return ResponseDTO.ok(alertCenterBusiness.listRecords(query));
    }

    @PreAuthorize("@permission.has('monitor:alertRecord:query')")
    @GetMapping("/record/{id}")
    public ResponseDTO<AlertRecordDetailDTO> getRecord(@PathVariable @NotNull Long id) {
        return ResponseDTO.ok(alertCenterBusiness.getRecordDetail(id));
    }

    @PreAuthorize("@permission.has('monitor:alertRecord:execute')")
    @AccessLog(title = "执行告警", businessType = BusinessTypeEnum.OTHER)
    @PostMapping("/record/execute")
    public ResponseDTO<AlertExecuteResultDTO> execute(@Valid @RequestBody AlertExecuteCommand command) {
        return ResponseDTO.ok(alertCenterBusiness.execute(command));
    }

    @PreAuthorize("@permission.has('monitor:alertRecord:retry')")
    @AccessLog(title = "重试告警", businessType = BusinessTypeEnum.OTHER)
    @PostMapping("/record/{id}/retry")
    public ResponseDTO<?> retry(@PathVariable @NotNull Long id) {
        alertCenterBusiness.retryRecord(id);
        return ResponseDTO.ok();
    }

    @PreAuthorize("@permission.has('monitor:alertRecord:query')")
    @GetMapping("/task/{taskId}/log/list")
    public ResponseDTO<List<AlertSendLogDTO>> listTaskLogs(@PathVariable @NotNull Long taskId) {
        return ResponseDTO.ok(alertCenterBusiness.listTaskLogs(taskId));
    }
}
