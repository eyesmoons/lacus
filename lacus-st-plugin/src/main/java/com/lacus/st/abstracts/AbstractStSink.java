package com.lacus.st.abstracts;

import com.lacus.st.interfaces.StSinkInterface;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * ST数据输出组件抽象基类
 * 提供数据输出组件的通用功能实现
 *
 * @author lacus
 */
@Slf4j
public abstract class AbstractStSink extends AbstractStComponent implements StSinkInterface {

    @Override
    public boolean checkConnection() {
        return doCheckConnection();
    }

    @Override
    public Map<String, Object> getSinkInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("componentName", getClass().getSimpleName());
        return info;
    }

    /**
     * 子类实现具体的连接检查逻辑
     *
     * @return 连接是否正常
     */
    protected abstract boolean doCheckConnection();
}
