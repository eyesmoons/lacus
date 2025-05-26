package com.lacus.domain.dig.form;

import com.lacus.common.exception.CustomException;
import com.lacus.domain.dig.form.validate.AbstractValidate;
import com.lacus.domain.dig.form.validate.MutuallyExclusiveValidate;
import com.lacus.domain.dig.form.validate.UnionNonEmptyValidate;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FormStructureValidate {

    public static void validateFormStructure(@NonNull FormStructure formStructure)
            throws CustomException {

        List<String> apiErrorList = validateApiOption(formStructure);
        List<String> localeErrorList = validateLocaleOption(formStructure);
        List<String> showErrorList = validateShow(formStructure);
        List<String> unionNonErrorList = validateUnionNonEmpty(formStructure);
        List<String> exclusiveErrorList = validateMutuallyExclusive(formStructure);
        List<String> duplicateFormItemErrorList = validateDuplicateFormItem(formStructure);

        apiErrorList.addAll(localeErrorList);
        apiErrorList.addAll(showErrorList);
        apiErrorList.addAll(unionNonErrorList);
        apiErrorList.addAll(exclusiveErrorList);
        apiErrorList.addAll(duplicateFormItemErrorList);

        if (!apiErrorList.isEmpty()) {
            throw new CustomException(formStructure.getName(), (Throwable) apiErrorList);
        }
    }

    private static List<String> validateApiOption(@NonNull FormStructure formStructure) {
        List<String> errorMessageList = new ArrayList();
        Map<String, Map<String, String>> apis = formStructure.getApis();
        formStructure
                .getForms()
                .forEach(
                        formOption -> {
                            if (formOption instanceof DynamicSelectOption) {
                                String api = ((DynamicSelectOption) formOption).getApi();
                                if (apis == null || !apis.containsKey(api)) {
                                    errorMessageList.add(
                                            String.format(
                                                    "DynamicSelectOption[%s] used api[%s] can not found in FormStructure.apis",
                                                    ((DynamicSelectOption) formOption).getLabel(),
                                                    api));
                                }
                            }
                        });
        return errorMessageList;
    }

    private static List<String> validateLocaleOption(@NonNull FormStructure formStructure) {
        List<String> errorMessageList = new ArrayList();
        FormLocale locales = formStructure.getLocales();
        formStructure
                .getForms()
                .forEach(
                        formOption -> {
                            if (formOption.getLabel().startsWith(FormLocale.I18N_PREFIX)) {
                                String labelName =
                                        formOption.getLabel().replace(FormLocale.I18N_PREFIX, "");
                                validateOneI18nOption(
                                        locales,
                                        formOption.getLabel(),
                                        "label",
                                        labelName,
                                        errorMessageList);
                            }

                            if (formOption.getDescription().startsWith(FormLocale.I18N_PREFIX)) {
                                String description =
                                        formOption
                                                .getDescription()
                                                .replace(FormLocale.I18N_PREFIX, "");
                                validateOneI18nOption(
                                        locales,
                                        formOption.getLabel(),
                                        "description",
                                        description,
                                        errorMessageList);
                            }

                            if (formOption.getPlaceholder().startsWith(FormLocale.I18N_PREFIX)) {
                                String placeholder =
                                        formOption
                                                .getPlaceholder()
                                                .replace(FormLocale.I18N_PREFIX, "");
                                validateOneI18nOption(
                                        locales,
                                        formOption.getLabel(),
                                        "placeholder",
                                        placeholder,
                                        errorMessageList);
                            }

                            AbstractValidate validate = formOption.getValidate();
                            if (validate != null
                                    && validate.getMessage().startsWith(FormLocale.I18N_PREFIX)) {
                                String message =
                                        validate.getMessage().replace(FormLocale.I18N_PREFIX, "");
                                validateOneI18nOption(
                                        locales,
                                        formOption.getLabel(),
                                        "validateMessage",
                                        message,
                                        errorMessageList);
                            }
                        });
        return errorMessageList;
    }

    private static void validateOneI18nOption(
            FormLocale locale,
            @NonNull String formOptionLabel,
            @NonNull String formOptionName,
            @NonNull String key,
            @NonNull List<String> errorMessageList) {
        if (locale == null || !locale.getEnUS().containsKey(key)) {
            errorMessageList.add(
                    String.format(
                            "FormOption[%s] used i18n %s[%s] can not found in FormStructure.locales en_US",
                            formOptionLabel, formOptionName, key));
        }

        if (locale == null || !locale.getZhCN().containsKey(key)) {
            errorMessageList.add(
                    String.format(
                            "FormOption[%s] used i18n %s[%s] can not found in FormStructure.locales zh_CN",
                            formOptionLabel, formOptionName, key));
        }
    }

    private static List<String> validateShow(@NonNull FormStructure formStructure) {
        List<String> errorMessageList = new ArrayList<>();
        List<String> allFields =
                formStructure.getForms().stream()
                        .map(AbstractFormOption::getField)
                        .collect(Collectors.toList());
        formStructure
                .getForms()
                .forEach(
                        formOption -> {
                            Map show = formOption.getShow();
                            if (show == null) {
                                return;
                            }

                            String field = show.get("field").toString();
                            if (!allFields.contains(field)) {
                                errorMessageList.add(
                                        String.format(
                                                "FormOption[%s] used show field[%s] can not found in form options",
                                                formOption.getLabel(), field));
                            }
                        });

        return errorMessageList;
    }

    private static List<String> validateUnionNonEmpty(@NonNull FormStructure formStructure) {
        List<String> errorMessageList = new ArrayList();
        Map<String, List<String>> unionMap = new HashMap<>();
        formStructure
                .getForms()
                .forEach(
                        formOption -> {
                            if (formOption.getValidate() != null
                                    && formOption.getValidate() instanceof UnionNonEmptyValidate) {
                                unionMap.put(
                                        formOption.getField(),
                                        ((UnionNonEmptyValidate) formOption.getValidate())
                                                .getFields());
                            }
                        });

        unionMap.forEach(
                (k, v) -> {
                    if (v == null || !v.contains(k)) {
                        errorMessageList.add(
                                String.format(
                                        "UnionNonEmptyValidate Option field[%s] must in validate union field list",
                                        k));
                    }

                    if (v != null) {
                        v.forEach(
                                field -> {
                                    if (!unionMap.containsKey(field)) {
                                        errorMessageList.add(
                                                String.format(
                                                        "UnionNonEmptyValidate Option field[%s] , validate union field[%s] can not found in form options",
                                                        k, field));
                                    }
                                });
                    }
                });

        return errorMessageList;
    }

    private static List<String> validateMutuallyExclusive(@NonNull FormStructure formStructure) {
        List<String> errorMessageList = new ArrayList<>();
        Map<String, List<String>> exclusiveMap = new HashMap<>();
        formStructure
                .getForms()
                .forEach(
                        formOption -> {
                            if (formOption.getValidate() != null
                                    && formOption.getValidate()
                                    instanceof MutuallyExclusiveValidate) {
                                exclusiveMap.put(
                                        formOption.getField(),
                                        ((MutuallyExclusiveValidate) formOption.getValidate())
                                                .getFields());
                            }
                        });

        exclusiveMap.forEach(
                (k, v) -> {
                    if (v == null || !v.contains(k)) {
                        errorMessageList.add(
                                String.format(
                                        "MutuallyExclusiveValidate Option field[%s] must in validate field list",
                                        k));
                    }

                    if (v != null) {
                        v.forEach(
                                field -> {
                                    if (!exclusiveMap.containsKey(field)) {
                                        errorMessageList.add(
                                                String.format(
                                                        "MutuallyExclusiveValidate Option field[%s] , validate field[%s] can not found in form options",
                                                        k, field));
                                    }
                                });
                    }
                });

        return errorMessageList;
    }

    public static List<String> validateDuplicateFormItem(@NonNull FormStructure formStructure) {
        List<String> fieldList = new ArrayList<>();
        List<String> errorFieldList = new ArrayList<>();
        List<String> errorMessage = new ArrayList<>();
        formStructure
                .getForms()
                .forEach(
                        form -> {
                            if (fieldList.contains(form.getField())) {
                                errorFieldList.add(form.getField());
                            } else {
                                fieldList.add(form.getField());
                            }
                        });
        if (!errorFieldList.isEmpty()) {
            errorMessage.add(
                    String.format(
                            "DuplicateFormItemValidate failed, Duplicate form items %s",
                            errorFieldList));
        }
        return errorMessage;
    }
}
