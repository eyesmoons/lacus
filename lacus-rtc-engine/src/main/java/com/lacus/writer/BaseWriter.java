package com.lacus.writer;

import com.lacus.IWriter;
import lombok.Getter;

/**
 * writer抽象处理器
 * @author shengyu
 * @date 2024/4/30 15:56
 */
@Getter
public abstract class BaseWriter implements IWriter {

    protected String name;

    public BaseWriter(String name) {
        this.name = name;
    }
}