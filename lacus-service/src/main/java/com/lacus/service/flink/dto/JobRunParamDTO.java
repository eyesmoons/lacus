package com.lacus.service.flink.dto;

import com.lacus.common.exception.CustomException;
import com.lacus.dao.flink.entity.FlinkJobEntity;
import com.lacus.utils.CommonPropertyUtils;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import static com.lacus.common.constant.Constants.FLINK_CLIENT_HOME;
import static com.lacus.common.constant.Constants.FLINK_DEFAULT_SAVEPOINT_PATH;
import static com.lacus.common.constant.Constants.LACUS_APPLICATION_HOME;

@Data
public class JobRunParamDTO {

    private String flinkBinPath;

    private String flinkRunParam;

    private String sqlPath;

    private String flinkCheckpointConfig;

    private String flinkSqlAppHome;

    private String mainJarPath;

    public JobRunParamDTO(String flinkBinPath,
                          String flinkRunParam,
                          String sqlPath,
                          String flinkSqlAppHome,
                          String flinkCheckpointConfig) {
        this.flinkBinPath = flinkBinPath;
        this.flinkRunParam = flinkRunParam;
        this.sqlPath = sqlPath;
        this.flinkSqlAppHome = flinkSqlAppHome;
        this.flinkCheckpointConfig = flinkCheckpointConfig;
    }

    public static JobRunParamDTO buildJobRunParam(FlinkJobEntity flinkJobEntity, String sqlPath) {
        String flinkHome = CommonPropertyUtils.getString(FLINK_CLIENT_HOME);
        String flinkBinPath = flinkHome + "/bin/flink";
        String flinkRunParam = flinkJobEntity.getFlinkRunConfig();
        String flinkSavepointPath = CommonPropertyUtils.getString(FLINK_DEFAULT_SAVEPOINT_PATH);
        String appHome = CommonPropertyUtils.getString(LACUS_APPLICATION_HOME);

        if (ObjectUtils.isEmpty(flinkHome)) {
            throw new CustomException("请配置环境变量[FLINK_HOME]");
        }
        if (ObjectUtils.isEmpty(flinkSavepointPath)) {
            throw new CustomException("请配置环境变量[FLINK_CHECKPOINT_PATH]");
        }
        if (ObjectUtils.isEmpty(appHome)) {
            throw new CustomException("请配置环境变量[APP_HOME]");
        }
        return new JobRunParamDTO(
                flinkBinPath,
                flinkRunParam,
                sqlPath,
                appHome,
                flinkSavepointPath
        );
    }
}
