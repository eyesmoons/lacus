package com.lacus.domain.dig.transform.impl;

import com.google.auto.service.AutoService;
import com.lacus.domain.dig.dto.DatabaseTableSchema;
import com.lacus.domain.dig.dto.SourceFieldsConfig;
import com.lacus.domain.dig.form.FormStructure;
import com.lacus.domain.dig.transform.Split;
import com.lacus.domain.dig.transform.SplitTransformOptions;
import com.lacus.domain.dig.transform.Transform;
import com.lacus.domain.dig.transform.TransformConfigSwitcher;
import com.lacus.domain.dig.transform.TransformOptions;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.shade.com.typesafe.config.Config;
import org.apache.seatunnel.shade.com.typesafe.config.ConfigValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@AutoService(TransformConfigSwitcher.class)
public class SplitTransformSwitcher implements TransformConfigSwitcher {
    @Override
    public Transform getTransform() {
        return Transform.MULTIFIELDSPLIT;
    }

    @Override
    public FormStructure getFormStructure(OptionRule transformOptionRule) {
        return null;
    }

    @Override
    public Config mergeTransformConfig(
            Config transformConfig, TransformOptions transformOption, SourceFieldsConfig inputSchema) {

        SplitTransformOptions splitTransformOptions = (SplitTransformOptions) transformOption;

        checkArgument(
                !splitTransformOptions.getSplits().isEmpty(),
                "SplitTransformSwitcher splits must be greater than 0");

        List<Map<String, Object>> splitOPs = new ArrayList<>();

        for (Split split : splitTransformOptions.getSplits()) {
            Map<String, Object> splitOP = new HashMap<>();
            splitOP.put("separator", split.getSeparator());
            splitOP.put("split_field", split.getSourceFieldName());
            splitOP.put("output_fields", ConfigValueFactory.fromIterable(split.getOutputFields()));
            splitOPs.add(splitOP);
        }

        return transformConfig.withValue("splitOPs", ConfigValueFactory.fromIterable(splitOPs));
    }
}
