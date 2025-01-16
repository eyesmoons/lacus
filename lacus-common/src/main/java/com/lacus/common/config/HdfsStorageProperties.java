package com.lacus.common.config;

import com.lacus.utils.CommonPropertyUtils;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

import static com.lacus.common.constant.Constants.DEFAULT_HDFS_CONFIG;
import static com.lacus.common.constant.Constants.HADOOP_RESOURCE_MANAGER_HTTPADDRESS_PORT;
import static com.lacus.common.constant.Constants.HADOOP_SECURITY_AUTHENTICATION_STARTUP_STATE;
import static com.lacus.common.constant.Constants.HADOOP_USER;
import static com.lacus.common.constant.Constants.YARN_APPLICATION_STATUS_ADDRESS;
import static com.lacus.common.constant.Constants.YARN_JOB_HISTORY_STATUS_ADDRESS;
import static com.lacus.common.constant.Constants.YARN_RESOURCEMANAGER_HA_RM_IDS;

@Data
@Configuration
public class HdfsStorageProperties {

    // hdfs username
    private String user = CommonPropertyUtils.getString(HADOOP_USER);

    // default fs
    private String defaultFS = CommonPropertyUtils.getString(DEFAULT_HDFS_CONFIG);

    // yarn resource manager ha rm ids
    private String yarnResourceRmIds = CommonPropertyUtils.getString(YARN_RESOURCEMANAGER_HA_RM_IDS);

    // yarn application status address
    private String yarnAppStatusAddress = CommonPropertyUtils.getString(YARN_APPLICATION_STATUS_ADDRESS);

    // yarn job history status address
    private String yarnJobHistoryStatusAddress = CommonPropertyUtils.getString(YARN_JOB_HISTORY_STATUS_ADDRESS);

    // hadoop resource manager http address port
    private String hadoopResourceManagerHttpAddressPort = CommonPropertyUtils.getString(HADOOP_RESOURCE_MANAGER_HTTPADDRESS_PORT);

    // hadoop security authentication startup state
    private boolean hadoopSecurityAuthStartupState = CommonPropertyUtils.getBoolean(HADOOP_SECURITY_AUTHENTICATION_STARTUP_STATE, false);
}
