package com.lacus.domain.datasync.job;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.common.utils.RestUtil;
import com.lacus.common.utils.kafka.KafkaUtil;
import com.lacus.common.utils.time.DateUtils;
import com.lacus.common.utils.yarn.*;
import com.lacus.dao.datasync.mapper.DataSyncJobInstanceMapper;
import com.lacus.dao.datasync.mapper.DataSyncJobMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

@Slf4j
@Service("monitorService")
public class JobMonitorService {

    @Value("${flink.conf-path}")
    private String yarnConf;

    @Autowired
    private RestUtil restUtil;

    private final String appType = "Apache Flink";

    @Value("${yarn.restapi-address}")
    private String flinkRestPrefix;

    @Value("${yarn.node-address}")
    private String yarnNode;

    @Autowired
    private DataSyncJobInstanceMapper jobLogsMapper;

    @Autowired
    private DataSyncJobMapper jobsMapper;

    public List<ApplicationModel> listFlinkJob() {
        return YarnUtil.listYarnRunningJob(yarnConf, appType);
    }

    public YarnMetrics clusterDetail() {
        YarnConfiguration conf = new YarnConfiguration();
        ConfigUtil.initConfig(conf, yarnConf);
        String host = conf.get("yarn.resourcemanager.webapp.address");
        JSONObject json = restUtil.getForJsonObject("http://" + host + "/ws/v1/cluster/metrics");
        log.debug("rest返回信息:{}", JSON.toJSONString(json));
        YarnMetrics yarnMetrics = json.getJSONObject("clusterMetrics").toJavaObject(YarnMetrics.class);

        yarnMetrics.setYarnNode(yarnNode);
        yarnMetrics.setTotalGB(yarnMetrics.getTotalMB() / 1024);
        yarnMetrics.setAllocatedGB(yarnMetrics.getAllocatedMB() / 1024);
        yarnMetrics.setAvailableGB(yarnMetrics.getAvailableMB() / 1024);
        BigDecimal memoryPer = BigDecimal.valueOf((double) yarnMetrics.getAllocatedMB() / yarnMetrics.getTotalMB());
        BigDecimal corePer = BigDecimal.valueOf((double) yarnMetrics.getAllocatedVirtualCores() / yarnMetrics.getTotalVirtualCores());
        DecimalFormat df = new DecimalFormat("#.####");
        yarnMetrics.setMemoryPercents(df.format(memoryPer));
        yarnMetrics.setVirtualCoresPercents(df.format(corePer));
        return yarnMetrics;
    }

    public ApplicationModel yarnJobDetail(String appId) {
        return YarnUtil.yarnJobDetail(yarnConf, appId);
    }

    public String getFlinkJobId(String applicationId) {
        try {
            String sb = flinkRestPrefix + applicationId + "/jobs/overview";
            log.info("获取flinkJobId请求地址：{}", sb);
            JSONObject jsonObject = restUtil.getForJsonObject(sb);
            log.debug("rest返回信息:{}", JSON.toJSONString(jsonObject));
            JSONArray jobs = JSON.parseObject(JSON.toJSONString(jsonObject)).getJSONArray("jobs");
            if (ObjectUtils.isEmpty(jobs)) {
                log.error("获取Flink JobId 失败");
            }
            return jobs.getJSONObject(0).getString("jid");
        } catch (Exception e) {
            log.error("获取Flink JobId 失败：{}", e.getMessage());
            return null;
        }
    }

    public Object flinkJobOverview(String applicationId) {
        String sb = flinkRestPrefix + applicationId + "/jobs/overview";
        return restUtil.getForObject(sb);
    }

    public FlinkJobDetail flinkJobDetail(String applicationId) {
        String flinkJobId = getFlinkJobId(applicationId);
        if (Objects.isNull(flinkJobId)) {
            for (int i = 0; i < 3; i++) {
                flinkJobId = getFlinkJobId(applicationId);
            }
        }
        if (Objects.isNull(flinkJobId)) {
            throw new RuntimeException("flinkJobId为null");
        }
        String sb = flinkRestPrefix + applicationId + "/jobs/" + flinkJobId;
        Object forObject = restUtil.getForObject(sb);
        return JSONObject.parseObject(JSON.toJSONString(forObject), FlinkJobDetail.class);
    }

    public Object flinkJobCheckpoint(String applicationId) {
        String flinkJobId = getFlinkJobId(applicationId);
        String sb = flinkRestPrefix + applicationId + "/jobs/" + flinkJobId + "/checkpoints";
        return restUtil.getForObject(sb);
    }

    public Object flinkJobCheckpointConfig(String applicationId) {
        String flinkJobId = getFlinkJobId(applicationId);
        String sb = flinkRestPrefix + applicationId + "/jobs/" + flinkJobId + "/checkpoints/config";
        return restUtil.getForObject(sb);
    }

    public Object flinkJobExceptions(String applicationId, String flinkJobId) {
        StringBuilder sb = new StringBuilder();
        sb.append(flinkRestPrefix)
                .append(applicationId)
                .append("/jobs/")
                .append(flinkJobId)
                .append("/exceptions");
        try {
            return restUtil.getForObject(sb.toString());
        } catch (Exception e) {
            throw new ApiException(ErrorCode.Internal.UNKNOWN_ERROR, "获取异常信息失败");
        }
    }

    public Object kafkaConsumedRate(String applicationId, String flinkJobId) {
        List<String> vertices = vertices(applicationId, flinkJobId);
        if (ObjectUtils.isNotEmpty(vertices)) {
            String ver = vertices.get(0);
            String sb = flinkRestPrefix +
                    applicationId +
                    "/jobs/" +
                    flinkJobId +
                    "/vertices/" +
                    ver +
                    "/subtasks/metrics?get=Source__Custom_Source.KafkaConsumer.records-consumed-rate";
            Object rs = restUtil.getForObject(sb);
            log.debug("rest返回信息:{}", JSON.toJSONString(rs));
            return rs;
        } else {
            throw new ApiException(ErrorCode.Internal.UNKNOWN_ERROR, "获取Flink监控信息失败");
        }
    }

    public Object kafkaConsumedLag(String sinkJobConf, List<String> topics) {
        JSONObject sourceMap = JSON.parseObject(JSON.toJSONString(sinkJobConf));
        Map<String, Long> topicLag = KafkaUtil.kafkaMessageLag(sourceMap, topics);
        String time = DateUtils.getCurrentTime();
        List<Map<String, Object>> result = Lists.newArrayList();
        for (Map.Entry<String, Long> entry : topicLag.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("topic", entry.getKey());
            map.put("value", entry.getValue());
            map.put("time", time);
            result.add(map);
        }
        return result;
    }

    public Object recordInOutPerSecond(String applicationId, String flinkJobId) {
        List<String> vertices = vertices(applicationId, flinkJobId);
        if (ObjectUtils.isEmpty(vertices)) {
            throw new ApiException(ErrorCode.Internal.UNKNOWN_ERROR, "获取Flink监控信息失败");
        }
        String baseUrl = flinkRestPrefix + applicationId + "/jobs/" + flinkJobId + "/vertices/";
        JSONArray rsLists = new JSONArray();
        for (int i = 0; i < vertices.size(); i++) {
            StringBuilder sbtmp = new StringBuilder();
            sbtmp.append(baseUrl).append(vertices.get(i));
            if (i == 0) {
                sbtmp.append("/subtasks/metrics?get=numRecordsOutPerSecond");
            } else {
                sbtmp.append("/subtasks/metrics?get=numRecordsInPerSecond");
            }
            Object rs = restUtil.getForObject(sbtmp.toString());
            JSONArray rsArray = JSON.parseArray(JSON.toJSONString(rs));
            if (ObjectUtils.isNotEmpty(rsArray)) {
                JSONObject rsJsob = rsArray.getJSONObject(0);
                if (i == 0) {
                    rsJsob.put("id", "kafkaSources输出-" + rsJsob.getString("id"));
                } else {
                    rsJsob.put("id", "dorisSink输入-" + rsJsob.getString("id"));
                }
                rsLists.addAll(rsArray);
            }
        }
        concatTime(rsLists);
        log.debug("rest返回信息:{}", JSON.toJSONString(rsLists));
        return rsLists;
    }

    public Object recordInOut(String applicationId, String flinkJobId) {
        List<String> vertices = vertices(applicationId, flinkJobId);
        if (ObjectUtils.isEmpty(vertices)) {
            throw new ApiException(ErrorCode.Internal.UNKNOWN_ERROR, "获取Flink监控信息失败");
        }
        String baseUrl = flinkRestPrefix + applicationId + "/jobs/" + flinkJobId + "/vertices/";
        JSONArray rsLists = new JSONArray();
        for (int i = 0; i < vertices.size(); i++) {
            StringBuilder sbtmp = new StringBuilder();
            sbtmp.append(baseUrl).append(vertices.get(i));
            if (i == 0) {
                sbtmp.append("/subtasks/metrics?get=numRecordsOut");
            } else {
                sbtmp.append("/subtasks/metrics?get=numRecordsIn");
            }
            Object rs = restUtil.getForObject(sbtmp.toString());
            JSONArray rsArray = JSON.parseArray(JSON.toJSONString(rs));
            if (ObjectUtils.isNotEmpty(rsArray)) {
                JSONObject rsJsob = rsArray.getJSONObject(0);
                if (i == 0) {
                    rsJsob.put("id", "kafkaSources输出-" + rsJsob.getString("id"));
                } else {
                    rsJsob.put("id", "dorisSink输入-" + rsJsob.getString("id"));
                }
                rsLists.addAll(rsArray);
            }
        }
        concatTime(rsLists);
        log.debug("rest返回信息:{}", JSON.toJSONString(rsLists));
        return rsLists;
    }

    public Object jobManagerMemory(String applicationId) {
        Object rs = restUtil.getForObject(flinkRestPrefix + applicationId + "/jobmanager/metrics?get=Status.JVM.Memory.NonHeap.Committed,Status.JVM.Memory.Heap.Used,Status.JVM.Memory.Heap.Committed,Status.JVM.Memory.Direct.MemoryUsed,Status.JVM.Memory.Mapped.MemoryUsed");
        JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(rs));
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String value = jsonObject.getString("value");
            if (StringUtils.isBlank(value)) {
                value = "0";
            }
            DecimalFormat df = new DecimalFormat("#.####");
            BigDecimal bg = new BigDecimal(value);
            BigDecimal val = bg.divide(new BigDecimal(1048576));
            jsonObject.put("value", new BigDecimal(df.format(val)));
        }
        concatTime(jsonArray);
        log.debug("rest返回信息:{}", jsonArray);
        return jsonArray;
    }


    public Object taskmanagerMemory(String applicationId) {
        String sb = flinkRestPrefix + applicationId +
                "/taskmanagers/metrics?get=Status.JVM.Memory.NonHeap.Committed,Status.JVM.Memory.Heap.Used,Status.JVM.Memory.Heap.Committed,Status.JVM.Memory.Direct.MemoryUsed,Status.JVM.Memory.Mapped.MemoryUsed";
        Object rs = restUtil.getForObject(sb);
        //单位换算
        JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(rs));
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String value = jsonObject.getString("sum");
            if (StringUtils.isBlank(value)) {
                value = "0";
            }
            DecimalFormat df = new DecimalFormat("#.####");
            BigDecimal bg = new BigDecimal(value);
            //byte=>MB
            BigDecimal val = bg.divide(new BigDecimal(1048576));
            jsonObject.put("value", new BigDecimal(df.format(val)));
        }
        concatTime(jsonArray);
        log.debug("rest返回信息:{}", JSON.toJSONString(jsonArray));
        return jsonArray;
    }

    private List<String> vertices(String applicationId, String flinkJobId) {
        List<String> verticeIdList = new ArrayList<>();
        String sb = flinkRestPrefix + applicationId + "/jobs/" + flinkJobId;
        Object forObject = restUtil.getForObject(sb);
        log.debug("rest返回信息:{}", JSON.toJSONString(forObject));
        JSONObject flinkDetail = JSON.parseObject(JSON.toJSONString(forObject));
        JSONArray vertices = flinkDetail.getJSONArray("vertices");
        for (int i = 0; i < vertices.size(); i++) {
            JSONObject json = vertices.getJSONObject(i);
            verticeIdList.add(json.getString("id"));
        }
        return verticeIdList;
    }

    private JSONArray verticesDetail(String applicationId, String flinkJobId, String verticeId) {
        String sb = flinkRestPrefix +
                applicationId +
                "/jobs/" +
                flinkJobId +
                "/vertices/" +
                verticeId +
                "/subtasks/metrics";
        Object forObject = restUtil.getForObject(sb);
        log.debug("rest返回信息:{}", JSON.toJSONString(forObject));
        return JSON.parseArray(JSON.toJSONString(forObject));
    }

    private void concatTime(JSONArray jsonArray) {
        String time = DateUtils.getCurrentTime();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            jsonObject.put("time", time);
        }
    }
}
