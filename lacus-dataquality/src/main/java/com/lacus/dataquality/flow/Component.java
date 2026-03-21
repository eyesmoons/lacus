package com.lacus.dataquality.flow;

import java.util.Map;

/**
 * 所有组件的基接口
 */
public interface Component {

    /**
     * 获取组件配置
     */
    Map<String, Object> getConfig();

    /**
     * 验证配置有效性，返回错误信息，null 表示验证通过
     */
    String validateConfig();
}
