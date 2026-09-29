package com.lacus.domain.scheduler.feign;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lacus.domain.scheduler.feign.conf.SchedulerFeignConfiguration;
import com.lacus.domain.scheduler.feign.fallback.SchedulerFeignClientFallbackFactory;
import com.lacus.domain.scheduler.feign.vo.ProcessDefinition;
import com.lacus.domain.scheduler.feign.vo.ScheduleVO;
import com.lacus.domain.scheduler.feign.vo.SchedulerResponse;
import com.lacus.enums.CommandType;
import com.lacus.enums.ExecutionStatus;
import com.lacus.enums.FailureStrategy;
import com.lacus.enums.ResourceType;
import com.lacus.enums.WarningType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@FeignClient(name = "dolphinschedulerFeignClient",
        url = "${dolphinscheduler.service_address}",
        contextId = "dolphinschedulerFeignClient",
        fallbackFactory = SchedulerFeignClientFallbackFactory.class,
        configuration = SchedulerFeignConfiguration.class)
public interface SchedulerFeign {

    /**
     * 根据流程定义ID查询流程定义详情
     */
    @GetMapping("/projects/{projectCode}/process-definition/{processCode}")
    SchedulerResponse<ProcessDefinition> queryProcessDefinitionById(@PathVariable("projectCode") Long projectCode, @PathVariable("processCode") Long processCode);

    /**
     * 根据项目ID查询项目详情
     *
     * @param projectId
     */
    @GetMapping("/projects/query-by-id")
    SchedulerResponse queryProjectById(@RequestParam("projectId") Integer projectId);

    /**
     * 流程实例列表分页
     */
    @GetMapping("/projects/{projectName}/instance/list-paging")
    SchedulerResponse queryProcessInstanceList(@PathVariable("projectName") String projectName,
                                               @RequestParam(value = "processDefinitionId", required = false, defaultValue = "0") Integer processDefinitionId,
                                               @RequestParam(value = "searchVal", required = false) String searchVal,
                                               @RequestParam(value = "executorName", required = false) String executorName,
                                               @RequestParam(value = "stateType", required = false) ExecutionStatus stateType,
                                               @RequestParam(value = "host", required = false) String host,
                                               @RequestParam(value = "startDate", required = false) String startTime,
                                               @RequestParam(value = "endDate", required = false) String endTime,
                                               @RequestParam("pageNum") Integer pageNum,
                                               @RequestParam("pageSize") Integer pageSize);

    /**
     * 根据流程实例ID查询子任务列表
     */
    @GetMapping("/projects/{projectName}/instance/task-list-by-process-id")
    SchedulerResponse queryTaskListByProcessId(@PathVariable("projectName") String projectName, @RequestParam("processInstanceId") Integer processInstanceId);

    /**
     * 根据流程实例ID查询详情
     */
    @GetMapping("/projects/{projectName}/instance/select-by-id")
    SchedulerResponse queryProcessInstanceById(@PathVariable("projectName") String projectName, @RequestParam("processInstanceId") Integer processInstanceId);

    /**
     * 新增工作流
     */
    @PostMapping("/projects/{projectCode}/process-definition")
    SchedulerResponse<Map<String, Object>> createProcessDefinition(@PathVariable("projectCode") Long projectCode,
                                                                   @RequestParam("name") String name,
                                                                   @RequestParam(value = "taskRelationJson") String taskRelationJson,
                                                                   @RequestParam(value = "taskDefinitionJson") String taskDefinitionJson,
                                                                   @RequestParam("locations") String locations,
                                                                   @RequestParam(value = "description", required = false) String description) throws JsonProcessingException;

    /**
     * 更新工作流
     */
    @PutMapping("/projects/{projectCode}/process-definition/{code}")
    SchedulerResponse updateProcessDefinition(@PathVariable("projectCode") Long projectCode,
                                              @PathVariable(value = "code", required = true) long code,
                                              @RequestParam(value = "name") String name,
                                              @RequestParam(value = "description", required = false) String description,
                                              @RequestParam("locations") String locations,
                                              @RequestParam(value = "taskRelationJson", required = true) String taskRelationJson,
                                              @RequestParam(value = "taskDefinitionJson", required = true) String taskDefinitionJson);

    /**
     * 删除工作流
     */
    @DeleteMapping(value = "/projects/{projectCode}/process-definition/{code}")
    SchedulerResponse deleteProcessDefinitionById(@PathVariable("projectCode") long projectCode,
                                                  @PathVariable("code") long workflowDefinitionCode);

    /**
     * 工作流上下线
     */
    @PostMapping(value = "/projects/{projectCode}/process-definition/{code}/release")
    SchedulerResponse releaseProcessDefinition(@PathVariable("projectCode") long projectCode,
                                               @PathVariable(value = "code", required = true) long processCode,
                                               @RequestParam(value = "releaseState", required = true) String releaseState);

    /**
     * 执行工作流
     */
    @PostMapping(value = "/projects/{projectCode}/executors/start-process-instance")
    SchedulerResponse startProcessInstance(@PathVariable("projectCode") long projectCode,
                                           @RequestParam(value = "processDefinitionCode") long processDefinitionCode,
                                           @RequestParam(value = "scheduleTime") String scheduleTime,
                                           @RequestParam(value = "failureStrategy") FailureStrategy failureStrategy,
                                           @RequestParam(value = "execType", required = false) CommandType execType,
                                           @RequestParam(value = "warningType") WarningType warningType,
                                           @RequestParam(value = "workerGroup", required = false, defaultValue = "default") String workerGroup,
                                           @RequestParam(value = "tenantCode", required = false, defaultValue = "default") String tenantCode,
                                           @RequestParam(value = "startParams", required = false) String startParams,
                                           @RequestParam(value = "testFlag", defaultValue = "0") int testFlag) throws ParseException;


    /**
     * 创建定时任务
     */
    @PostMapping("/projects/{projectCode}/schedules")
    SchedulerResponse<Map<String, Object>> createSchedule(@PathVariable("projectCode") long projectCode,
                                                          @RequestParam(value = "processDefinitionCode") long processDefinitionCode,
                                                          @RequestParam(value = "schedule") String schedule,
                                                          @RequestParam(value = "workerGroup", required = false, defaultValue = "default") String workerGroup) throws IOException;

    /**
     * 更新定时任务
     */
    @PutMapping("/projects/{projectCode}/schedules/{id}")
    SchedulerResponse updateSchedule(@PathVariable("projectCode") long projectCode,
                                     @PathVariable(value = "id") Integer id,
                                     @RequestParam(value = "schedule") String schedule) throws IOException;

    /**
     * 删除定时任务
     */
    @DeleteMapping(value = "/projects/{projectCode}/schedules/{id}")
    SchedulerResponse deleteScheduleById(@PathVariable("projectCode") Long projectCode,
                                         @PathVariable("id") Integer scheduleId
    );

    /**
     * 上线定时任务
     */
    @PostMapping("/projects/{projectCode}/schedules/{id}/online")
    SchedulerResponse online(@PathVariable("projectCode") Long projectCode,
                             @PathVariable("id") Integer id);

    @PostMapping("/projects/{projectCode}/schedules/{id}/offline")
    SchedulerResponse offline(@PathVariable("projectCode") Long projectCode,
                              @PathVariable("id") Integer id);

    /**
     * 根据工作流定义ID查询定时任务列表
     */
    @GetMapping("/projects/{projectCode}/schedules")
    SchedulerResponse<List<ScheduleVO>> queryScheduleListPaging(@PathVariable("projectCode") long projectCode,
                                                                @RequestParam("processDefinitionCode") long processDefinitionCode,
                                                                @RequestParam("pageNo") Integer pageNum,
                                                                @RequestParam("pageSize") Integer pageSize);

    /**
     * 根据fullName和type查询资源信息，type：0 文件 1 udf
     */
    @GetMapping(value = "/resources/query-full-name")
    SchedulerResponse queryResource(@RequestParam(value = "fullName", required = false) String fullName,
                                    @RequestParam(value = "type") ResourceType type,
                                    @RequestParam(value = "tenantCode") String tenantCode);

    /**
     * create project 创建海豚项目
     */
    @PostMapping(value = "/projects/create")
    SchedulerResponse createProject(@RequestParam("projectName") String projectName,
                                    @RequestParam(value = "description", required = false) String description);

    /**
     * query process definition all by project id 查询指定海豚项目的工作流定义列表
     */
    @GetMapping(value = "/projects/{projectName}/process/queryProcessDefinitionAllByProjectId")
    SchedulerResponse queryProcessDefinitionAllByProjectId(@PathVariable("projectName") String projectName,
                                                           @RequestParam("projectId") Integer projectId);

    @DeleteMapping("/projects/{projectCode}/process-instances/{workflowInstanceId}")
    SchedulerResponse deleteProcessInstance(@PathVariable("projectCode") long projectCode, @PathVariable("workflowInstanceId") Long workflowInstanceId);
}
