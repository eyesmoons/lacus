package com.lacus.domain.dig.transform.impl;

import com.google.auto.service.AutoService;
import com.lacus.domain.dig.dto.DatabaseTableSchema;
import com.lacus.domain.dig.dto.SourceFieldsConfig;
import com.lacus.domain.dig.form.FormStructure;
import com.lacus.domain.dig.transform.Copy;
import com.lacus.domain.dig.transform.CopyTransformOptions;
import com.lacus.domain.dig.transform.Transform;
import com.lacus.domain.dig.transform.TransformConfigSwitcher;
import com.lacus.domain.dig.transform.TransformConfigSwitcherUtils;
import com.lacus.domain.dig.transform.TransformOptions;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.shade.com.typesafe.config.Config;
import org.apache.seatunnel.shade.com.typesafe.config.ConfigFactory;

import java.util.LinkedHashMap;
import java.util.Map;

@AutoService(TransformConfigSwitcher.class)
public class CopyTransformSwitcher implements TransformConfigSwitcher {
    @Override
    public Transform getTransform() {
        return Transform.COPY;
    }

    @Override
    public FormStructure getFormStructure(OptionRule transformOptionRule) {
        return null;
    }

    @Override
    public Config mergeTransformConfig(
            Config transformConfig, TransformOptions transformOption, SourceFieldsConfig inputSchema) {

        CopyTransformOptions copyTransformOptions = (CopyTransformOptions) transformOption;

        LinkedHashMap<String, String> fields = new LinkedHashMap<>();
        for (Copy copy : copyTransformOptions.getCopyList()) {
            fields.put(copy.getTargetFieldName(), copy.getSourceFieldName());
        }

        return transformConfig.withValue("fields", TransformConfigSwitcherUtils.getOrderedConfigForLinkedHashMap(fields).root());
    }
}
