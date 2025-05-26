package com.lacus.domain.dig.form;

import lombok.Data;
import lombok.NonNull;
import org.apache.seatunnel.shade.com.fasterxml.jackson.annotation.JsonIgnoreType;
import org.apache.seatunnel.shade.com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@Data
public class FormStructure {
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private FormLocale locales;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Map<String, String>> apis;

    private List<AbstractFormOption> forms;

    public FormStructure() {}

    public FormStructure(
            @NonNull String name,
            @NonNull List<AbstractFormOption> formOptionList,
            FormLocale locale,
            Map<String, Map<String, String>> apis) {
        this.name = name;
        this.forms = formOptionList;
        this.locales = locale;
        this.apis = apis;
    }

    @JsonIgnoreType
    public enum HttpMethod {
        GET,

        POST
    }

    public static FormStructureBuilder builder() {
        return new FormStructureBuilder();
    }
}
