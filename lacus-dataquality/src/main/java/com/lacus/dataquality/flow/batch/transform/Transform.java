package com.lacus.dataquality.flow.batch.transform;

import com.lacus.dataquality.flow.Component;
import com.lacus.dataquality.context.SparkRuntimeEnvironment;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * 批量数据转换接口（Transform）
 */
public interface Transform extends Component {

    /**
     * 转换数据
     *
     * @param data 输入数据集
     * @param env  运行时环境
     * @return 转换后的数据集
     */
    Dataset<Row> transform(Dataset<Row> data, SparkRuntimeEnvironment env);
}
