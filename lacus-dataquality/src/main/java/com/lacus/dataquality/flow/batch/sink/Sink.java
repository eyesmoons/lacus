package com.lacus.dataquality.flow.batch.sink;

import com.lacus.dataquality.flow.Component;
import com.lacus.dataquality.context.SparkRuntimeEnvironment;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * 批量数据写入接口（Sink）
 */
public interface Sink extends Component {

    /**
     * 预处理（如设置默认参数等），可选
     */
    default void prepare(SparkRuntimeEnvironment env) {
    }

    /**
     * 写入数据
     *
     * @param data 待写入数据集
     * @param env  运行时环境
     */
    void write(Dataset<Row> data, SparkRuntimeEnvironment env);
}
