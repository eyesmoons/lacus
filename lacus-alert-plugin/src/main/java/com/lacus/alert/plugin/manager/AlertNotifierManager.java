package com.lacus.alert.plugin.manager;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lacus.alert.plugin.spi.AlertNotifier;
import com.lacus.dao.alert.entity.AlertChannelTypeEntity;
import com.lacus.dao.alert.mapper.AlertChannelTypeMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AlertNotifierManager {

    @Autowired
    private AlertChannelTypeMapper alertChannelTypeMapper;

    private final Map<String, AlertNotifier> notifierMap = new LinkedHashMap<>();

    @Transactional(rollbackFor = Exception.class)
    public void registerAll() {
        ServiceLoader<AlertNotifier> notifiers = ServiceLoader.load(AlertNotifier.class);
        notifierMap.clear();
        for (AlertNotifier notifier : notifiers) {
            notifierMap.put(notifier.getTypeCode(), notifier);
            try {
                upsertChannelType(notifier);
                log.info("注册告警通知插件成功: {}", notifier.getTypeCode());
            } catch (Exception ex) {
                log.error("注册告警通知插件失败: {}", notifier.getTypeCode(), ex);
            }
        }
    }

    public AlertNotifier getNotifier(String typeCode) {
        return notifierMap.get(typeCode);
    }

    public List<AlertNotifier> listNotifiers() {
        return new ArrayList<>(notifierMap.values());
    }

    private void upsertChannelType(AlertNotifier notifier) {
        LambdaQueryWrapper<AlertChannelTypeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertChannelTypeEntity::getTypeCode, notifier.getTypeCode());
        AlertChannelTypeEntity entity = alertChannelTypeMapper.selectOne(wrapper);
        if (Objects.isNull(entity)) {
            entity = new AlertChannelTypeEntity();
            entity.setTypeCode(notifier.getTypeCode());
        }
        entity.setTypeName(notifier.getTypeName());
        entity.setNotifierBean(notifier.getClass().getName());
        entity.setConfigSchema(JSON.toJSONString(notifier.getConfigSchema()));
        entity.setEnabled(Boolean.TRUE);
        entity.setSortOrder(notifier.getSortOrder());
        entity.setRemark(notifier.getRemark());
        if (entity.getId() == null) {
            alertChannelTypeMapper.insert(entity);
        } else {
            alertChannelTypeMapper.updateById(entity);
        }
    }
}
