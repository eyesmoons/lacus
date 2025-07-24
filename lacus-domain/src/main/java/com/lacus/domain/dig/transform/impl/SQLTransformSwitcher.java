package com.lacus.domain.dig.transform.impl;

import com.google.auto.service.AutoService;
import com.lacus.domain.dig.dto.DatabaseTableSchema;
import com.lacus.domain.dig.dto.SourceFieldsConfig;
import com.lacus.domain.dig.form.FormStructure;
import com.lacus.domain.dig.transform.SQLTransformOptions;
import com.lacus.domain.dig.transform.Transform;
import com.lacus.domain.dig.transform.TransformConfigSwitcher;
import com.lacus.domain.dig.transform.TransformOptions;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.shade.com.typesafe.config.Config;
import org.apache.seatunnel.shade.com.typesafe.config.ConfigValueFactory;

@AutoService(TransformConfigSwitcher.class)
public class SQLTransformSwitcher implements TransformConfigSwitcher {
    @Override
    public Transform getTransform() {
        return Transform.SQL;
    }

    @Override
    public FormStructure getFormStructure(OptionRule transformOptionRule) {
        return null;
    }

    @Override
    public Config mergeTransformConfig(
            Config transformConfig, TransformOptions transformOption, SourceFieldsConfig inputSchema) {

        SQLTransformOptions sqlTransformOptions = (SQLTransformOptions) transformOption;

        return transformConfig.withValue(
                "query", ConfigValueFactory.fromAnyRef(sqlTransformOptions.getSql().getQuery()));
    }
}
