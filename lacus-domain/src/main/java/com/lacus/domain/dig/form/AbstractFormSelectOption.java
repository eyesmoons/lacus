package com.lacus.domain.dig.form;

import lombok.Getter;
import lombok.NonNull;
import org.apache.seatunnel.shade.com.fasterxml.jackson.annotation.JsonProperty;

@Getter
public abstract class AbstractFormSelectOption extends AbstractFormOption {

    @JsonProperty("type")
    private final FormType formType = FormType.SELECT;

    public AbstractFormSelectOption(@NonNull String label, @NonNull String field) {
        super(label, field);
    }

    @Getter
    public static class SelectOption {
        @JsonProperty
        private String label;

        @JsonProperty
        private Object value;

        public SelectOption(@NonNull String label, @NonNull Object value) {
            this.label = label;
            this.value = value;
        }
    }
}
