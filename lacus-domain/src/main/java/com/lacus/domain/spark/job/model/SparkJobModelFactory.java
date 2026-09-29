package com.lacus.domain.spark.job.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.domain.spark.job.command.AddSparkJarJobCommand;
import com.lacus.domain.spark.job.command.AddSparkSqlJobCommand;
import com.lacus.domain.spark.job.command.UpdateSparkJarJobCommand;
import com.lacus.domain.spark.job.command.UpdateSparkSqlJobCommand;

public class SparkJobModelFactory {

    public static SparkJobModel loadFromSqlAddCommand(AddSparkSqlJobCommand addSqlCommand, SparkJobModel model) {
        if (addSqlCommand != null && model != null) {
            BeanUtil.copyProperties(addSqlCommand, model);
        }
        return model;
    }

    public static SparkJobModel loadFromSqlUpdateCommand(UpdateSparkSqlJobCommand updateSqlCommand, SparkJobModel model) {
        if (updateSqlCommand != null && model != null) {
            BeanUtil.copyProperties(updateSqlCommand, model);
        }
        return model;
    }

    public static SparkJobModel loadFromJarAddCommand(AddSparkJarJobCommand addJarCommand, SparkJobModel model) {
        if (addJarCommand != null && model != null) {
            BeanUtil.copyProperties(addJarCommand, model);
        }
        return model;
    }

    public static SparkJobModel loadFromJarUpdateCommand(UpdateSparkJarJobCommand updateJarCommand, SparkJobModel model) {
        if (updateJarCommand != null && model != null) {
            BeanUtil.copyProperties(updateJarCommand, model);
        }
        return model;
    }
} 