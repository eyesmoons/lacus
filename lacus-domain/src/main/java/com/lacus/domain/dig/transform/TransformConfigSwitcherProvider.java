package com.lacus.domain.dig.transform;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public enum TransformConfigSwitcherProvider {
    INSTANCE;

    private final Map<Transform, TransformConfigSwitcher> configSwitcherCache;

    TransformConfigSwitcherProvider() {
        ServiceLoader<TransformConfigSwitcher> loader =
                ServiceLoader.load(TransformConfigSwitcher.class);
        configSwitcherCache = new ConcurrentHashMap<>();

        for (TransformConfigSwitcher switcher : loader) {
            configSwitcherCache.put(switcher.getTransform(), switcher);
        }
    }

    public TransformConfigSwitcher getTransformConfigSwitcher(Transform name) {
        return configSwitcherCache.get(name);
    }
}
