package com.lacus.st.interfaces;

import java.util.Map;

/**
 * ST数据源组件接口
 * 所有数据源组件都需要实现此接口
 *
 * @author lacus
 */
public interface StSourceInterface extends StComponentInterface {

    /**
     * 检查数据源连接
     *
     * @return 连接是否正常
     */
    boolean checkConnection();

    /**
     * 获取数据源信息
     *
     * @return 数据源信息
     */
    Map<String, Object> getSourceInfo();
}
