package com.lacus.core.buried;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BuriedFunc {

    private static final ThreadLocal<String> buriedThreadLocal = new InheritableThreadLocal<>();

    private static final ThreadLocal<String> childThreadLocal = new ThreadLocal<>();

    private static final ConcurrentMap<String, BuriedModel> buriedMap = new ConcurrentHashMap<>();

    public static void setApi(String apiName) {
        buriedThreadLocal.set(apiName);
    }

    public static String getApi() {
        return buriedThreadLocal.get();
    }

    /**
     * 子线程信息
     *
     * @param uuid
     */
    public static void setChildThreadLocal(String uuid) {
        childThreadLocal.set(uuid);
    }

    public static String getChildThreadLocal() {
        return childThreadLocal.get();
    }


    public static String printLog() {
        String api = getApi();
        BuriedModel buriedModel = buriedMap.get(api);
        if (Objects.isNull(buriedModel)) {
            return "";
        }
        String result = String.join("<br/>", buriedModel.getStackLogs());
        buriedMap.remove(api);
        buriedThreadLocal.remove();
        return result;
    }

    public static void addLog(String log) {
        String api = getApi();
        BuriedModel buriedModel;
        if (api.startsWith("testRun")) {
            buriedModel = buriedMap.get(api);
            if (Objects.nonNull(buriedModel)) {
                List<String> stackLogs = buriedModel.getStackLogs();
                stackLogs.add(log);
            } else {
                buriedModel = new BuriedModel();
                List<String> logList = Lists.newArrayList();
                logList.add(log);
                buriedModel.setStackLogs(logList);
                buriedMap.put(api, buriedModel);
            }
        }
    }


    public static void addDelay(Long delay) {
        String api = getApi();
        String childUid = getChildThreadLocal();
        BuriedModel buriedModel;
        if (api.startsWith("testRun")) {
            buriedModel = buriedMap.get(childUid);
            if (Objects.nonNull(buriedModel)) {
                List<Long> delayTime = buriedModel.getDelayTime();
                delayTime.add(delay);
            } else {
                buriedModel = new BuriedModel();
                List<Long> delayList = Lists.newArrayList();
                buriedModel.setDelayTime(delayList);
                buriedMap.put(childUid, buriedModel);
            }
        }
    }

    public static String printDelay() {
        String childUid = getChildThreadLocal();
        String result = "";
        BuriedModel buriedModel = buriedMap.get(childUid);
        if (Objects.isNull(buriedModel)) {
            return null;
        }
        BuriedModel model = buriedMap.get(getApi());
        model.setDelayTime(buriedModel.getDelayTime());
        buriedMap.remove(childUid);
        return buriedModel.getDelayTime().toString();
    }


}
