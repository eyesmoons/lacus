package com.lacus.domain.dig.form;

import lombok.Getter;
import lombok.NonNull;
import org.apache.seatunnel.shade.com.fasterxml.jackson.annotation.JsonProperty;

@Getter
public class FormInputOption extends AbstractFormOption {
    @JsonProperty("type")
    private final FormType formType = FormType.INPUT;

    private final InputType inputType;

    public FormInputOption(
            @NonNull InputType inputType, @NonNull String label, @NonNull String field) {
        super(label, field);
        this.inputType = inputType;
    }

    @Getter
    public enum InputType {
        @JsonProperty("text")
        TEXT("text"),

        @JsonProperty("password")
        PASSWORD("password"),

        @JsonProperty("textarea")
        TEXTAREA("textarea");

        private String inputType;

        InputType(String inputType) {
            this.inputType = inputType;
        }
    }
}
