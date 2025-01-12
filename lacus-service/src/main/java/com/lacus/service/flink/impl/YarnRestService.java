package com.lacus.service.flink.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lacus.common.exception.CustomException;
import com.lacus.enums.FlinkStatusEnum;
import com.lacus.service.flink.dto.YarnApplicationDTO;
import com.lacus.service.flink.dto.YarnApplicationInfoDTO;
import com.lacus.service.flink.dto.YarnJobInfoDTO;
import com.lacus.utils.PropertyUtils;
import com.lacus.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.lacus.common.constant.Constants.YARN_RESTAPI_ADDRESS;

@Service
@Slf4j
public class YarnRestService {

    private final RestUtil restUtil;

    public YarnRestService(RestUtil restUtil) {
        this.restUtil = restUtil;
    }

    public String getAppIdByYarn(String jobName, String queueName) {
        if (StringUtils.isEmpty(jobName)) {
            throw new CustomException("任务名称为空！");
        }
        String url = getYarnRmHttpAddress() + "ws/v1/cluster/apps?queue=" + queueName;
        String res = restUtil.getForString(url);
        YarnApplicationInfoDTO yarnApplicationInfoDTO = JSON.parseObject(res, YarnApplicationInfoDTO.class);
        this.check(yarnApplicationInfoDTO, queueName, jobName);

        for (YarnApplicationDTO yarnApplicationDTO : yarnApplicationInfoDTO.getApps().getYarnApplicationList()) {
            if (jobName.equals(yarnApplicationDTO.getName())) {
                if (FlinkStatusEnum.RUNNING.name().equals(yarnApplicationDTO.getState())) {
                    log.info("任务信息：{}", yarnApplicationDTO);
                    return yarnApplicationDTO.getId();
                } else {
                    log.error("任务运行状态失败，状态为： {}", yarnApplicationDTO.getState());
                }
            }
        }
        throw new CustomException("yarn队列[" + queueName + "]中没有找到运行的任务，jobName：" + jobName);
    }

    public String getYarnRmHttpAddress() {
        String urlHa = PropertyUtils.getString(YARN_RESTAPI_ADDRESS);
        if (StringUtils.isEmpty(urlHa)) {
            throw new CustomException("请配置[" + YARN_RESTAPI_ADDRESS + "]");
        }
        return getActiveYarnUrl(urlHa);
    }

    private String getActiveYarnUrl(String urlHa) {
        String[] urls = urlHa.split(";");
        for (String http : urls) {
            try {
                String url = http + "ws/v1/cluster/info";
                String res = restUtil.getForString(url);
                if (StringUtils.isNotEmpty(res)) {
                    JSONObject jsonObject = (JSONObject) JSON.parse(res);
                    String haState = jsonObject.getJSONObject("clusterInfo").get("haState").toString();
                    if ("ACTIVE".equalsIgnoreCase(haState)) {
                        return http;
                    }
                }
            } catch (Exception e) {
                log.error("http连接异常：{}", http, e);
            }
        }
        throw new CustomException("连接异常：" + urlHa);
    }

    public void stopJobByJobId(String appId) {
        log.info("停止Flink任务, appId: {}", appId);
        if (StringUtils.isEmpty(appId)) {
            throw new CustomException("appId为空");
        }
        String url = getYarnRmHttpAddress() + "ws/v1/cluster/apps/" + appId + "/state";
        log.info("停止Flink任务, url: {}", url);
        JSONObject params = new JSONObject();
        params.put("state", "KILLED");
        JSONObject result = restUtil.postForJsonObject(url, params);
        log.info("停止Flink任务：{}", result);
    }

    public FlinkStatusEnum getJobStateByJobId(String appId) {
        if (StringUtils.isEmpty(appId)) {
            throw new CustomException("appId为空");
        }
        String url = getYarnRmHttpAddress() + "ws/v1/cluster/apps/" + appId + "/state";
        String res = restUtil.getForString(url);
        if (StringUtils.isEmpty(res)) {
            throw new CustomException("请求失败，返回结果为空：" + url);
        }
        return FlinkStatusEnum.getFlinkStateEnum(String.valueOf(JSON.parseObject(res).get("state")));
    }

    public YarnJobInfoDTO getJobInfoForPerYarnByAppId(String appId) {
        if (StringUtils.isEmpty(appId)) {
            throw new CustomException("appId为空");
        }
        String res;
        try {
            String url = getYarnRmHttpAddress() + "/proxy/" + appId + "/jobs";
            res = restUtil.getForString(url);
            if (StringUtils.isEmpty(res)) {
                return null;
            }
            JSONArray jsonArray = (JSONArray) JSON.parseObject(res).get("jobs");
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            YarnJobInfoDTO jobInfo = new YarnJobInfoDTO();
            jobInfo.setId((String) jsonObject.get("id"));
            jobInfo.setStatus((String) jsonObject.get("status"));
            return jobInfo;
        } catch (Exception e) {
            log.error("get jobInfo for yarn-per-job by appid出错  ", e);
        }
        return null;
    }

    public void cancelJobForYarnByAppId(String appId, String jobId) {
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(jobId)) {
            throw new CustomException("appId为空");
        }

        String url = getYarnRmHttpAddress() + "/proxy/" + appId + "/jobs/yarn-cancel";
        String res = restUtil.getForString(url);
        log.info("cancel job by appId请求结果：{}", res);
    }


    public String getSavepointPath(String appId, String jobId) {
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(jobId)) {
            throw new CustomException("appId为空");
        }

        String url = getYarnRmHttpAddress() + "/proxy/" + appId + "/jobs/" + jobId + "/checkpoints";
        String res = restUtil.getForString(url);
        log.info("get savepoint path请求结果: res：{}", res);
        if (StringUtils.isEmpty(res)) {
            return null;
        }
        try {
            JSONObject jsonObject = (JSONObject) JSON.parseObject(res).get("latest");
            JSONObject savepoint = (JSONObject) jsonObject.get("savepoint");
            if (savepoint == null) {
                return null;
            }
            return (String) savepoint.get("external_path");
        } catch (Exception e) {
            log.error("json解析异常{}", res, e);
        }
        return null;
    }

    private void check(YarnApplicationInfoDTO yarnApplicationInfoDTO, String queueName, String jobName) {
        if (Objects.isNull(yarnApplicationInfoDTO)) {
            throw new CustomException("yarn队列[" + queueName + "]中没有找到运行的任务：" + jobName);
        } else if (Objects.isNull(yarnApplicationInfoDTO.getApps())) {
            throw new CustomException("yarn队列[" + queueName + "]中没有找到运行的任务：" + jobName);
        } else if (Objects.isNull(yarnApplicationInfoDTO.getApps().getYarnApplicationList()) || yarnApplicationInfoDTO.getApps().getYarnApplicationList().isEmpty()) {
            throw new CustomException("yarn队列[" + queueName + "]中没有找到运行的任务：" + jobName);
        }
    }
}
