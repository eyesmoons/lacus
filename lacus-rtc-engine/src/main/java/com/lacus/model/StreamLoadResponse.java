package com.lacus.model;

import lombok.Data;

@Data
public class StreamLoadResponse {

    /**
     * 导入的事务ID。用户可不感知。
     */
    private int TxnId;
    /**
     * 导入 Label。由用户指定或系统自动生成。
     */
    private String Label;
    /**
     * 导入完成状态。
     * "Success"：表示导入成功。
     * "Publish Timeout"：该状态也表示导入已经完成，只是数据可能会延迟可见，无需重试。
     * "Label Already Exists"：Label 重复，需更换 Label。
     * "Fail"：导入失败。
     */
    private String Status;
    /**
     * 已存在的 Label 对应的导入作业的状态。
     * <p>
     * 这个字段只有在当 Status 为 "Label Already Exists" 是才会显示。
     * 用户可以通过这个状态，知晓已存在 Label 对应的导入作业的状态。
     * "RUNNING" 表示作业还在执行，"FINISHED" 表示作业成功。
     */
    private String ExistingJobStatus;
    /**
     * 导入错误信息。
     */
    private String Message;
    /**
     * 导入总处理的行数。
     */
    private long NumberTotalRows;
    /**
     * 成功导入的行数。
     */
    private long NumberLoadedRows;
    /**
     * 数据质量不合格的行数。
     */
    private int NumberFilteredRows;
    /**
     * 被 where 条件过滤的行数。
     */
    private int NumberUnselectedRows;
    /**
     * 导入的字节数。
     */
    private long LoadBytes;
    /**
     * 导入完成时间。单位毫秒。
     */
    private int LoadTimeMs;
    /**
     * 向Fe请求开始一个事务所花费的时间，单位毫秒。
     */
    private int BeginTxnTimeMs;
    /**
     * 向Fe请求获取导入数据执行计划所花费的时间，单位毫秒。
     */
    private int StreamLoadPutTimeMs;
    /**
     * 读取数据所花费的时间，单位毫秒。
     */
    private int ReadDataTimeMs;
    /**
     * 执行写入数据操作所花费的时间，单位毫秒。
     */
    private int WriteDataTimeMs;
    /**
     * 向Fe请求提交并且发布事务所花费的时间，单位毫秒。
     */
    private int CommitAndPublishTimeMs;
    /**
     * 如果有数据质量问题，通过访问这个 URL 查看具体错误行。
     */
    private String ErrorURL;
}
