package com.lacus.domain.dig.form;

import com.lacus.common.exception.CustomException;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormStructureBuilder {
    private String name;

    private final List<AbstractFormOption> forms = new ArrayList<>();

    private FormLocale locales;

    private Map<String, Map<String, String>> apis;

    public FormStructureBuilder name(@NonNull String name) {
        this.name = name;
        return this;
    }

    public FormStructureBuilder addFormOption(@NonNull AbstractFormOption... formOptions) {
        Collections.addAll(forms, formOptions);
        return this;
    }

    public FormStructureBuilder withLocale(FormLocale locale) {
        this.locales = locale;
        return this;
    }

    public FormStructureBuilder addApi(
            @NonNull String apiName,
            @NonNull String url,
            @NonNull FormStructure.HttpMethod method) {
        if (apis == null) {
            apis = new HashMap<>();
        }
        apis.putIfAbsent(apiName, new HashMap<>());
        apis.get(apiName).put("url", url);
        apis.get(apiName).put("method", method.name().toLowerCase(java.util.Locale.ROOT));

        return this;
    }

    public FormStructure build() throws CustomException {
        FormStructure formStructure = new FormStructure(name, forms, locales, apis);
        FormStructureValidate.validateFormStructure(formStructure);
        return formStructure;
    }
}
