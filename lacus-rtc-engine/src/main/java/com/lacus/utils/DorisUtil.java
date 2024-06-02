package com.lacus.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lacus.exception.CustomException;
import com.lacus.model.StreamLoadResponse;
import com.lacus.model.SinkDataSource;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class DorisUtil {

    private final static List<String> DORIS_SUCCESS_STATUS = new ArrayList<>(Arrays.asList("Success", "Publish Timeout"));

    public static Map<String, Integer> getBeConfig(SinkDataSource dataSource) {
        Map<String, Integer> beMap = new HashMap<>();
        System.out.println(JSON.toJSONString(dataSource));
        List<JSONObject> backends = JdbcUtil.executeQuery(
                dataSource.getHostPort(),
                dataSource.getDbName(),
                dataSource.getUserName(),
                PasswordUtil.decryptPwd(dataSource.getPassword()),
                "show backends");

        for (JSONObject be : backends) {
            if (be.getBoolean("Alive")) {
                String ip = be.getString("Host");
                String port = be.getString("HttpPort");
                beMap.put(ip + ":" + port, 1);
            }
        }
        log.info("BE config：{}", beMap);
        if (beMap.isEmpty()) {
            throw new CustomException("BE config not found");
        }
        return beMap;
    }

    /**
     * 判断streamLoad是否成功
     *
     * @param streamLoadResponse 返回内容
     */
    public static Boolean checkStreamLoadStatus(StreamLoadResponse streamLoadResponse) {
        return DORIS_SUCCESS_STATUS.contains(streamLoadResponse.getStatus()) && streamLoadResponse.getNumberTotalRows() == streamLoadResponse.getNumberLoadedRows();
    }
}
