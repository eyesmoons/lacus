package com.lacus.domain.dig.transform.impl;

import com.google.auto.service.AutoService;
import com.lacus.common.exception.CustomException;
import com.lacus.domain.dig.dto.DatabaseTableSchema;
import com.lacus.domain.dig.dto.SourceFieldsConfig;
import com.lacus.domain.dig.dto.TableField;
import com.lacus.domain.dig.form.FormStructure;
import com.lacus.domain.dig.transform.ChangeOrder;
import com.lacus.domain.dig.transform.DeleteField;
import com.lacus.domain.dig.transform.FieldMapperTransformOptions;
import com.lacus.domain.dig.transform.RenameField;
import com.lacus.domain.dig.transform.Transform;
import com.lacus.domain.dig.transform.TransformConfigSwitcher;
import com.lacus.domain.dig.transform.TransformConfigSwitcherUtils;
import com.lacus.domain.dig.transform.TransformOptions;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.shade.com.typesafe.config.Config;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AutoService(TransformConfigSwitcher.class)
public class FieldMapperTransformSwitcher implements TransformConfigSwitcher {
    @Override
    public Transform getTransform() {
        return Transform.FIELDMAPPER;
    }

    @Override
    public FormStructure getFormStructure(OptionRule transformOptionRule) {
        return null;
    }

    @Override
    public Config mergeTransformConfig(
            Config transformConfig, TransformOptions transformOption, SourceFieldsConfig inputSchema) {

        LinkedHashMap<String, String> fieldsMap =
                inputSchema.getTableFields().stream()
                        .map(TableField::getName)
                        .collect(
                                Collectors.toMap(
                                        key -> key,
                                        key -> key,
                                        (v1, v2) -> v1,
                                        LinkedHashMap::new));

        FieldMapperTransformOptions fieldMapperTransformOptions = (FieldMapperTransformOptions) transformOption;
        List<DeleteField> deleteFields = fieldMapperTransformOptions.getDeleteFields();
        List<RenameField> renameFields = fieldMapperTransformOptions.getRenameFields();
        List<ChangeOrder> changeOrders = fieldMapperTransformOptions.getChangeOrders();

        for (RenameField renameField : renameFields) {
            if (!fieldsMap.containsKey(renameField.getSourceFieldName())) {
                throw new CustomException("FieldMapperTransformSwitcher renameFields sourceFieldName not exist");
            }
            fieldsMap.put(renameField.getSourceFieldName(), renameField.getTargetName());
        }

        for (DeleteField deleteField : deleteFields) {
            if (!fieldsMap.containsKey(deleteField.getSourceFieldName())) {
                throw new CustomException("FieldMapperTransformSwitcher deleteFields sourceFieldName not exist");
            }
            fieldsMap.remove(deleteField.getSourceFieldName());
        }

        for (ChangeOrder changeOrder : changeOrders) {
            if (!fieldsMap.containsKey(changeOrder.getSourceFieldName())) {
                throw new CustomException("FieldMapperTransformSwitcher changeOrders sourceFieldName not exist");
            }
            fieldsMap =
                    reorderLinkedHashMap(
                            fieldsMap, changeOrder.getSourceFieldName(), changeOrder.getIndex());
        }

        return transformConfig.withValue(
                "field_mapper", TransformConfigSwitcherUtils.getOrderedConfigForLinkedHashMap(fieldsMap).root());
    }

    public static LinkedHashMap<String, String> reorderLinkedHashMap(
            LinkedHashMap<String, String> map, String key, int index) {
        if (map == null || !map.containsKey(key) || index < 0 || index >= map.size()) {
            return map;
        }

        LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();
        int currentIndex = 0;

        // Insert the specified key at the specified index
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (currentIndex == index) {
                resultMap.put(key, map.get(key));
            }

            if (!entry.getKey().equals(key)) {
                resultMap.put(entry.getKey(), entry.getValue());
                currentIndex++;
            }
        }

        // Handle the case when the specified index is equal to the map size
        if (index == map.size() - 1) {
            resultMap.put(key, map.get(key));
        }

        return resultMap;
    }
}
