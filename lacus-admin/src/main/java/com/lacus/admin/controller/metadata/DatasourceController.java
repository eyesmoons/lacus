package com.lacus.admin.controller.metadata;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.CustomException;
import com.lacus.core.annotations.AccessLog;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.dao.quartz.entity.SysJob;
import com.lacus.dao.quartz.entity.SysJobLog;
import com.lacus.domain.metadata.datasource.DatasourceBusiness;
import com.lacus.domain.metadata.datasource.command.AddMetaDatasourceCommand;
import com.lacus.domain.metadata.datasource.command.CreateSyncJobCommand;
import com.lacus.domain.metadata.datasource.command.UpdateMetaDatasourceCommand;
import com.lacus.domain.metadata.datasource.command.UpdateSyncJobCommand;
import com.lacus.domain.metadata.datasource.dto.MetaDatasourceDTO;
import com.lacus.domain.metadata.datasource.model.MetaDatasourceModel;
import com.lacus.domain.metadata.datasource.query.DatasourceQuery;
import com.lacus.enums.DatasourceTypeEnum;
import com.lacus.enums.dictionary.BusinessTypeEnum;
import com.lacus.service.metadata.IMetaDataSourceService;
import com.lacus.service.quartz.ISysJobLogService;
import com.lacus.service.quartz.ISysJobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.quartz.SchedulerException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Api(value = "数据源管理", tags = {"数据源定义"})
@RestController
@RequestMapping("/metadata/datasource")
public class DatasourceController {

    private final DatasourceBusiness datasourceBusiness;

    @Autowired
    private ISysJobService sysJobService;

    @Autowired
    private ISysJobLogService sysJobLogService;

    @Autowired
    private IMetaDataSourceService metaDataSourceService;

    public DatasourceController(DatasourceBusiness datasourceBusiness) {
        this.datasourceBusiness = datasourceBusiness;
    }

    @ApiOperation("数据源列表")
    @PreAuthorize("@permission.has('metadata:datasource:list')")
    @GetMapping("/list")
    public ResponseDTO<List<MetaDatasourceModel>> list(
            @RequestParam(value = "datasourceName", required = false) String datasourceName,
            @RequestParam(value = "sourceType", required = false) String sourceType) {
        List<MetaDatasourceModel> list = datasourceBusiness.getDatasourceList(datasourceName, sourceType);
        return ResponseDTO.ok(list);
    }

    @ApiOperation("数据源列表")
    @PreAuthorize("@permission.has('metadata:datasource:list')")
    @GetMapping("/pageList")
    public ResponseDTO<PageDTO> pageList(DatasourceQuery query) {
        PageDTO page = datasourceBusiness.pageList(query);
        return ResponseDTO.ok(page);
    }

    @ApiOperation("新建数据源")
    @PreAuthorize("@permission.has('metadata:datasource:add')")
    @AccessLog(title = "数据源管理", businessType = BusinessTypeEnum.ADD)
    @PostMapping
    public ResponseDTO<?> add(@RequestBody @Validated AddMetaDatasourceCommand addCommand) {
        datasourceBusiness.addDatasource(addCommand);
        return ResponseDTO.ok();
    }

    @ApiOperation("修改数据源")
    @PreAuthorize("@permission.has('metadata:datasource:edit')")
    @AccessLog(title = "数据源管理", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping
    public ResponseDTO<?> edit(@Validated @RequestBody UpdateMetaDatasourceCommand updateCommand) {
        datasourceBusiness.updateDatasource(updateCommand);
        return ResponseDTO.ok();
    }

    @ApiOperation("删除数据源")
    @PreAuthorize("@permission.has('metadata:datasource:remove')")
    @AccessLog(title = "数据源管理", businessType = BusinessTypeEnum.DELETE)
    @DeleteMapping("/{datasourceIds}")
    public ResponseDTO<?> remove(@PathVariable @NotNull List<Long> datasourceIds) {
        datasourceBusiness.removeDatasource(datasourceIds);
        return ResponseDTO.ok();
    }

    @ApiOperation("数据源详情")
    @PreAuthorize("@permission.has('metadata:datasource:query')")
    @GetMapping(value = "/{datasourceId}")
    public ResponseDTO<MetaDatasourceDTO> getInfo(@PathVariable Long datasourceId) {
        MetaDatasourceDTO dto = datasourceBusiness.getDatasourceInfo(datasourceId);
        return ResponseDTO.ok(dto);
    }

    @ApiOperation("测试数据源")
    @GetMapping(value = "/test/{datasourceId}")
    public ResponseDTO<Boolean> testConnection(@PathVariable Long datasourceId) {
        Boolean result = datasourceBusiness.testConnection(datasourceId);
        return ResponseDTO.ok(result);
    }

    @ApiOperation("数据源类型下拉框")
    @GetMapping("/types")
    public ResponseDTO<List<Map<String, Object>>> datasourceTypeList() {
        List<Map<String, Object>> list = DatasourceTypeEnum.listAll();
        return ResponseDTO.ok(list);
    }

    @PreAuthorize("@permission.has('metadata:datasource:edit')")
    @AccessLog(title = "数据源管理", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping("/{datasourceId}/status")
    public ResponseDTO<Boolean> changeStatus(@PathVariable Long datasourceId, @RequestBody UpdateMetaDatasourceCommand command) {
        return ResponseDTO.ok(datasourceBusiness.changeStatus(datasourceId, command.getStatus()));
    }

    @ApiOperation("为数据源创建定时同步任务")
    @PreAuthorize("@permission.has('metadata:datasource:edit')")
    @AccessLog(title = "数据源管理", businessType = BusinessTypeEnum.ADD)
    @PostMapping("/{datasourceId}/syncJob")
    public ResponseDTO<?> createSyncJob(@PathVariable Long datasourceId, @RequestBody @Validated CreateSyncJobCommand command) {
        try {
            // 检查数据源是否存在
            MetaDatasourceEntity datasource = metaDataSourceService.getById(datasourceId);
            if (datasource == null) {
                throw new CustomException("数据源不存在");
            }

            // 检查是否已存在定时任务
            String invokeTarget = "metadataSyncJob.syncDatasource(" + datasourceId + "L)";
            SysJob query = new SysJob();
            query.setJobGroup("METADATA_SYNC");
            query.setInvokeTarget(invokeTarget);
            List<SysJob> existingJobs = sysJobService.selectJobList(query);
            if (!existingJobs.isEmpty()) {
                throw new CustomException("该数据源已配置定时同步任务");
            }

            // 创建定时任务
            SysJob job = new SysJob();
            job.setJobName("数据源元数据同步-" + datasource.getDatasourceName());
            job.setJobGroup("METADATA_SYNC");
            job.setInvokeTarget(invokeTarget);
            job.setCronExpression(command.getCronExpression());
            job.setConcurrent(command.getConcurrent());
            job.setMisfirePolicy(command.getMisfirePolicy());
            job.setStatus(command.getStatus());
            job.setRemark(command.getRemark());
            job.setCreateTime(new Date());

            sysJobService.insertJob(job);
            return ResponseDTO.ok();
        } catch (SchedulerException e) {
            throw new CustomException("创建定时任务失败：" + e.getMessage());
        }
    }

    @ApiOperation("查询数据源的定时同步任务")
    @PreAuthorize("@permission.has('metadata:datasource:query')")
    @GetMapping("/{datasourceId}/syncJob")
    public ResponseDTO<SysJob> getSyncJobInfo(@PathVariable Long datasourceId) {
        String invokeTarget = "metadataSyncJob.syncDatasource(" + datasourceId + "L)";
        SysJob query = new SysJob();
        query.setJobGroup("METADATA_SYNC");
        query.setInvokeTarget(invokeTarget);
        List<SysJob> jobs = sysJobService.selectJobList(query);
        if (jobs.isEmpty()) {
            return ResponseDTO.ok(null);
        }
        return ResponseDTO.ok(jobs.get(0));
    }

    @ApiOperation("更新数据源的定时同步任务")
    @PreAuthorize("@permission.has('metadata:datasource:edit')")
    @AccessLog(title = "数据源管理", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping("/{datasourceId}/syncJob")
    public ResponseDTO<?> updateSyncJob(@PathVariable Long datasourceId, @RequestBody @Validated UpdateSyncJobCommand command) {
        try {
            SysJob job = sysJobService.selectJobById(command.getJobId());
            if (job == null) {
                throw new CustomException("定时任务不存在");
            }

            // 验证任务是否属于该数据源
            String expectedInvokeTarget = "metadataSyncJob.syncDatasource(" + datasourceId + "L)";
            if (!expectedInvokeTarget.equals(job.getInvokeTarget())) {
                throw new CustomException("定时任务不属于该数据源");
            }

            job.setCronExpression(command.getCronExpression());
            job.setConcurrent(command.getConcurrent());
            job.setMisfirePolicy(command.getMisfirePolicy());
            job.setStatus(command.getStatus());
            job.setRemark(command.getRemark());

            sysJobService.updateJob(job);
            return ResponseDTO.ok();
        } catch (SchedulerException e) {
            throw new CustomException("更新定时任务失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除数据源的定时同步任务")
    @PreAuthorize("@permission.has('metadata:datasource:edit')")
    @AccessLog(title = "数据源管理", businessType = BusinessTypeEnum.DELETE)
    @DeleteMapping("/{datasourceId}/syncJob")
    public ResponseDTO<?> deleteSyncJob(@PathVariable Long datasourceId) {
        try {
            String invokeTarget = "metadataSyncJob.syncDatasource(" + datasourceId + "L)";
            SysJob query = new SysJob();
            query.setJobGroup("METADATA_SYNC");
            query.setInvokeTarget(invokeTarget);
            List<SysJob> jobs = sysJobService.selectJobList(query);

            if (jobs.isEmpty()) {
                throw new CustomException("定时任务不存在");
            }

            sysJobService.deleteJob(jobs.get(0));
            return ResponseDTO.ok();
        } catch (SchedulerException e) {
            throw new CustomException("删除定时任务失败：" + e.getMessage());
        }
    }

    @ApiOperation("查询数据源同步日志")
    @PreAuthorize("@permission.has('metadata:datasource:query')")
    @GetMapping("/{datasourceId}/syncLogs")
    public ResponseDTO<List<SysJobLog>> getSyncLogs(@PathVariable Long datasourceId) {
        String invokeTarget = "metadataSyncJob.syncDatasource(" + datasourceId + "L)";
        SysJobLog query = new SysJobLog();
        query.setInvokeTarget(invokeTarget);
        List<SysJobLog> logs = sysJobLogService.selectJobLogList(query);
        return ResponseDTO.ok(logs);
    }
}
