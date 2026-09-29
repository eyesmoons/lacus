package com.lacus.alert.plugin.notifier;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.google.auto.service.AutoService;
import com.lacus.alert.plugin.spi.AlertConfigField;
import com.lacus.alert.plugin.spi.AlertNotifier;
import com.lacus.alert.plugin.spi.NotifyContext;
import com.lacus.alert.plugin.spi.NotifyResult;
import com.lacus.alert.plugin.support.AlertPluginConfigHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoService(AlertNotifier.class)
public class SmsAlertNotifier implements AlertNotifier {

    @Override
    public String getTypeCode() {
        return "SMS";
    }

    @Override
    public String getTypeName() {
        return "短信";
    }

    @Override
    public String getRemark() {
        return "通用 HTTP 短信网关通知";
    }

    @Override
    public Integer getSortOrder() {
        return 30;
    }

    @Override
    public List<AlertConfigField> getConfigSchema() {
        List<AlertConfigField> fields = new ArrayList<>();
        fields.add(AlertConfigField.builder().field("endpoint").label("网关地址").type("text").required(true).placeholder("例如 https://sms-gateway.example.com/send").build());
        fields.add(AlertConfigField.builder().field("accessKey").label("Access Key").type("text").required(false).build());
        fields.add(AlertConfigField.builder().field("accessSecret").label("Access Secret").type("password").required(false).sensitive(true).build());
        fields.add(AlertConfigField.builder().field("signName").label("短信签名").type("text").required(false).build());
        fields.add(AlertConfigField.builder().field("templateCode").label("模板编码").type("text").required(false).build());
        fields.add(AlertConfigField.builder().field("phones").label("接收手机号").type("text_area").required(true).placeholder("多个手机号可用逗号或换行分隔").build());
        return fields;
    }

    @Override
    public NotifyResult send(NotifyContext context) {
        long start = System.currentTimeMillis();
        Map<String, Object> config = context.getConfig();
        try {
            AlertPluginConfigHelper.require(config, "endpoint", "phones");
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", context.getTitle());
            payload.put("content", context.getContent());
            payload.put("alertLevel", context.getAlertLevel());
            payload.put("phones", AlertPluginConfigHelper.getStringList(config, "phones"));
            payload.put("signName", AlertPluginConfigHelper.getString(config, "signName"));
            payload.put("templateCode", AlertPluginConfigHelper.getString(config, "templateCode"));
            payload.put("accessKey", AlertPluginConfigHelper.getString(config, "accessKey"));
            payload.put("accessSecret", AlertPluginConfigHelper.getString(config, "accessSecret"));

            HttpResponse response = HttpRequest.post(AlertPluginConfigHelper.getString(config, "endpoint"))
                .header("Content-Type", "application/json")
                .body(JSON.toJSONString(payload))
                .timeout(10000)
                .execute();
            String body = response.body();
            boolean success = response.getStatus() >= 200 && response.getStatus() < 300 && !StrUtil.containsIgnoreCase(body, "error");
            return NotifyResult.builder()
                .success(success)
                .requestPayload(JSON.toJSONString(payload))
                .responsePayload(body)
                .responseSummary(success ? "短信发送成功" : "短信发送失败")
                .errorMessage(success ? null : body)
                .costMs(System.currentTimeMillis() - start)
                .build();
        } catch (Exception ex) {
            return NotifyResult.builder()
                .success(false)
                .errorMessage(ex.getMessage())
                .responseSummary("短信发送失败")
                .costMs(System.currentTimeMillis() - start)
                .build();
        }
    }
}
