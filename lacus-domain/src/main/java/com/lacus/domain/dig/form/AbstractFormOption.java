package com.lacus.domain.dig.form;

import com.lacus.domain.dig.form.validate.AbstractValidate;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.apache.seatunnel.shade.com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.seatunnel.shade.com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public abstract class AbstractFormOption<T extends AbstractFormOption, V extends AbstractValidate> {

    // support i18n
    private final String label;
    private final String field;
    private Object defaultValue;

    // support i18n
    private String description = "";
    private boolean clearable;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> show;

    // support i18n
    private String placeholder = "";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private V validate;

    public AbstractFormOption(@NonNull String label, @NonNull String field) {
        this.label = label;
        this.field = field;
    }

    public enum FormType {
        @JsonProperty("input")
        INPUT("input"),

        @JsonProperty("select")
        SELECT("select");

        @Getter private String formType;

        FormType(String formType) {
            this.formType = formType;
        }
    }

    public T withShow(@NonNull String field, @NonNull List<Object> values) {
        if (this.show == null) {
            this.show = new HashMap<>();
        }

        Set<Object> valueSet = new HashSet<>(values);
        this.show.put(Constants.SHOW_FIELD, field);
        this.show.put(Constants.SHOW_VALUE, valueSet);
        return (T) this;
    }

    public T withValidate(@NonNull V validate) {
        this.validate = validate;
        return (T) this;
    }

    public T withDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return (T) this;
    }

    public T withDescription(@NonNull String description) {
        this.description = description;
        return (T) this;
    }

    public T withI18nDescription(@NonNull String description) {
        this.description = FormLocale.I18N_PREFIX + description;
        return (T) this;
    }

    public T withClearable() {
        this.clearable = true;
        return (T) this;
    }

    public T withPlaceholder(@NonNull String placeholder) {
        this.placeholder = placeholder;
        return (T) this;
    }

    public T withI18nPlaceholder(@NonNull String placeholder) {
        this.placeholder = FormLocale.I18N_PREFIX + placeholder;
        return (T) this;
    }
}
