package com.lacus.domain.dig.form;

import lombok.Data;
import lombok.NonNull;
import org.apache.seatunnel.shade.com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.seatunnel.shade.com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;

@Data
public class FormLocale {
    @JsonIgnore public static final String I18N_PREFIX = "i18n.";

    @JsonProperty("zh_CN")
    private LinkedHashMap<String, String> zhCN = new LinkedHashMap<>();

    @JsonProperty("en_US")
    private LinkedHashMap<String, String> enUS = new LinkedHashMap<>();

    public FormLocale addZhCN(@NonNull String key, @NonNull String value) {
        zhCN.put(key, value);
        return this;
    }

    public FormLocale addEnUS(@NonNull String key, @NonNull String value) {
        enUS.put(key, value);
        return this;
    }
}
