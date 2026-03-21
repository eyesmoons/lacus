package com.lacus.dataquality.flow.batch.source;

import com.lacus.dataquality.flow.Component;
import com.lacus.dataquality.context.SparkRuntimeEnvironment;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * 批量数据读取接口（Source）
 */
public interface Source extends Component {

    /**
     * 预处理（如初始化连接等），可选
     */
    default void prepare(SparkRuntimeEnvironment env) {
    }

    /**
     * 从数据源读取数据
     *
     * @param env 运行时环境
     * @return Spark Dataset
     */
    Dataset<Row> read(SparkRuntimeEnvironment env);
}
