package com.lacus.service.impl;

import com.alibaba.fastjson2.JSON;
import com.lacus.common.constants.Constant;
import com.lacus.common.exception.ApiException;
import com.lacus.common.utils.DateUtils;
import com.lacus.dao.entity.ApiHistoryEntity;
import com.lacus.service.IBuriedFuncService;
import com.qtong.data.domain.BuriedLog;
import com.qtong.data.domain.Record;
import com.qtong.data.processor.BuriedProcessor;
import com.qtong.data.processor.KafkaProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class BuriedFuncServiceImpl implements IBuriedFuncService {


    private BuriedProcessor processor;


    @Value("${kafka.bootstrapServers}")
    private String bootstrapServer;

    @PostConstruct
    private void init() {
        processor = new KafkaProcessor(bootstrapServer);
        log.info("初始化连接到的kafka地址为:{}",bootstrapServer);
    }

    @Override
    public void buriedFunc(ApiHistoryEntity entity) {
        Record record = Record.builder()
                .itemDate(DateUtils.getCurrentTime())
                .itemName(entity.getApiUrl())
                .itemInfo(entity.getErrorInfo())
                .itemStatus(entity.getCallStatus().name())
                .itemIp(entity.getCallIp())
                .itemExtend(JSON.toJSONString(entity))
                .itemText1(String.valueOf(entity.getCallCode())) //调用状态
                .itemText2(String.valueOf(entity.getCallDelay()))   //调用延迟
                .itemText3(DateUtils.dateToString(entity.getCallTime()))   //调用时间
                .build();
        BuriedLog buriedLog = BuriedLog.builder().application(Constant.SERVICE_NAME.toUpperCase()).record(record).build();
        try {
            processor.sendEvent(buriedLog);
        } catch (Exception ex) {
            throw new ApiException("埋点日志数据发送失败！", ex);
        }

    }


}
