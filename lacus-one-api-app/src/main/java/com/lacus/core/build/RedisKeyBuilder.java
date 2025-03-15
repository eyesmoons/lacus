package com.lacus.core.build;

import com.lacus.common.constants.Constant;

public class RedisKeyBuilder {

    public static String apiKeyBuild(String apiUrl) {
        return Constant.SERVICE_NAME + ":core" + ":" + apiUrl;
    }

    public static String datasourceKeyBuild(Long datasourceId) {
        return Constant.SERVICE_NAME + ":datasource" + ":" + datasourceId;
    }
}
