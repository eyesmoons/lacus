package com.lacus.domain.dig.form;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class StaticSelectOption extends AbstractFormSelectOption {

    private List<SelectOption> options;

    public StaticSelectOption(
            @NonNull List<SelectOption> options, @NonNull String label, @NonNull String field) {
        super(label, field);
        this.options = options;
    }
}
