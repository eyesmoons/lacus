package com.lacus.domain.dig.transform.impl;

import com.google.auto.service.AutoService;
import com.lacus.domain.dig.dto.DatabaseTableSchema;
import com.lacus.domain.dig.dto.SourceFieldsConfig;
import com.lacus.domain.dig.form.FormStructure;
import com.lacus.domain.dig.helper.SeaTunnelOptionRuleWrapper;
import com.lacus.domain.dig.transform.Transform;
import com.lacus.domain.dig.transform.TransformConfigSwitcher;
import com.lacus.domain.dig.transform.TransformOptions;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.shade.com.typesafe.config.Config;

@AutoService(TransformConfigSwitcher.class)
public class ReplaceTransformSwitcher implements TransformConfigSwitcher {
    @Override
    public Transform getTransform() {
        return Transform.REPLACE;
    }

    @Override
    public FormStructure getFormStructure(OptionRule transformOptionRule) {
        return SeaTunnelOptionRuleWrapper.wrapper(transformOptionRule, this.getTransform().name());
    }

    @Override
    public Config mergeTransformConfig(
            Config transformConfig, TransformOptions transformOption, SourceFieldsConfig inputSchema) {

        return transformConfig;
    }
}
