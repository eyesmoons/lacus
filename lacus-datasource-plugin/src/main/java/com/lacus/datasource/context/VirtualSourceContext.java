package com.lacus.datasource.context;

public final class VirtualSourceContext {
    private static final ThreadLocal<String> CONNECTION_PARAMS = new ThreadLocal<>();

    private VirtualSourceContext() {
    }

    public static void set(String params) {
        CONNECTION_PARAMS.set(params);
    }

    public static String get() {
        return CONNECTION_PARAMS.get();
    }

    public static void clear() {
        CONNECTION_PARAMS.remove();
    }
}