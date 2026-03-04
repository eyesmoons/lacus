package com.lacus.st.interfaces;

import java.util.Map;

/**
 * ST数据输出组件接口
 * 所有数据输出组件都需要实现此接口
 *
 * @author lacus
 */
public interface StSinkInterface extends StComponentInterface {

    /**
     * 检查输出目标连接
     *
     * @return 连接是否正常
     */
    boolean checkConnection();

    /**
     * 获取输出目标信息
     *
     * @return 输出目标信息
     */
    Map<String, Object> getSinkInfo();
}
