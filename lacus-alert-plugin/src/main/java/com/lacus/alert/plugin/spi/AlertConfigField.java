package com.lacus.alert.plugin.spi;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertConfigField {

    private String field;

    private String label;

    private String type;

    private Boolean required;

    private String placeholder;

    private Object defaultValue;

    private Boolean sensitive;

    private List<Option> options;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Option {
        private String label;
        private Object value;
    }
}
