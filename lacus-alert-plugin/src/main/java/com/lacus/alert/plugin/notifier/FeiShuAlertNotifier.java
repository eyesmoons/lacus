package com.lacus.alert.plugin.notifier;

import cn.hutool.core.codec.Base64;
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
public class FeiShuAlertNotifier implements AlertNotifier {

    @Override
    public String getTypeCode() {
        return "FEISHU";
    }

    @Override
    public String getTypeName() {
        return "飞书";
    }

    @Override
    public String getRemark() {
        return "飞书机器人通知";
    }

    @Override
    public Integer getSortOrder() {
        return 40;
    }

    @Override
    public List<AlertConfigField> getConfigSchema() {
        List<AlertConfigField> fields = new ArrayList<>();
        fields.add(AlertConfigField.builder().field("webhook").label("机器人 Webhook").type("text").required(true).build());
        fields.add(AlertConfigField.builder().field("secret").label("加签密钥").type("password").required(false).sensitive(true).build());
        return fields;
    }

    @Override
    public NotifyResult send(NotifyContext context) {
        long start = System.currentTimeMillis();
        Map<String, Object> config = context.getConfig();
        try {
            AlertPluginConfigHelper.require(config, "webhook");
            Map<String, Object> payload = new HashMap<>();
            payload.put("msg_type", "post");
            Map<String, Object> zhCn = new HashMap<>();
            zhCn.put("title", context.getTitle());
            List<List<Map<String, String>>> content = new ArrayList<>();
            List<Map<String, String>> line = new ArrayList<>();
            line.add(textNode(StrUtil.format("级别：{}", StrUtil.blankToDefault(context.getAlertLevel(), "INFO"))));
            content.add(line);
            List<Map<String, String>> line2 = new ArrayList<>();
            line2.add(textNode(context.getContent()));
            content.add(line2);
            zhCn.put("content", content);
            Map<String, Object> post = new HashMap<>();
            Map<String, Object> inner = new HashMap<>();
            inner.put("zh_cn", zhCn);
            post.put("post", inner);
            payload.put("content", post);
            appendSign(payload, config);

            HttpResponse response = HttpRequest.post(AlertPluginConfigHelper.getString(config, "webhook"))
                .header("Content-Type", "application/json")
                .body(JSON.toJSONString(payload))
                .timeout(10000)
                .execute();
            String body = response.body();
            boolean success = response.getStatus() >= 200 && response.getStatus() < 300
                && (StrUtil.contains(body, "\"code\":0") || StrUtil.contains(body, "\"StatusCode\":0"));
            return NotifyResult.builder()
                .success(success)
                .requestPayload(JSON.toJSONString(payload))
                .responsePayload(body)
                .responseSummary(success ? "飞书发送成功" : "飞书发送失败")
                .errorMessage(success ? null : body)
                .costMs(System.currentTimeMillis() - start)
                .build();
        } catch (Exception ex) {
            return NotifyResult.builder()
                .success(false)
                .errorMessage(ex.getMessage())
                .responseSummary("飞书发送失败")
                .costMs(System.currentTimeMillis() - start)
                .build();
        }
    }

    private Map<String, String> textNode(String text) {
        Map<String, String> node = new HashMap<>();
        node.put("tag", "text");
        node.put("text", text);
        return node;
    }

    private void appendSign(Map<String, Object> payload, Map<String, Object> config) throws Exception {
        String secret = AlertPluginConfigHelper.getString(config, "secret");
        if (StrUtil.isBlank(secret)) {
            return;
        }
        long timestamp = System.currentTimeMillis() / 1000;
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String sign = Base64.encode(mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8)));
        payload.put("timestamp", String.valueOf(timestamp));
        payload.put("sign", sign);
    }
}
