package com.lacus.utils;

public class SeaTunnelConfigUtil {

    private static final String CONFIG_TEMPLATE =
            "env {\n"
                    + "env_placeholder"
                    + "}\n"
                    + "source {\n"
                    + "source_placeholder"
                    + "}\n"
                    + "transform {\n"
                    + "transform_placeholder"
                    + "}\n"
                    + "sink {\n"
                    + "sink_placeholder"
                    + "}\n";

    public static String generateConfig( String sources, String transforms, String sinks) {
        return CONFIG_TEMPLATE
                .replace("env_placeholder", "")
                .replace("source_placeholder", sources)
                .replace("transform_placeholder", transforms)
                .replace("sink_placeholder", sinks);
    }

    public static String generateConfig(
            String env, String sources, String transforms, String sinks) {
        return CONFIG_TEMPLATE
                .replace("env_placeholder", env)
                .replace("source_placeholder", sources)
                .replace("transform_placeholder", transforms)
                .replace("sink_placeholder", sinks);
    }
}
