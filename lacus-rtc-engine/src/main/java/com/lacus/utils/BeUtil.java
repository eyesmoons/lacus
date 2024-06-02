package com.lacus.utils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * doris be 工具类
 */
public class BeUtil {

    private static AtomicInteger pos = new AtomicInteger(0);

    /**
     * 轮询be节点，获取一台be作为stream load节点
     */
    public static String getBeRoundRobin(Map<String, Integer> beMap) {
        Set<String> keySet = beMap.keySet();
        ArrayList<String> keyList = new ArrayList<>(keySet);
        if (pos.get() >= keySet.size()) {
            pos = new AtomicInteger(0);
        }
        return keyList.get(pos.getAndIncrement());
    }
}
