package com.lacus.domain.monitor.alert;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.alert.plugin.manager.AlertNotifierManager;
import com.lacus.alert.plugin.spi.AlertNotifier;
import com.lacus.alert.plugin.spi.NotifyContext;
import com.lacus.alert.plugin.spi.NotifyResult;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.core.security.AuthenticationUtils;
import com.lacus.dao.alert.entity.AlertChannelInstanceEntity;
import com.lacus.dao.alert.entity.AlertChannelTypeEntity;
import com.lacus.dao.alert.entity.AlertGroupChannelRelEntity;
import com.lacus.dao.alert.entity.AlertGroupEntity;
import com.lacus.dao.alert.entity.AlertRecordEntity;
import com.lacus.dao.alert.entity.AlertSendLogEntity;
import com.lacus.dao.alert.entity.AlertSendTaskEntity;
import com.lacus.dao.alert.mapper.AlertChannelInstanceMapper;
import com.lacus.dao.alert.mapper.AlertChannelTypeMapper;
import com.lacus.dao.alert.mapper.AlertGroupChannelRelMapper;
import com.lacus.dao.alert.mapper.AlertGroupMapper;
import com.lacus.dao.alert.mapper.AlertRecordMapper;
import com.lacus.dao.alert.mapper.AlertSendLogMapper;
import com.lacus.dao.alert.mapper.AlertSendTaskMapper;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertExecuteCommand;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertExecuteResultDTO;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertGroupDTO;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertGroupDetailDTO;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertGroupQuery;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertGroupUpsertCommand;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertRecordDTO;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertRecordDetailDTO;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertRecordQuery;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertSendLogDTO;
import com.lacus.domain.monitor.alert.AlertCenterModels.AlertSendTaskDTO;
import com.lacus.domain.monitor.alert.AlertCenterModels.ChannelInstanceDTO;
import com.lacus.domain.monitor.alert.AlertCenterModels.ChannelInstanceDetailDTO;
import com.lacus.domain.monitor.alert.AlertCenterModels.ChannelInstanceQuery;
import com.lacus.domain.monitor.alert.AlertCenterModels.ChannelInstanceTestCommand;
import com.lacus.domain.monitor.alert.AlertCenterModels.ChannelInstanceUpsertCommand;
import com.lacus.domain.monitor.alert.AlertCenterModels.ChannelTypeDTO;
import com.lacus.domain.monitor.alert.AlertCenterModels.GroupBindingCommand;
import com.lacus.domain.monitor.alert.AlertCenterModels.GroupBindingDetailDTO;
import com.lacus.domain.monitor.alert.AlertCenterModels.OptionDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertCenterBusiness {

    @Autowired
    private AlertChannelTypeMapper alertChannelTypeMapper;
    @Autowired
    private AlertChannelInstanceMapper alertChannelInstanceMapper;
    @Autowired
    private AlertGroupMapper alertGroupMapper;
    @Autowired
    private AlertGroupChannelRelMapper alertGroupChannelRelMapper;
    @Autowired
    private AlertRecordMapper alertRecordMapper;
    @Autowired
    private AlertSendTaskMapper alertSendTaskMapper;
    @Autowired
    private AlertSendLogMapper alertSendLogMapper;
    @Autowired
    private AlertNotifierManager alertNotifierManager;
    @Autowired
    private AlertConfigCipher alertConfigCipher;
    @Autowired
    private AlertDispatchBusiness alertDispatchBusiness;
    @Autowired
    private AlertProperties alertProperties;

    public List<ChannelTypeDTO> listChannelTypes() {
        LambdaQueryWrapper<AlertChannelTypeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertChannelTypeEntity::getEnabled, true)
            .orderByAsc(AlertChannelTypeEntity::getSortOrder)
            .orderByAsc(AlertChannelTypeEntity::getId);
        return alertChannelTypeMapper.selectList(wrapper).stream().map(ChannelTypeDTO::new).collect(Collectors.toList());
    }

    public PageDTO listChannelInstances(ChannelInstanceQuery query) {
        Page<AlertChannelInstanceEntity> page = alertChannelInstanceMapper.selectPage(query.toPage(), query.toQueryWrapper());
        Map<Long, AlertChannelTypeEntity> typeMap = channelTypeMap(page.getRecords().stream().map(AlertChannelInstanceEntity::getChannelTypeId).collect(Collectors.toSet()));
        List<ChannelInstanceDTO> rows = page.getRecords().stream().map(entity -> toChannelInstanceDTO(entity, typeMap.get(entity.getChannelTypeId()))).collect(Collectors.toList());
        return new PageDTO(rows, page.getTotal());
    }

    public ChannelInstanceDetailDTO getChannelInstance(Long id) {
        AlertChannelInstanceEntity entity = requireInstance(id);
        AlertChannelTypeEntity type = requireChannelType(entity.getChannelTypeId());
        ChannelInstanceDetailDTO dto = new ChannelInstanceDetailDTO();
        BeanUtil.copyProperties(toChannelInstanceDTO(entity, type), dto);
        dto.setConfigSchema(type.getConfigSchema());
        dto.setConfig(alertConfigCipher.maskConfig(type.getTypeCode(), entity.getConfigJson()));
        return dto;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addChannelInstance(ChannelInstanceUpsertCommand command) {
        AlertChannelTypeEntity type = requireChannelType(command.getChannelTypeId());
        checkInstanceUnique(null, command.getInstanceCode(), command.getInstanceName());
        AlertChannelInstanceEntity entity = new AlertChannelInstanceEntity();
        entity.setChannelTypeId(type.getId());
        entity.setInstanceCode(command.getInstanceCode().trim());
        entity.setInstanceName(command.getInstanceName().trim());
        entity.setConfigJson(alertConfigCipher.encryptConfig(type.getTypeCode(), command.getConfig()));
        entity.setEnabled(ObjectUtil.defaultIfNull(command.getEnabled(), Boolean.TRUE));
        entity.setTestStatus(AlertCenterConstants.ChannelTestStatus.UNTESTED);
        entity.setVersion(0);
        alertChannelInstanceMapper.insert(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateChannelInstance(ChannelInstanceUpsertCommand command) {
        AlertChannelInstanceEntity entity = requireInstance(command.getId());
        AlertChannelTypeEntity type = requireChannelType(command.getChannelTypeId());
        checkInstanceUnique(entity.getId(), command.getInstanceCode(), command.getInstanceName());
        entity.setChannelTypeId(type.getId());
        entity.setInstanceCode(command.getInstanceCode().trim());
        entity.setInstanceName(command.getInstanceName().trim());
        entity.setConfigJson(alertConfigCipher.mergeAndEncryptConfig(type.getTypeCode(), command.getConfig(), entity.getConfigJson()));
        entity.setEnabled(ObjectUtil.defaultIfNull(command.getEnabled(), Boolean.TRUE));
        entity.setVersion((entity.getVersion() == null ? 0 : entity.getVersion()) + 1);
        alertChannelInstanceMapper.updateById(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteChannelInstances(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        LambdaQueryWrapper<AlertGroupChannelRelEntity> relWrapper = new LambdaQueryWrapper<>();
        relWrapper.in(AlertGroupChannelRelEntity::getChannelInstanceId, ids);
        alertGroupChannelRelMapper.delete(relWrapper);
        alertChannelInstanceMapper.deleteBatchIds(ids);
    }

    @Transactional(rollbackFor = Exception.class)
    public NotifyResult testChannelInstance(ChannelInstanceTestCommand command) {
        AlertChannelTypeEntity type;
        Map<String, Object> config;
        if (command.getInstanceId() != null) {
            AlertChannelInstanceEntity instance = requireInstance(command.getInstanceId());
            Long targetTypeId = ObjectUtil.defaultIfNull(command.getChannelTypeId(), instance.getChannelTypeId());
            type = requireChannelType(targetTypeId);
            if (ObjectUtil.equal(targetTypeId, instance.getChannelTypeId())) {
                config = mergeTestConfig(type.getTypeCode(), instance.getConfigJson(), command.getConfig());
            } else {
                config = command.getConfig();
            }
        } else {
            type = requireChannelType(command.getChannelTypeId());
            config = command.getConfig();
        }
        AlertNotifier notifier = alertNotifierManager.getNotifier(type.getTypeCode());
        if (notifier == null) {
            throw new ApiException(ErrorCode.Business.UNSUPPORTED_OPERATION, type.getTypeCode());
        }
        NotifyResult result = notifier.send(NotifyContext.builder()
            .title(command.getTestTitle())
            .content(command.getTestContent())
            .alertLevel("INFO")
            .config(config)
            .build());
        if (command.getInstanceId() != null) {
            AlertChannelInstanceEntity instance = requireInstance(command.getInstanceId());
            instance.setTestStatus(result.isSuccess() ? AlertCenterConstants.ChannelTestStatus.SUCCESS : AlertCenterConstants.ChannelTestStatus.FAILED);
            instance.setLastTestTime(new Date());
            alertChannelInstanceMapper.updateById(instance);
        }
        return result;
    }

    public List<OptionDTO> listChannelInstanceOptions() {
        LambdaQueryWrapper<AlertChannelInstanceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertChannelInstanceEntity::getEnabled, true).orderByAsc(AlertChannelInstanceEntity::getInstanceName);
        return alertChannelInstanceMapper.selectList(wrapper).stream()
            .map(item -> new OptionDTO(item.getId(), item.getInstanceName()))
            .collect(Collectors.toList());
    }

    public PageDTO listGroups(AlertGroupQuery query) {
        Page<AlertGroupEntity> page = alertGroupMapper.selectPage(query.toPage(), query.toQueryWrapper());
        Map<Long, Integer> countMap = groupChannelCountMap(page.getRecords().stream().map(AlertGroupEntity::getId).collect(Collectors.toSet()));
        List<AlertGroupDTO> rows = page.getRecords().stream().map(entity -> {
            AlertGroupDTO dto = new AlertGroupDTO();
            BeanUtil.copyProperties(entity, dto);
            dto.setChannelCount(countMap.getOrDefault(entity.getId(), 0));
            return dto;
        }).collect(Collectors.toList());
        return new PageDTO(rows, page.getTotal());
    }

    public AlertGroupDetailDTO getGroup(Long id) {
        AlertGroupEntity entity = requireGroup(id);
        AlertGroupDetailDTO dto = new AlertGroupDetailDTO();
        BeanUtil.copyProperties(entity, dto);
        dto.setChannelCount(groupChannelCountMap(Collections.singleton(id)).getOrDefault(id, 0));
        dto.setChannelBindings(groupBindings(id));
        return dto;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addGroup(AlertGroupUpsertCommand command) {
        checkGroupUnique(null, command.getGroupCode(), command.getGroupName());
        AlertGroupEntity entity = new AlertGroupEntity();
        entity.setGroupCode(command.getGroupCode().trim());
        entity.setGroupName(command.getGroupName().trim());
        entity.setDescription(command.getDescription());
        entity.setEnabled(ObjectUtil.defaultIfNull(command.getEnabled(), Boolean.TRUE));
        alertGroupMapper.insert(entity);
        saveGroupBindings(entity.getId(), command.getChannelBindings());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateGroup(AlertGroupUpsertCommand command) {
        AlertGroupEntity entity = requireGroup(command.getId());
        checkGroupUnique(entity.getId(), command.getGroupCode(), command.getGroupName());
        entity.setGroupCode(command.getGroupCode().trim());
        entity.setGroupName(command.getGroupName().trim());
        entity.setDescription(command.getDescription());
        entity.setEnabled(ObjectUtil.defaultIfNull(command.getEnabled(), Boolean.TRUE));
        alertGroupMapper.updateById(entity);
        LambdaQueryWrapper<AlertGroupChannelRelEntity> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(AlertGroupChannelRelEntity::getGroupId, entity.getId());
        alertGroupChannelRelMapper.delete(deleteWrapper);
        saveGroupBindings(entity.getId(), command.getChannelBindings());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteGroups(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        LambdaQueryWrapper<AlertGroupChannelRelEntity> relWrapper = new LambdaQueryWrapper<>();
        relWrapper.in(AlertGroupChannelRelEntity::getGroupId, ids);
        alertGroupChannelRelMapper.delete(relWrapper);
        alertGroupMapper.deleteBatchIds(ids);
    }

    public List<OptionDTO> listGroupOptions() {
        LambdaQueryWrapper<AlertGroupEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertGroupEntity::getEnabled, true).orderByAsc(AlertGroupEntity::getGroupName);
        return alertGroupMapper.selectList(wrapper).stream().map(item -> new OptionDTO(item.getId(), item.getGroupName())).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public AlertExecuteResultDTO execute(AlertExecuteCommand command) {
        AlertGroupEntity group = requireEnabledGroup(command.getGroupCode());
        List<GroupBindingDetailDTO> bindings = groupBindings(group.getId()).stream().filter(item -> Boolean.TRUE.equals(item.getEnabled())).collect(Collectors.toList());
        if (CollUtil.isEmpty(bindings)) {
            throw new ApiException(ErrorCode.Business.UNSUPPORTED_OPERATION, "告警组未绑定可用实例");
        }
        Date now = new Date();
        AlertRecordEntity record = new AlertRecordEntity();
        record.setRecordNo(generateRecordNo());
        record.setGroupId(group.getId());
        record.setGroupCode(group.getGroupCode());
        record.setGroupName(group.getGroupName());
        record.setTriggerSource(StrUtil.blankToDefault(command.getTriggerSource(), AlertCenterConstants.TriggerSource.MANUAL));
        record.setBizKey(command.getBizKey());
        record.setAlertLevel(command.getAlertLevel());
        record.setTitle(command.getTitle());
        record.setContent(command.getContent());
        record.setExtJson(command.getExt() == null ? null : JSON.toJSONString(command.getExt()));
        record.setStatus(AlertCenterConstants.RecordStatus.PENDING);
        record.setChannelCount(bindings.size());
        record.setSuccessCount(0);
        record.setFailedCount(0);
        record.setRequestedBy(StrUtil.blankToDefault(command.getRequestedBy(), currentUsername()));
        record.setRequestedTime(now);
        alertRecordMapper.insert(record);

        int maxRetry = Math.max(alertProperties.getRetry().getMaxTimes(), 1);
        for (GroupBindingDetailDTO binding : bindings) {
            AlertSendTaskEntity task = new AlertSendTaskEntity();
            task.setTaskNo(generateTaskNo());
            task.setRecordId(record.getId());
            task.setChannelInstanceId(binding.getChannelInstanceId());
            task.setChannelTypeCode(binding.getTypeCode());
            task.setInstanceCode(binding.getInstanceCode());
            task.setInstanceName(binding.getInstanceName());
            task.setStatus(AlertCenterConstants.TaskStatus.WAITING);
            task.setRetryCount(0);
            task.setMaxRetryCount(maxRetry);
            alertSendTaskMapper.insert(task);
        }
        AlertExecuteResultDTO result = new AlertExecuteResultDTO();
        result.setRecordId(record.getId());
        result.setRecordNo(record.getRecordNo());
        result.setTaskCount(bindings.size());
        result.setStatus(record.getStatus());
        return result;
    }

    public PageDTO listRecords(AlertRecordQuery query) {
        Page<AlertRecordEntity> page = alertRecordMapper.selectPage(query.toPage(), query.toQueryWrapper());
        List<AlertRecordDTO> rows = page.getRecords().stream().map(AlertRecordDTO::new).collect(Collectors.toList());
        return new PageDTO(rows, page.getTotal());
    }

    public AlertRecordDetailDTO getRecordDetail(Long id) {
        AlertRecordEntity entity = requireRecord(id);
        AlertRecordDetailDTO dto = new AlertRecordDetailDTO();
        BeanUtil.copyProperties(entity, dto);
        LambdaQueryWrapper<AlertSendTaskEntity> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(AlertSendTaskEntity::getRecordId, id).orderByAsc(AlertSendTaskEntity::getId);
        dto.setTasks(alertSendTaskMapper.selectList(taskWrapper).stream().map(AlertSendTaskDTO::new).collect(Collectors.toList()));
        return dto;
    }

    public List<AlertSendLogDTO> listTaskLogs(Long taskId) {
        LambdaQueryWrapper<AlertSendLogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertSendLogEntity::getTaskId, taskId).orderByDesc(AlertSendLogEntity::getId);
        return alertSendLogMapper.selectList(wrapper).stream().map(AlertSendLogDTO::new).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void retryRecord(Long recordId) {
        AlertRecordEntity record = requireRecord(recordId);
        LambdaQueryWrapper<AlertSendTaskEntity> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(AlertSendTaskEntity::getRecordId, recordId);
        List<AlertSendTaskEntity> tasks = alertSendTaskMapper.selectList(taskWrapper);
        List<AlertSendTaskEntity> retryTasks = tasks.stream()
            .filter(task -> !AlertCenterConstants.TaskStatus.SUCCESS.equals(task.getStatus()))
            .collect(Collectors.toList());
        if (CollUtil.isEmpty(retryTasks)) {
            throw new ApiException(ErrorCode.Business.UNSUPPORTED_OPERATION, "没有可重试的任务");
        }
        Date now = new Date();
        for (AlertSendTaskEntity task : retryTasks) {
            task.setStatus(AlertCenterConstants.TaskStatus.WAITING);
            task.setRetryCount(0);
            task.setNextRetryTime(now);
            task.setStartedTime(null);
            task.setFinishedTime(null);
            task.setResponseSummary(null);
            task.setLastError(null);
            task.setMaxRetryCount(Math.max(alertProperties.getRetry().getMaxTimes(), 1));
            alertSendTaskMapper.updateById(task);
        }
        record.setStatus(AlertCenterConstants.RecordStatus.PENDING);
        record.setFinishedTime(null);
        record.setErrorMessage(null);
        alertRecordMapper.updateById(record);
        alertDispatchBusiness.aggregateRecord(recordId);
    }

    private ChannelInstanceDTO toChannelInstanceDTO(AlertChannelInstanceEntity entity, AlertChannelTypeEntity type) {
        ChannelInstanceDTO dto = new ChannelInstanceDTO();
        dto.setId(entity.getId());
        dto.setChannelTypeId(entity.getChannelTypeId());
        dto.setTypeCode(type == null ? null : type.getTypeCode());
        dto.setTypeName(type == null ? null : type.getTypeName());
        dto.setInstanceCode(entity.getInstanceCode());
        dto.setInstanceName(entity.getInstanceName());
        dto.setEnabled(entity.getEnabled());
        dto.setTestStatus(entity.getTestStatus());
        dto.setLastTestTime(entity.getLastTestTime());
        dto.setCreateTime(entity.getCreateTime());
        return dto;
    }

    private Map<Long, AlertChannelTypeEntity> channelTypeMap(Set<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<AlertChannelTypeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AlertChannelTypeEntity::getId, ids);
        return alertChannelTypeMapper.selectList(wrapper).stream().collect(Collectors.toMap(AlertChannelTypeEntity::getId, item -> item));
    }

    private Map<Long, Integer> groupChannelCountMap(Set<Long> groupIds) {
        if (CollUtil.isEmpty(groupIds)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<AlertGroupChannelRelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AlertGroupChannelRelEntity::getGroupId, groupIds);
        return alertGroupChannelRelMapper.selectList(wrapper).stream().collect(Collectors.groupingBy(AlertGroupChannelRelEntity::getGroupId, Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
    }

    private List<GroupBindingDetailDTO> groupBindings(Long groupId) {
        LambdaQueryWrapper<AlertGroupChannelRelEntity> relWrapper = new LambdaQueryWrapper<>();
        relWrapper.eq(AlertGroupChannelRelEntity::getGroupId, groupId).orderByAsc(AlertGroupChannelRelEntity::getNotifyOrder).orderByAsc(AlertGroupChannelRelEntity::getId);
        List<AlertGroupChannelRelEntity> rels = alertGroupChannelRelMapper.selectList(relWrapper);
        if (CollUtil.isEmpty(rels)) {
            return new ArrayList<>();
        }
        Map<Long, AlertChannelInstanceEntity> instanceMap = instanceMap(rels.stream().map(AlertGroupChannelRelEntity::getChannelInstanceId).collect(Collectors.toSet()));
        Map<Long, AlertChannelTypeEntity> typeMap = channelTypeMap(instanceMap.values().stream().map(AlertChannelInstanceEntity::getChannelTypeId).collect(Collectors.toSet()));
        return rels.stream().map(rel -> {
            AlertChannelInstanceEntity instance = instanceMap.get(rel.getChannelInstanceId());
            AlertChannelTypeEntity type = instance == null ? null : typeMap.get(instance.getChannelTypeId());
            GroupBindingDetailDTO dto = new GroupBindingDetailDTO();
            dto.setChannelInstanceId(rel.getChannelInstanceId());
            dto.setNotifyOrder(rel.getNotifyOrder());
            dto.setEnabled(instance != null && Boolean.TRUE.equals(instance.getEnabled()) && Boolean.TRUE.equals(rel.getEnabled()));
            if (instance != null) {
                dto.setInstanceCode(instance.getInstanceCode());
                dto.setInstanceName(instance.getInstanceName());
            }
            if (type != null) {
                dto.setTypeCode(type.getTypeCode());
                dto.setTypeName(type.getTypeName());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    private Map<Long, AlertChannelInstanceEntity> instanceMap(Set<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<AlertChannelInstanceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AlertChannelInstanceEntity::getId, ids);
        return alertChannelInstanceMapper.selectList(wrapper).stream().collect(Collectors.toMap(AlertChannelInstanceEntity::getId, item -> item));
    }

    private Map<String, Object> mergeTestConfig(String typeCode, String encryptedConfig, Map<String, Object> incomingConfig) {
        Map<String, Object> merged = new LinkedHashMap<>(alertConfigCipher.decryptConfig(typeCode, encryptedConfig));
        if (incomingConfig == null) {
            return merged;
        }
        incomingConfig.forEach((key, value) -> {
            if (AlertCenterConstants.MASKED_VALUE.equals(String.valueOf(value))) {
                return;
            }
            merged.put(key, value);
        });
        return merged;
    }

    private void saveGroupBindings(Long groupId, List<GroupBindingCommand> commands) {
        Map<Long, Integer> duplicates = new HashMap<>();
        for (GroupBindingCommand command : commands) {
            duplicates.put(command.getChannelInstanceId(), duplicates.getOrDefault(command.getChannelInstanceId(), 0) + 1);
        }
        if (duplicates.values().stream().anyMatch(count -> count > 1)) {
            throw new ApiException(ErrorCode.Client.COMMON_REQUEST_PARAMETERS_INVALID, "告警实例不允许重复绑定");
        }
        Map<Long, AlertChannelInstanceEntity> instances = instanceMap(commands.stream().map(GroupBindingCommand::getChannelInstanceId).collect(Collectors.toSet()));
        for (GroupBindingCommand command : commands) {
            if (!instances.containsKey(command.getChannelInstanceId())) {
                throw new ApiException(ErrorCode.Business.OBJECT_NOT_FOUND, command.getChannelInstanceId(), "告警实例");
            }
            AlertGroupChannelRelEntity rel = new AlertGroupChannelRelEntity();
            rel.setGroupId(groupId);
            rel.setChannelInstanceId(command.getChannelInstanceId());
            rel.setNotifyOrder(ObjectUtil.defaultIfNull(command.getNotifyOrder(), 1));
            rel.setEnabled(Boolean.TRUE);
            alertGroupChannelRelMapper.insert(rel);
        }
    }

    private void checkInstanceUnique(Long id, String instanceCode, String instanceName) {
        LambdaQueryWrapper<AlertChannelInstanceEntity> codeWrapper = new LambdaQueryWrapper<>();
        codeWrapper.eq(AlertChannelInstanceEntity::getInstanceCode, instanceCode.trim());
        if (id != null) {
            codeWrapper.ne(AlertChannelInstanceEntity::getId, id);
        }
        if (alertChannelInstanceMapper.selectCount(codeWrapper) > 0) {
            throw new ApiException(ErrorCode.Client.COMMON_REQUEST_PARAMETERS_INVALID, "实例编码已存在");
        }
        LambdaQueryWrapper<AlertChannelInstanceEntity> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(AlertChannelInstanceEntity::getInstanceName, instanceName.trim());
        if (id != null) {
            nameWrapper.ne(AlertChannelInstanceEntity::getId, id);
        }
        if (alertChannelInstanceMapper.selectCount(nameWrapper) > 0) {
            throw new ApiException(ErrorCode.Client.COMMON_REQUEST_PARAMETERS_INVALID, "实例名称已存在");
        }
    }

    private void checkGroupUnique(Long id, String groupCode, String groupName) {
        LambdaQueryWrapper<AlertGroupEntity> codeWrapper = new LambdaQueryWrapper<>();
        codeWrapper.eq(AlertGroupEntity::getGroupCode, groupCode.trim());
        if (id != null) {
            codeWrapper.ne(AlertGroupEntity::getId, id);
        }
        if (alertGroupMapper.selectCount(codeWrapper) > 0) {
            throw new ApiException(ErrorCode.Client.COMMON_REQUEST_PARAMETERS_INVALID, "告警组编码已存在");
        }
        LambdaQueryWrapper<AlertGroupEntity> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(AlertGroupEntity::getGroupName, groupName.trim());
        if (id != null) {
            nameWrapper.ne(AlertGroupEntity::getId, id);
        }
        if (alertGroupMapper.selectCount(nameWrapper) > 0) {
            throw new ApiException(ErrorCode.Client.COMMON_REQUEST_PARAMETERS_INVALID, "告警组名称已存在");
        }
    }

    private AlertChannelTypeEntity requireChannelType(Long id) {
        AlertChannelTypeEntity entity = alertChannelTypeMapper.selectById(id);
        if (entity == null) {
            throw new ApiException(ErrorCode.Business.OBJECT_NOT_FOUND, id, "渠道类型");
        }
        return entity;
    }

    private AlertChannelInstanceEntity requireInstance(Long id) {
        AlertChannelInstanceEntity entity = alertChannelInstanceMapper.selectById(id);
        if (entity == null) {
            throw new ApiException(ErrorCode.Business.OBJECT_NOT_FOUND, id, "告警实例");
        }
        return entity;
    }

    private AlertGroupEntity requireGroup(Long id) {
        AlertGroupEntity entity = alertGroupMapper.selectById(id);
        if (entity == null) {
            throw new ApiException(ErrorCode.Business.OBJECT_NOT_FOUND, id, "告警组");
        }
        return entity;
    }

    private AlertGroupEntity requireEnabledGroup(String groupCode) {
        LambdaQueryWrapper<AlertGroupEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertGroupEntity::getGroupCode, groupCode).eq(AlertGroupEntity::getEnabled, true);
        AlertGroupEntity entity = alertGroupMapper.selectOne(wrapper);
        if (entity == null) {
            throw new ApiException(ErrorCode.Client.COMMON_REQUEST_PARAMETERS_INVALID, "告警组不存在或已停用");
        }
        return entity;
    }

    private AlertRecordEntity requireRecord(Long id) {
        AlertRecordEntity entity = alertRecordMapper.selectById(id);
        if (entity == null) {
            throw new ApiException(ErrorCode.Business.OBJECT_NOT_FOUND, id, "告警记录");
        }
        return entity;
    }

    private String currentUsername() {
        try {
            return AuthenticationUtils.getUsername();
        } catch (Exception ex) {
            return AlertCenterConstants.SYSTEM_OPERATOR;
        }
    }

    private String generateRecordNo() {
        return "rec_" + IdUtil.getSnowflakeNextIdStr();
    }

    private String generateTaskNo() {
        return "task_" + IdUtil.getSnowflakeNextIdStr();
    }
}
