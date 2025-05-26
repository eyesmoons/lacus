package com.lacus.domain.dig.form;

import lombok.NonNull;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

public class FormOptionBuilder {

    private String label;

    private String field;

    public static FormOptionBuilder builder() {
        return new FormOptionBuilder();
    }

    public FormOptionBuilder withLabel(@NonNull String label) {
        this.label = label;
        return this;
    }

    public FormOptionBuilder withI18nLabel(@NonNull String label) {
        this.label = FormLocale.I18N_PREFIX + label;
        return this;
    }

    public FormOptionBuilder withField(@NonNull String field) {
        this.field = field;
        return this;
    }

    public InputOptionBuilder inputOptionBuilder() {
        return new InputOptionBuilder(label, field);
    }

    public DynamicSelectOptionBuilder dynamicSelectOptionBuilder() {
        return new DynamicSelectOptionBuilder(label, field);
    }

    public StaticSelectOptionBuilder staticSelectOptionBuilder() {
        return new StaticSelectOptionBuilder(label, field);
    }

    public static class InputOptionBuilder {
        private String label;

        private String field;

        public InputOptionBuilder(@NonNull String label, @NonNull String field) {
            this.label = label;
            this.field = field;
        }

        public FormInputOption formTextInputOption() {
            return new FormInputOption(FormInputOption.InputType.TEXT, label, field);
        }

        public FormInputOption formPasswordInputOption() {
            return new FormInputOption(FormInputOption.InputType.PASSWORD, label, field);
        }

        public FormInputOption formTextareaInputOption() {
            return new FormInputOption(FormInputOption.InputType.TEXTAREA, label, field);
        }
    }

    public static class DynamicSelectOptionBuilder {
        private String label;

        private String field;

        private String selectApi;

        public DynamicSelectOptionBuilder(@NonNull String label, @NonNull String field) {
            this.label = label;
            this.field = field;
        }

        public DynamicSelectOptionBuilder withSelectApi(@NonNull String selectApi) {
            this.selectApi = selectApi;
            return this;
        }

        public DynamicSelectOption formDynamicSelectOption() {
            return new DynamicSelectOption(selectApi, label, field);
        }
    }

    public static class StaticSelectOptionBuilder {
        private String label;

        private String field;

        private List<AbstractFormSelectOption.SelectOption> options = new ArrayList<>();

        public StaticSelectOptionBuilder(@NonNull String label, @NonNull String field) {
            this.label = label;
            this.field = field;
        }

        public StaticSelectOptionBuilder addSelectOptions(
                @NonNull List<ImmutablePair> selectOptions) {
            for (ImmutablePair option : selectOptions) {
                options.add(
                        new AbstractFormSelectOption.SelectOption(
                                option.left.toString(), option.right.toString()));
            }
            return this;
        }

        public StaticSelectOptionBuilder addI18nSelectOptions(
                @NonNull List<ImmutablePair> selectOptions) {
            for (ImmutablePair option : selectOptions) {
                options.add(
                        new AbstractFormSelectOption.SelectOption(
                                FormLocale.I18N_PREFIX + option.left.toString(),
                                option.right.toString()));
            }
            return this;
        }

        public StaticSelectOptionBuilder addSelectOptions(@NonNull ImmutablePair... selectOptions) {
            for (ImmutablePair option : selectOptions) {
                options.add(
                        new AbstractFormSelectOption.SelectOption(
                                option.left.toString(), option.right.toString()));
            }
            return this;
        }

        public StaticSelectOptionBuilder addI18nSelectOptions(
                @NonNull ImmutablePair... selectOptions) {
            for (ImmutablePair option : selectOptions) {
                options.add(
                        new AbstractFormSelectOption.SelectOption(
                                FormLocale.I18N_PREFIX + option.left.toString(),
                                option.right.toString()));
            }
            return this;
        }

        public StaticSelectOption formStaticSelectOption() {
            return new StaticSelectOption(options, label, field);
        }
    }
}
