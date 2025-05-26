package com.lacus.domain.dig;

import com.lacus.domain.dig.form.FormStructure;
import com.lacus.domain.dig.helper.StPluginDiscoveryHelper;
import com.lacus.domain.dig.resp.ConnectorInfo;
import com.lacus.enums.StConnectorStatus;
import org.apache.seatunnel.api.common.PluginIdentifier;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.common.constants.PluginType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class StConnectorBusiness {
    private static final List<String> SKIP_SOURCE = Collections.emptyList();
    private static final List<String> SKIP_SINK = Collections.singletonList("Console");

    public List<ConnectorInfo> listSources(StConnectorStatus status) throws IOException {
        Map<PluginType, LinkedHashMap<PluginIdentifier, OptionRule>> allConnectors = StPluginDiscoveryHelper.getAllConnectors();

        List<ConnectorInfo> connectorInfos = Collections.emptyList();
        if (status == StConnectorStatus.ALL) {
            connectorInfos = StPluginDiscoveryHelper.getAllConnectorsFromPluginMapping(PluginType.SOURCE);
        } else if (status == StConnectorStatus.DOWNLOADED) {
            connectorInfos = StPluginDiscoveryHelper.getDownloadedConnectors(allConnectors, PluginType.SOURCE);
        }
        return connectorInfos.stream()
                .filter(c -> !SKIP_SOURCE.contains(c.getPluginIdentifier().getPluginName()))
                .collect(Collectors.toList());
    }

    public List<ConnectorInfo> listTransforms() throws IOException {
        Map<PluginType, LinkedHashMap<PluginIdentifier, OptionRule>> allConnectors = StPluginDiscoveryHelper.getAllConnectors();
        return StPluginDiscoveryHelper.getTransforms(allConnectors);
    }

    public List<ConnectorInfo> listSinks(StConnectorStatus status) throws IOException {
        Map<PluginType, LinkedHashMap<PluginIdentifier, OptionRule>> allConnectors = StPluginDiscoveryHelper.getAllConnectors();
        List<ConnectorInfo> connectorInfos = Collections.emptyList();
        if (status == StConnectorStatus.ALL) {
            connectorInfos = StPluginDiscoveryHelper.getAllConnectorsFromPluginMapping(PluginType.SINK);
        } else if (status == StConnectorStatus.DOWNLOADED) {
            connectorInfos = StPluginDiscoveryHelper.getDownloadedConnectors(allConnectors, PluginType.SINK);
        }
        return connectorInfos.stream()
                .filter(c -> !SKIP_SINK.contains(c.getPluginIdentifier().getPluginName()))
                .collect(Collectors.toList());
    }

    public FormStructure getConnectorFormStructure(String pluginType, String connectorName) {
        Map<PluginType, LinkedHashMap<PluginIdentifier, OptionRule>> allConnectors = StPluginDiscoveryHelper.getAllConnectors();
        if (PluginType.SOURCE.getType().equals(pluginType)) {
            ConcurrentMap<String, FormStructure> allDownloadedSourceFormStructures = StPluginDiscoveryHelper.getDownloadedConnectorFormStructures(allConnectors, PluginType.SOURCE);
            return allDownloadedSourceFormStructures.get(connectorName);
        }

        if (PluginType.TRANSFORM.getType().equals(pluginType)) {
            ConcurrentMap<String, FormStructure> transformFormStructures = StPluginDiscoveryHelper.getTransformFormStructures(allConnectors);
            return transformFormStructures.get(connectorName);
        }

        if (PluginType.SINK.getType().equals(pluginType)) {
            ConcurrentMap<String, FormStructure> downloadedSinkFormStructures = StPluginDiscoveryHelper.getDownloadedConnectorFormStructures(allConnectors, PluginType.SINK);
            return downloadedSinkFormStructures.get(connectorName);
        }
        return null;
    }
}
