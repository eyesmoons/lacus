package com.lacus.domain.monitor.alert;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lacus.dao.alert.entity.AlertChannelInstanceEntity;
import com.lacus.dao.alert.entity.AlertChannelTypeEntity;
import com.lacus.dao.alert.entity.AlertGroupEntity;
import com.lacus.dao.alert.entity.AlertRecordEntity;
import com.lacus.dao.alert.entity.AlertSendLogEntity;
import com.lacus.dao.alert.entity.AlertSendTaskEntity;
import com.lacus.dao.system.query.AbstractPageQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

public final class AlertCenterModels {

    private AlertCenterModels() {
    }

    @Data
    public static class OptionDTO {
        private Long value;
        private String label;

        public OptionDTO() {
        }

        public OptionDTO(Long value, String label) {
            this.value = value;
            this.label = label;
        }
    }

    @Data
    public static class ChannelTypeDTO {
        private Long id;
        private String typeCode;
        private String typeName;
        private String notifierBean;
        private String configSchema;
        private Boolean enabled;
        private Integer sortOrder;
        private String remark;

        public ChannelTypeDTO(AlertChannelTypeEntity entity) {
            BeanUtil.copyProperties(entity, this);
        }
    }

    @Data
    public static class ChannelInstanceDTO {
        private Long id;
        private Long channelTypeId;
        private String typeCode;
        private String typeName;
        private String instanceCode;
        private String instanceName;
        private Boolean enabled;
        private String testStatus;
        private Date lastTestTime;
        private Date createTime;

        public ChannelInstanceDTO() {
        }
    }

    @Data
    public static class ChannelInstanceDetailDTO extends ChannelInstanceDTO {
        private String configSchema;
        private Map<String, Object> config;
    }

    @Data
    public static class ChannelInstanceUpsertCommand {
        private Long id;
        @NotNull(message = "渠道类型不能为空")
        private Long channelTypeId;
        @NotBlank(message = "实例编码不能为空")
        private String instanceCode;
        @NotBlank(message = "实例名称不能为空")
        private String instanceName;
        @NotNull(message = "实例配置不能为空")
        private Map<String, Object> config;
        private Boolean enabled = Boolean.TRUE;
    }

    @Data
    public static class ChannelInstanceTestCommand {
        private Long instanceId;
        private Long channelTypeId;
        private Map<String, Object> config;
        @NotBlank(message = "测试标题不能为空")
        private String testTitle;
        @NotBlank(message = "测试内容不能为空")
        private String testContent;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ChannelInstanceQuery extends AbstractPageQuery {
        private String searchVal;
        private Long channelTypeId;
        private Boolean enabled;
        private String testStatus;

        @Override
        public QueryWrapper toQueryWrapper() {
            QueryWrapper<AlertChannelInstanceEntity> wrapper = new QueryWrapper<>();
            wrapper.eq(channelTypeId != null, "channel_type_id", channelTypeId)
                .eq(enabled != null, "enabled", enabled)
                .eq(StrUtil.isNotBlank(testStatus), "test_status", testStatus)
                .and(StrUtil.isNotBlank(searchVal), q -> q.like("instance_code", searchVal).or().like("instance_name", searchVal));
            addSortCondition(wrapper);
            if (StrUtil.isBlank(getOrderByColumn())) {
                wrapper.orderByDesc("id");
            }
            return wrapper;
        }
    }

    @Data
    public static class GroupBindingCommand {
        @NotNull(message = "实例不能为空")
        private Long channelInstanceId;
        private Integer notifyOrder = 1;
    }

    @Data
    public static class AlertGroupDTO {
        private Long id;
        private String groupCode;
        private String groupName;
        private String description;
        private Boolean enabled;
        private Integer channelCount;
        private Date createTime;
    }

    @Data
    public static class AlertGroupDetailDTO extends AlertGroupDTO {
        private List<GroupBindingDetailDTO> channelBindings = new ArrayList<>();
    }

    @Data
    public static class GroupBindingDetailDTO {
        private Long channelInstanceId;
        private String instanceCode;
        private String instanceName;
        private String typeCode;
        private String typeName;
        private Integer notifyOrder;
        private Boolean enabled;
    }

    @Data
    public static class AlertGroupUpsertCommand {
        private Long id;
        @NotBlank(message = "告警组编码不能为空")
        private String groupCode;
        @NotBlank(message = "告警组名称不能为空")
        private String groupName;
        private String description;
        private Boolean enabled = Boolean.TRUE;
        @NotEmpty(message = "请至少绑定一个告警实例")
        @Valid
        private List<GroupBindingCommand> channelBindings;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class AlertGroupQuery extends AbstractPageQuery {
        private String searchVal;
        private Boolean enabled;

        @Override
        public QueryWrapper toQueryWrapper() {
            QueryWrapper<AlertGroupEntity> wrapper = new QueryWrapper<>();
            wrapper.eq(enabled != null, "enabled", enabled)
                .and(StrUtil.isNotBlank(searchVal), q -> q.like("group_code", searchVal).or().like("group_name", searchVal));
            addSortCondition(wrapper);
            if (StrUtil.isBlank(getOrderByColumn())) {
                wrapper.orderByDesc("id");
            }
            return wrapper;
        }
    }

    @Data
    public static class AlertExecuteCommand {
        @NotBlank(message = "告警组编码不能为空")
        private String groupCode;
        private String triggerSource = AlertCenterConstants.TriggerSource.MANUAL;
        private String bizKey;
        @NotBlank(message = "告警级别不能为空")
        private String alertLevel;
        @NotBlank(message = "告警标题不能为空")
        private String title;
        @NotBlank(message = "告警内容不能为空")
        private String content;
        private String requestedBy;
        private Map<String, Object> ext;
    }

    @Data
    public static class AlertExecuteResultDTO {
        private Long recordId;
        private String recordNo;
        private Integer taskCount;
        private String status;
    }

    @Data
    public static class AlertRecordDTO {
        private Long id;
        private String recordNo;
        private String groupCode;
        private String groupName;
        private String triggerSource;
        private String bizKey;
        private String alertLevel;
        private String title;
        private String status;
        private Integer channelCount;
        private Integer successCount;
        private Integer failedCount;
        private String requestedBy;
        private Date requestedTime;
        private Date finishedTime;
        private String errorMessage;

        public AlertRecordDTO() {
        }

        public AlertRecordDTO(AlertRecordEntity entity) {
            BeanUtil.copyProperties(entity, this);
        }
    }

    @Data
    public static class AlertRecordDetailDTO extends AlertRecordDTO {
        private String content;
        private String extJson;
        private List<AlertSendTaskDTO> tasks = new ArrayList<>();
    }

    @Data
    public static class AlertSendTaskDTO {
        private Long id;
        private String taskNo;
        private String channelTypeCode;
        private String instanceCode;
        private String instanceName;
        private String status;
        private Integer retryCount;
        private Integer maxRetryCount;
        private Date nextRetryTime;
        private Date startedTime;
        private Date finishedTime;
        private String responseSummary;
        private String lastError;

        public AlertSendTaskDTO() {
        }

        public AlertSendTaskDTO(AlertSendTaskEntity entity) {
            BeanUtil.copyProperties(entity, this);
        }
    }

    @Data
    public static class AlertSendLogDTO {
        private Long id;
        private Integer attemptNo;
        private String requestPayload;
        private String responsePayload;
        private Boolean success;
        private Integer costMs;
        private String errorMessage;
        private Date createTime;

        public AlertSendLogDTO(AlertSendLogEntity entity) {
            BeanUtil.copyProperties(entity, this);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class AlertRecordQuery extends AbstractPageQuery {
        private String groupCode;
        private String status;
        private String alertLevel;
        private String keyword;

        @Override
        public QueryWrapper toQueryWrapper() {
            QueryWrapper<AlertRecordEntity> wrapper = new QueryWrapper<>();
            wrapper.eq(StrUtil.isNotBlank(groupCode), "group_code", groupCode)
                .eq(StrUtil.isNotBlank(status), "status", status)
                .eq(StrUtil.isNotBlank(alertLevel), "alert_level", alertLevel)
                .and(StrUtil.isNotBlank(keyword), q -> q.like("record_no", keyword)
                    .or().like("title", keyword)
                    .or().like("content", keyword)
                    .or().like("biz_key", keyword));
            addTimeCondition(wrapper, "requested_time");
            addSortCondition(wrapper);
            if (StrUtil.isBlank(getOrderByColumn())) {
                wrapper.orderByDesc("requested_time");
            }
            return wrapper;
        }
    }
}
