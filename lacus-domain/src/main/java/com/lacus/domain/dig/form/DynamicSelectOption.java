package com.lacus.domain.dig.form;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
public class DynamicSelectOption extends AbstractFormSelectOption {
    private String api;

    public DynamicSelectOption(@NonNull String api, @NonNull String label, @NonNull String field) {
        super(label, field);
        this.api = api;
    }
}
