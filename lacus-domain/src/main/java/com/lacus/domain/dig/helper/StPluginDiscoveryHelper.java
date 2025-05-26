package com.lacus.domain.dig.helper;

import com.lacus.domain.dig.form.FormStructure;
import com.lacus.domain.dig.resp.ConnectorInfo;
import lombok.NonNull;
import org.apache.seatunnel.api.common.PluginIdentifier;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.common.constants.PluginType;
import org.apache.seatunnel.plugin.discovery.AbstractPluginDiscovery;
import org.apache.seatunnel.plugin.discovery.seatunnel.SeaTunnelSinkPluginDiscovery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class StPluginDiscoveryHelper {

    public static List<ConnectorInfo> getAllConnectorsFromPluginMapping(PluginType pluginType) {
        Map<PluginIdentifier, String> plugins = AbstractPluginDiscovery.getAllSupportedPlugins(pluginType);
        List<ConnectorInfo> connectorInfos = new ArrayList<>();
        plugins.forEach((plugin, artifactId) -> connectorInfos.add(new ConnectorInfo(plugin, artifactId)));
        return connectorInfos;
    }

    public static List<ConnectorInfo> getDownloadedConnectors(
            @NonNull Map<PluginType, LinkedHashMap<PluginIdentifier, OptionRule>> allPlugins,
            @NonNull PluginType pluginType) {
        LinkedHashMap<PluginIdentifier, OptionRule> pluginIdentifierOptionRuleLinkedHashMap =
                allPlugins.get(pluginType);
        if (pluginIdentifierOptionRuleLinkedHashMap == null) {
            return new ArrayList<>();
        }

        List<ConnectorInfo> connectorInfos = new ArrayList<>();
        pluginIdentifierOptionRuleLinkedHashMap.forEach(
                (plugin, optionRule) -> connectorInfos.add(new ConnectorInfo(plugin, null)));
        return connectorInfos;
    }

    public static List<ConnectorInfo> getTransforms(
            @NonNull Map<PluginType, LinkedHashMap<PluginIdentifier, OptionRule>> allPlugins) {
        LinkedHashMap<PluginIdentifier, OptionRule> pluginIdentifierOptionRuleLinkedHashMap =
                allPlugins.get(PluginType.TRANSFORM);
        if (pluginIdentifierOptionRuleLinkedHashMap == null) {
            return new ArrayList<>();
        }
        return pluginIdentifierOptionRuleLinkedHashMap.keySet().stream()
                .map(t -> new ConnectorInfo(t, null))
                .collect(Collectors.toList());
    }

    public static Map<PluginType, LinkedHashMap<PluginIdentifier, OptionRule>> getAllConnectors() {
        return new SeaTunnelSinkPluginDiscovery().getAllPlugin();
    }

    public static ConcurrentMap<String, FormStructure> getDownloadedConnectorFormStructures(
            @NonNull Map<PluginType, LinkedHashMap<PluginIdentifier, OptionRule>> allPlugins,
            @NonNull PluginType pluginType) {
        LinkedHashMap<PluginIdentifier, OptionRule> pluginIdentifierOptionRuleLinkedHashMap =
                allPlugins.get(pluginType);
        ConcurrentMap<String, FormStructure> result = new ConcurrentHashMap<>();
        if (pluginIdentifierOptionRuleLinkedHashMap == null) {
            return result;
        }

        pluginIdentifierOptionRuleLinkedHashMap.forEach(
                (key, value) ->
                        result.put(
                                key.getPluginName(),
                                SeaTunnelOptionRuleWrapper.wrapper(
                                        value, key.getPluginName(), pluginType)));

        return result;
    }

    public static ConcurrentMap<String, FormStructure> getTransformFormStructures(
            Map<PluginType, LinkedHashMap<PluginIdentifier, OptionRule>> allPlugins) {
        return getDownloadedConnectorFormStructures(allPlugins, PluginType.TRANSFORM);
    }
}
