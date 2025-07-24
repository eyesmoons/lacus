package com.lacus.domain.dig.transform;

import com.lacus.domain.dig.dto.DatabaseTableSchema;
import com.lacus.domain.dig.dto.SourceFieldsConfig;
import com.lacus.domain.dig.form.FormStructure;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.shade.com.typesafe.config.Config;

public interface TransformConfigSwitcher {

    Transform getTransform();

    FormStructure getFormStructure(OptionRule transformOptionRule);

    Config mergeTransformConfig(
            Config transformConfig, TransformOptions transformOption, SourceFieldsConfig inputSchema);
}
