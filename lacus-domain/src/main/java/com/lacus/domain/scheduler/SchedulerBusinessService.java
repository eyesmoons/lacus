package com.lacus.domain.scheduler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lacus.common.exception.CustomException;
import com.lacus.domain.scheduler.dto.ProcessInstance;
import com.lacus.domain.scheduler.dto.ProcessInstanceDetail;
import com.lacus.domain.scheduler.dto.ProcessInstanceRunInfo;
import com.lacus.domain.scheduler.dto.ProcessTaskInstance;
import com.lacus.domain.scheduler.dto.ScheduleParam;
import com.lacus.domain.scheduler.feign.SchedulerFeign;
import com.lacus.domain.scheduler.feign.vo.ProcessDefinition;
import com.lacus.domain.scheduler.feign.vo.SchedulerResponse;
import com.lacus.enums.CommandType;
import com.lacus.enums.FailureStrategy;
import com.lacus.enums.WarningType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class SchedulerBusinessService {

    @Autowired
    private SchedulerFeign schedulerFeign;

    /**
     * 根据流程定义id查询最近一次流程实例
     *
     * @param projectName         项目名称
     * @param processDefinitionId 流程定义id
     */
    @SuppressWarnings("rawtypes")
    public ProcessInstanceRunInfo getLastProcessInstance(String projectName, Integer processDefinitionId) {
        SchedulerResponse response = schedulerFeign.queryProcessInstanceList(projectName, processDefinitionId, "", "", null, "", "", "", 1, 1);
        if (response.getCode() != 0) {
            throw new CustomException("查询流程实例错误：" + response.getMsg());
        }

        JSONObject json = JSONObject.parseObject(JSON.toJSONString(response.getData()));
        JSONArray jsonArr = json.getJSONArray("list");

        if (!jsonArr.isEmpty()) {
            return JSON.toJavaObject(jsonArr.getJSONObject(0), ProcessInstanceRunInfo.class);
        }
        return null;
    }

    /**
     * 获取任务实例Map
     *
     * @param processTaskList 流程节点列表
     */
    public Map<String, ProcessTaskInstance> getInstanceRunMap(ProcessInstance processTaskList) {
        Map<String, ProcessTaskInstance> instanceRunMap = new HashMap<>();
        List<ProcessTaskInstance> taskList = processTaskList.getTaskList();
        if (!taskList.isEmpty()) {
            taskList.forEach(item -> {
                JSONObject taskObj = JSONObject.parseObject(item.getTaskJson());
                instanceRunMap.put(taskObj.getString("id"), item);
            });
        }

        return instanceRunMap;
    }

    /**
     * 根据流程id查询流程信息
     *
     */
    public ProcessDefinition getProcessDefinitionByProcessId(Long projectCode, Long processCode) {
        SchedulerResponse<ProcessDefinition> response = schedulerFeign.queryProcessDefinitionById(projectCode, processCode);
        if (response.getCode() != 0) {
            throw new CustomException("项目流程错误：" + response.getMsg());
        }
        return response.getData();
    }

    /**
     * 根据流程id获取任务列表
     *
     * @param projectName 项目名称
     * @param instanceId  流程实例id
     */
    @SuppressWarnings("rawtypes")
    public ProcessInstance getProcessTaskList(String projectName, Integer instanceId) {
        SchedulerResponse response = schedulerFeign.queryTaskListByProcessId(projectName, instanceId);
        if (response.getCode() != 0) {
            throw new CustomException("查询任务错误：" + response.getMsg());
        }
        Object data = response.getData();
        if (data != null) {
            return JSONObject.parseObject(JSON.toJSONString(data), ProcessInstance.class);
        }
        return null;
    }

    /**
     * 根据id查询流程实例详情
     *
     * @param projectName 项目名称
     * @param instanceId  流程实例id
     */
    @SuppressWarnings("rawtypes")
    public ProcessInstanceDetail queryProcessInstanceById(String projectName, Integer instanceId) {
        SchedulerResponse response = schedulerFeign.queryProcessInstanceById(projectName, instanceId);
        if (response.getCode() != 0) {
            throw new CustomException("查询任务实例错误：" + response.getMsg());
        }

        Object data = response.getData();
        if (data != null) {
            return JSONObject.parseObject(JSON.toJSONString(data), ProcessInstanceDetail.class);
        }
        return null;
    }

    public Map<String, Long> createProcessDef(Long projectCode,
                                              String name,
                                              String taskRelationJson,
                                              String taskDefinitionJson,
                                              String locations,
                                              String description,
                                              Boolean online,
                                              Integer enableCronTab,
                                              String cronExpression,
                                              Date cronEndTime,
                                              String tenantCode,
                                              String workerGroup) {
        Long processCode = null;
        Long schedulerId = null;
        try {
            try {
                SchedulerResponse<Map<String, Object>> processDefinitionResponse = schedulerFeign.createProcessDefinition(projectCode, name, taskRelationJson, taskDefinitionJson, locations, description);
                if (processDefinitionResponse.getCode() != 0) {
                    throw new CustomException("创建流程定义错误：" + processDefinitionResponse.getMsg());
                }

                Map<String, Object> data = processDefinitionResponse.getData();
                if (data != null) {
                    processCode = Long.valueOf(data.get("code").toString());
                }
            } catch (JsonProcessingException e) {
                log.error("json解析异常：{}", e.getMessage());
            }

            // 上线工作流定义
            if (online && ObjectUtils.isNotEmpty(processCode)) {
                releaseProcessDes(projectCode, processCode, "ONLINE");
            }

            // 创建定时任务
            if (Objects.nonNull(cronExpression) && StringUtils.isNotBlank(cronExpression)) {
                Date nowDate = new Date();
                ScheduleParam scheduleParam = new ScheduleParam();
                scheduleParam.setCrontab(cronExpression);
                scheduleParam.setTimezoneId("Asia/Shanghai");
                scheduleParam.setFailureStrategy("END");
                scheduleParam.setTenantCode(tenantCode);
                scheduleParam.setStartTime(nowDate);
                scheduleParam.setEndTime(Objects.nonNull(cronEndTime) ? cronEndTime : DateUtils.addYears(nowDate, 30));
                SchedulerResponse<Map<String, Object>> createSchedulerResponse = schedulerFeign.createSchedule(projectCode, processCode, JSON.toJSONString(scheduleParam), workerGroup);
                if (createSchedulerResponse.getCode() != 0) {
                    throw new CustomException("创建定时任务错误：" + createSchedulerResponse.getMsg());
                }
                Map<String, Object> data = createSchedulerResponse.getData();
                schedulerId = Long.valueOf(data.get("id").toString());
            }
            if (ObjectUtils.isNotEmpty(cronExpression) && Objects.equals(1, enableCronTab) && ObjectUtils.isNotEmpty(schedulerId)) {
                onlineScheduler(projectCode, Integer.parseInt(schedulerId.toString()));
            } else if (Objects.equals(0, enableCronTab) && ObjectUtils.isNotEmpty(schedulerId)) {
                offlineScheduler(projectCode, Integer.parseInt(schedulerId.toString()));
            }
        } catch (Exception e) {
            throw new CustomException("创建流程定义接口调用异常：" + e.getMessage());
        }
        Map<String, Long> result = new HashMap<>();
        result.put("processCode", processCode);
        result.put("schedulerId", schedulerId);
        return result;
    }

    /**
     * 上下线工作流定义
     */
    @SuppressWarnings("rawtypes")
    public Boolean releaseProcessDes(Long projectCode, Long processCode, String releaseState) {
        SchedulerResponse onlineProcessResponse = schedulerFeign.releaseProcessDefinition(projectCode, processCode, releaseState);
        if (onlineProcessResponse.getCode() != 0) {
            throw new CustomException("工作流上线或下线错误：" + onlineProcessResponse.getMsg());
        }
        return true;
    }

    /**
     * 上线定时任务
     */
    public void onlineScheduler(Long projectCode, Integer schedulerId) {
        try {
            // 上线定时任务
            SchedulerResponse<?> response = schedulerFeign.online(projectCode, schedulerId);
            log.info("定时任务上线结果：{}", JSON.toJSONString(response));
        } catch (Exception e) {
            throw new CustomException("上线定时任务错误：" + e.getMessage());
        }
    }

    public void offlineScheduler(Long projectCode, Integer schedulerId) {
        try {
            // 下线定时任务
            schedulerFeign.offline(projectCode, schedulerId);
        } catch (Exception e) {
            throw new CustomException("下线定时任务错误：" + e.getMessage());
        }
    }

    /**
     * 启动工作流实例
     */
    @SuppressWarnings("rawtypes")
    public void startProcessInstance(Long projectCode, Long processCode,
                                     String scheduleTime, CommandType commandType,
                                     String tenantCode, String workerGroup) {
        try {
            SchedulerResponse startProcessResponse = schedulerFeign.startProcessInstance(
                    projectCode,
                    processCode,
                    scheduleTime,
                    FailureStrategy.CONTINUE,
                    commandType,
                    WarningType.NONE,
                    workerGroup,
                    tenantCode,
                    null,
                    0);
            if (startProcessResponse.getCode() != 0) {
                throw new CustomException("启动工作流实例失败：" + startProcessResponse.getMsg());
            }
        } catch (ParseException e) {
            throw new CustomException("启动工作流实例失败：" + e.getMessage());
        }
    }

    /**
     * 更新工作流定义
     */
    @SuppressWarnings("rawtypes")
    public Map<String, Long> updateProcessDef(Long projectCode,
                                              String processName,
                                              Long processCode,
                                              Integer schedulerId,
                                              String taskRelationJson,
                                              String taskDefinitionJson,
                                              String locations,
                                              String description,
                                              String cronExpression,
                                              Date cronEndTime,
                                              Integer status,
                                              String tenantCode,
                                              String workerGroup) {
        Long newSchedulerId = null;
        if (ObjectUtils.isNotEmpty(schedulerId)) {
            newSchedulerId = Long.valueOf(schedulerId);
        }
        log.info("1. 下线工作流定义");
        if (ObjectUtils.isNotEmpty(processCode)) {
            Boolean releaseProcess = releaseProcessDes(projectCode, processCode, "OFFLINE");
            if (releaseProcess) {
                log.info("2. 编辑工作流定义：{}", taskDefinitionJson);
                SchedulerResponse updateProcessDefinition = schedulerFeign.updateProcessDefinition(
                        projectCode,
                        processCode,
                        processName,
                        description,
                        locations,
                        taskRelationJson,
                        taskDefinitionJson);
                if (updateProcessDefinition.getCode() != 0) {
                    throw new CustomException("修改工作流定义失败：" + updateProcessDefinition.getMsg());
                }

                log.info("3. 上线工作流定义");
                if (ObjectUtils.isNotEmpty(processCode)) {
                    releaseProcessDes(projectCode, processCode, "ONLINE");
                }

                log.info("4. 更新定时任务：{}", cronExpression);
                try {
                    Date nowDate = new Date();
                    ScheduleParam scheduleParam = new ScheduleParam();
                    scheduleParam.setCrontab(cronExpression);
                    scheduleParam.setTimezoneId("Asia/Shanghai");
                    scheduleParam.setFailureStrategy("END");
                    scheduleParam.setTenantCode(tenantCode);
                    scheduleParam.setStartTime(nowDate);
                    scheduleParam.setEndTime(Objects.nonNull(cronEndTime) ? cronEndTime : DateUtils.addYears(nowDate, 30));

                    if (Objects.isNull(schedulerId)) {
                        if (!StringUtils.isEmpty(cronExpression)) {
                            //原来的工作流没有定时
                            SchedulerResponse<Map<String, Object>> aDefault = schedulerFeign.createSchedule(projectCode, processCode, JSON.toJSONString(scheduleParam), workerGroup);
                            if (aDefault.getCode() != 0) {
                                throw new CustomException("创建定时任务错误：" + aDefault.getMsg());
                            }
                            Map<String, Object> data = aDefault.getData();
                            newSchedulerId = Long.valueOf(data.get("id").toString());
                        }
                    } else {
                        //原来的工作流有定时
                        if (StringUtils.isEmpty(cronExpression)) {
                            SchedulerResponse responseEntity = schedulerFeign.deleteScheduleById(projectCode, schedulerId);
                            if (responseEntity.getCode() != 0) {
                                throw new CustomException("删除定时任务错误：" + responseEntity.getMsg());
                            }
                            newSchedulerId = null;
                        } else {
                            SchedulerResponse scheduleResponse2 = schedulerFeign.updateSchedule(projectCode, schedulerId, JSON.toJSONString(scheduleParam));
                            if (scheduleResponse2.getCode() != 0) {
                                throw new CustomException("定时任务修改错误：" + scheduleResponse2.getMsg());
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new CustomException("更新定时任务失败：" + e.getMessage());
                }

                log.info("5. 上线定时任务");
                if (Objects.equals(1, status) && ObjectUtils.isNotEmpty(schedulerId)) {
                    onlineScheduler(projectCode, schedulerId);
                } else if (Objects.equals(0, status) && ObjectUtils.isNotEmpty(schedulerId)) {
                    offlineScheduler(projectCode, Integer.parseInt(schedulerId.toString()));
                }
            }
        }
        Map<String, Long> result = new HashMap<>();
        result.put("processCode", processCode);
        result.put("schedulerId", newSchedulerId);
        return result;
    }

    /**
     * 删除工作流定义
     */
    @SuppressWarnings("rawtypes")
    public void deleteProcessDef(Long projectCode, Long processCode) {
        // 1. 下线工作流
        if (ObjectUtils.isNotEmpty(processCode)) {
            releaseProcessDes(projectCode, processCode, "OFFLINE");
        }
        // 2. 删除工作流
        SchedulerResponse deleteResponse = schedulerFeign.deleteProcessDefinitionById(projectCode, processCode);
        if (deleteResponse.getCode() != 0) {
            throw new CustomException("工作流删除错误：" + deleteResponse.getMsg());
        }
    }

    public List<ProcessDefinition> queryAllProcessByProjectId(String projectName, Integer projectId) {
        SchedulerResponse<?> response = schedulerFeign.queryProcessDefinitionAllByProjectId(projectName, projectId);
        if (response.getCode() != 0) {
            throw new CustomException("查询指定项目工作流列表出错：" + response.getMsg());
        }
        Object data = response.getData();
        if (null != data) {
            return JSONArray.parseArray(JSON.toJSONString(data), ProcessDefinition.class);
        }
        return null;
    }

    public Boolean deleteProcessInstance(Long projectCode, Long workflowInstanceId) {
        SchedulerResponse<Void> response = schedulerFeign.deleteProcessInstance(projectCode, workflowInstanceId);
        return response.getCode() == 0;
    }
}
