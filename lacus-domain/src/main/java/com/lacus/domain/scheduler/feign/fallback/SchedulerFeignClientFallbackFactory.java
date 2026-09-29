package com.lacus.domain.scheduler.feign.fallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lacus.domain.scheduler.feign.SchedulerFeign;
import com.lacus.domain.scheduler.feign.vo.SchedulerResponse;
import com.lacus.enums.CommandType;
import com.lacus.enums.ExecutionStatus;
import com.lacus.enums.FailureStrategy;
import com.lacus.enums.ResourceType;
import com.lacus.enums.ResultCode;
import com.lacus.enums.WarningType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;


@Component
@Slf4j
public class SchedulerFeignClientFallbackFactory implements FallbackFactory<SchedulerFeign> {

    @SuppressWarnings({"rawtypes"})
    @Override
    public SchedulerFeign create(Throwable e) {
        log.error("call [sql route] service error，reason：[{}]，fallback！", e.getMessage());

        return new SchedulerFeign() {

            public SchedulerResponse dataFail(String message) {
                return new SchedulerResponse<>(ResultCode.CALL_THIRD_FAILED.getCode(), message + e.getMessage());
            }

            @Override
            public SchedulerResponse queryProcessDefinitionById(Long projectCode, Long processCode) {
                return dataFail("查询流程定义出错：");
            }

            @Override
            public SchedulerResponse queryProjectById(Integer projectId) {
                return dataFail("查询项目出错：");
            }

            @Override
            public SchedulerResponse queryProcessInstanceList(String projectName, Integer processDefinitionId, String searchVal, String executorName, ExecutionStatus stateType, String host, String startTime, String endTime, Integer pageNum, Integer pageSize) {
                return dataFail("查询流程实例出错：");
            }

            @Override
            public SchedulerResponse queryTaskListByProcessId(String projectName, Integer processInstanceId) {
                return dataFail("查询任务列表出错：");
            }

            @Override
            public SchedulerResponse queryProcessInstanceById(String projectName, Integer processInstanceId) {
                return dataFail("查询流程实例出错：");
            }

            @Override
            public SchedulerResponse createProcessDefinition(Long projectCode, String name, String taskRelationJson, String taskDefinitionJson, String locations, String description) throws JsonProcessingException {
                return dataFail("创建流程定义出错：");
            }

            @Override
            public SchedulerResponse updateProcessDefinition(Long projectCode, long code, String name, String description, String locations, String taskRelationJson, String taskDefinitionJson) {
                return dataFail("更新流程定义出错：");
            }

            @Override
            public SchedulerResponse deleteProcessDefinitionById(long projectCode, long workflowDefinitionCode) {
                return dataFail("删除流程定义出错：");
            }

            @Override
            public SchedulerResponse releaseProcessDefinition(long projectCode, long processCode, String releaseState) {
                return dataFail("上下线流程定义出错：");
            }

            @Override
            public SchedulerResponse startProcessInstance(long projectCode, long processDefinitionCode, String scheduleTime, FailureStrategy failureStrategy, CommandType execType, WarningType warningType, String workerGroup, String tenantCode, String startParams, int testFlag) throws ParseException {
                return dataFail("启动流程实例出错：");
            }

            @Override
            public SchedulerResponse createSchedule(long projectCode, long processDefinitionCode, String schedule, String workerGroup) throws IOException {
                return dataFail("创建定时器出错：");
            }

            @Override
            public SchedulerResponse updateSchedule(long projectCode, Integer id, String schedule) throws IOException {
                return dataFail("更新定时器出错：");
            }

            @Override
            public SchedulerResponse deleteScheduleById(Long projectCode, Integer scheduleId) {
                return dataFail("删除定时器出错：");
            }

            @Override
            public SchedulerResponse online(Long projectCode, Integer id) {
                return dataFail("上线定时器出错：");
            }

            @Override
            public SchedulerResponse offline(Long projectCode, Integer id) {
                return dataFail("下线定时器出错：");
            }

            @Override
            public SchedulerResponse queryScheduleListPaging(long projectCode, long processDefinitionCode, Integer pageNum, Integer pageSize) {
                return dataFail("查询定时器出错：");
            }

            @Override
            public SchedulerResponse queryResource(String fullName, ResourceType type, String tenantCode) {
                return dataFail("查询资源出错：");
            }

            @Override
            public SchedulerResponse createProject(String projectName, String description) {
                return dataFail("创建项目出错：");
            }

            @Override
            public SchedulerResponse queryProcessDefinitionAllByProjectId(String projectName, Integer projectId) {
                return dataFail("查询流程定义出错：");
            }

            @Override
            public SchedulerResponse deleteProcessInstance(long projectCode, Long workflowInstanceId) {
                return dataFail("删除工作流实例出错：");
            }
        };
    }
}
