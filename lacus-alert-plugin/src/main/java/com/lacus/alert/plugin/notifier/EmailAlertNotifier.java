package com.lacus.alert.plugin.notifier;

import cn.hutool.core.util.StrUtil;
import com.google.auto.service.AutoService;
import com.lacus.alert.plugin.spi.AlertConfigField;
import com.lacus.alert.plugin.spi.AlertNotifier;
import com.lacus.alert.plugin.spi.NotifyContext;
import com.lacus.alert.plugin.spi.NotifyResult;
import com.lacus.alert.plugin.support.AlertPluginConfigHelper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

@AutoService(AlertNotifier.class)
public class EmailAlertNotifier implements AlertNotifier {

    @Override
    public String getTypeCode() {
        return "EMAIL";
    }

    @Override
    public String getTypeName() {
        return "邮件";
    }

    @Override
    public String getRemark() {
        return "SMTP 邮件通知";
    }

    @Override
    public Integer getSortOrder() {
        return 10;
    }

    @Override
    public List<AlertConfigField> getConfigSchema() {
        List<AlertConfigField> fields = new ArrayList<>();
        fields.add(AlertConfigField.builder().field("host").label("SMTP Host").type("text").required(true).placeholder("例如 smtp.qq.com").build());
        fields.add(AlertConfigField.builder().field("port").label("SMTP Port").type("number").required(true).defaultValue(465).build());
        fields.add(AlertConfigField.builder().field("username").label("用户名").type("text").required(true).build());
        fields.add(AlertConfigField.builder().field("password").label("密码/授权码").type("password").required(true).sensitive(true).build());
        fields.add(AlertConfigField.builder().field("from").label("发件人邮箱").type("text").required(true).build());
        fields.add(AlertConfigField.builder().field("to").label("收件人").type("text_area").required(true).placeholder("多个邮箱可用逗号或换行分隔").build());
        fields.add(AlertConfigField.builder().field("ssl").label("启用 SSL").type("radio").defaultValue(true)
            .options(buildBooleanOptions()).build());
        fields.add(AlertConfigField.builder().field("starttls").label("启用 STARTTLS").type("radio").defaultValue(false)
            .options(buildBooleanOptions()).build());
        fields.add(AlertConfigField.builder().field("html").label("HTML 内容").type("radio").defaultValue(false)
            .options(buildBooleanOptions()).build());
        return fields;
    }

    @Override
    public NotifyResult send(NotifyContext context) {
        long start = System.currentTimeMillis();
        Map<String, Object> config = context.getConfig();
        try {
            AlertPluginConfigHelper.require(config, "host", "port", "username", "password", "from", "to");
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost(AlertPluginConfigHelper.getString(config, "host"));
            sender.setPort(AlertPluginConfigHelper.getInt(config, "port", 465));
            sender.setUsername(AlertPluginConfigHelper.getString(config, "username"));
            sender.setPassword(AlertPluginConfigHelper.getString(config, "password"));
            sender.setDefaultEncoding(StandardCharsets.UTF_8.name());
            Properties properties = sender.getJavaMailProperties();
            properties.put("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.ssl.enable", String.valueOf(AlertPluginConfigHelper.getBoolean(config, "ssl", Boolean.TRUE)));
            properties.put("mail.smtp.starttls.enable", String.valueOf(AlertPluginConfigHelper.getBoolean(config, "starttls", Boolean.FALSE)));
            properties.put("mail.smtp.timeout", "10000");
            properties.put("mail.smtp.connectiontimeout", "10000");
            properties.put("mail.smtp.writetimeout", "10000");

            javax.mail.internet.MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(AlertPluginConfigHelper.getString(config, "from"));
            helper.setTo(AlertPluginConfigHelper.getStringList(config, "to").toArray(new String[0]));
            helper.setSubject(buildSubject(context));
            helper.setText(context.getContent(), AlertPluginConfigHelper.getBoolean(config, "html", Boolean.FALSE));
            sender.send(message);
            return NotifyResult.builder()
                .success(true)
                .requestPayload("subject=" + buildSubject(context) + ";to=" + StrUtil.join(",", AlertPluginConfigHelper.getStringList(config, "to")))
                .responseSummary("SMTP 发送成功")
                .costMs(System.currentTimeMillis() - start)
                .build();
        } catch (Exception ex) {
            return NotifyResult.builder()
                .success(false)
                .errorMessage(ex.getMessage())
                .responseSummary("SMTP 发送失败")
                .costMs(System.currentTimeMillis() - start)
                .build();
        }
    }

    private String buildSubject(NotifyContext context) {
        return StrUtil.format("[{}] {}", StrUtil.blankToDefault(context.getAlertLevel(), "INFO"), context.getTitle());
    }

    private List<AlertConfigField.Option> buildBooleanOptions() {
        List<AlertConfigField.Option> options = new ArrayList<>();
        options.add(AlertConfigField.Option.builder().label("是").value(true).build());
        options.add(AlertConfigField.Option.builder().label("否").value(false).build());
        return options;
    }
}
