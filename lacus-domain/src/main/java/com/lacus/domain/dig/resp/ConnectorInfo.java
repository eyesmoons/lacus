package com.lacus.domain.dig.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.seatunnel.api.common.PluginIdentifier;

@Data
@AllArgsConstructor
public class ConnectorInfo {
    private PluginIdentifier pluginIdentifier;
    private String artifactId;
}
