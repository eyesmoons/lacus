package com.lacus.alert.plugin.notifier;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.net.URLEncodeUtil;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@AutoService(AlertNotifier.class)
public class DingTalkAlertNotifier implements AlertNotifier {

    @Override
    public String getTypeCode() {
        return "DINGTALK";
    }

    @Override
    public String getTypeName() {
        return "钉钉";
    }

    @Override
    public String getRemark() {
        return "钉钉机器人通知";
    }

    @Override
    public Integer getSortOrder() {
        return 20;
    }

    @Override
    public List<AlertConfigField> getConfigSchema() {
        List<AlertConfigField> fields = new ArrayList<>();
        fields.add(AlertConfigField.builder().field("webhook").label("机器人 Webhook").type("text").required(true).build());
        fields.add(AlertConfigField.builder().field("secret").label("加签密钥").type("password").required(false).sensitive(true).build());
        fields.add(AlertConfigField.builder().field("atMobiles").label("@手机号").type("text_area").required(false).placeholder("多个手机号可用逗号或换行分隔").build());
        fields.add(AlertConfigField.builder().field("isAtAll").label("@所有人").type("radio").defaultValue(false).options(booleanOptions()).build());
        return fields;
    }

    @Override
    public NotifyResult send(NotifyContext context) {
        long start = System.currentTimeMillis();
        Map<String, Object> config = context.getConfig();
        try {
            AlertPluginConfigHelper.require(config, "webhook");
            Map<String, Object> payload = new HashMap<>();
            payload.put("msgtype", "markdown");
            Map<String, Object> markdown = new HashMap<>();
            markdown.put("title", context.getTitle());
            markdown.put("text", buildMarkdown(context));
            payload.put("markdown", markdown);
            Map<String, Object> at = new HashMap<>();
            at.put("atMobiles", AlertPluginConfigHelper.getStringList(config, "atMobiles"));
            at.put("isAtAll", AlertPluginConfigHelper.getBoolean(config, "isAtAll", Boolean.FALSE));
            payload.put("at", at);

            String webhook = buildWebhook(config);
            HttpResponse response = HttpRequest.post(webhook)
                .header("Content-Type", "application/json")
                .body(JSON.toJSONString(payload))
                .timeout(10000)
                .execute();
            String body = response.body();
            boolean success = response.getStatus() >= 200 && response.getStatus() < 300 && StrUtil.contains(body, "\"errcode\":0");
            return NotifyResult.builder()
                .success(success)
                .requestPayload(JSON.toJSONString(payload))
                .responsePayload(body)
                .responseSummary(success ? "钉钉发送成功" : "钉钉发送失败")
                .errorMessage(success ? null : body)
                .costMs(System.currentTimeMillis() - start)
                .build();
        } catch (Exception ex) {
            return NotifyResult.builder()
                .success(false)
                .errorMessage(ex.getMessage())
                .responseSummary("钉钉发送失败")
                .costMs(System.currentTimeMillis() - start)
                .build();
        }
    }

    private String buildMarkdown(NotifyContext context) {
        return StrUtil.format("### {}\n\n> 级别：{}\n\n{}", context.getTitle(), StrUtil.blankToDefault(context.getAlertLevel(), "INFO"), context.getContent());
    }

    private String buildWebhook(Map<String, Object> config) throws Exception {
        String webhook = AlertPluginConfigHelper.getString(config, "webhook");
        String secret = AlertPluginConfigHelper.getString(config, "secret");
        if (StrUtil.isBlank(secret)) {
            return webhook;
        }
        long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String sign = URLEncodeUtil.encode(Base64.encode(mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8))));
        return webhook + (webhook.contains("?") ? "&" : "?") + "timestamp=" + timestamp + "&sign=" + sign;
    }

    private List<AlertConfigField.Option> booleanOptions() {
        List<AlertConfigField.Option> options = new ArrayList<>();
        options.add(AlertConfigField.Option.builder().label("是").value(true).build());
        options.add(AlertConfigField.Option.builder().label("否").value(false).build());
        return options;
    }
}
