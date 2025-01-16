package com.lacus.source.impl;

import com.lacus.source.ISource;
import com.ververica.cdc.connectors.base.options.StartupOptions;
import lombok.Getter;

import java.util.Properties;

/**
 * reader抽象处理器
 *
 * @created by shengyu on 2023/8/31 10:55
 */
@Getter
public abstract class BaseSource implements ISource {

    protected String name;

    public BaseSource(String name) {
        this.name = name;
    }

    protected StartupOptions getStartupOptions(String syncType, Long timeStamp) {
        StartupOptions startupOptions = null;
        switch (syncType) {
            case "initial":
                startupOptions = StartupOptions.initial();
                break;
            case "earliest":
                startupOptions = StartupOptions.earliest();
                break;
            case "latest":
                startupOptions = StartupOptions.latest();
                break;
            case "timestamp":
                startupOptions = StartupOptions.timestamp(timeStamp);
                break;
        }
        return startupOptions;
    }

    protected Properties getDebeziumProperties() {
        Properties properties = new Properties();
        properties.setProperty("converters", "dateConverters");
        //根据类在那个包下面修改
        properties.setProperty("dateConverters.type", "com.lacus.function.MySqlDateTimeConverter");
        properties.setProperty("dateConverters.format.date", "yyyy-MM-dd");
        properties.setProperty("dateConverters.format.time", "HH:mm:ss");
        properties.setProperty("dateConverters.format.datetime", "yyyy-MM-dd HH:mm:ss");
        properties.setProperty("dateConverters.format.timestamp", "yyyy-MM-dd HH:mm:ss");
        properties.setProperty("dateConverters.format.timestamp.zone", "UTC+8");
        // 全局读写锁，可能会影响在线业务，跳过锁设置
        properties.setProperty("debezium.snapshot.locking.mode", "none");
        properties.setProperty("include.schema.changes", "true");
        properties.setProperty("bigint.unsigned.handling.mode", "long");
        properties.setProperty("decimal.handling.mode", "double");
        return properties;
    }
}
