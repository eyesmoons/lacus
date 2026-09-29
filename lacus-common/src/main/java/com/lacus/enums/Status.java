package com.lacus.enums;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.Optional;

public enum Status {
    SUCCESS(200, "success", "成功"),
    INTERNAL_SERVER_ERROR_ARGS(10000, "Internal Server Error: {0}", "服务端异常: {0}"),
    REQUEST_PARAMS_NOT_VALID_ERROR(10001, "request parameter {0} is not valid", "请求参数[{0}]无效"),
    TASK_TIMEOUT_PARAMS_ERROR(10002, "task timeout parameter is not valid", "任务超时参数无效"),
    USER_NAME_EXIST(10003, "user name already exists", "用户名已存在"),
    USER_NAME_NULL(10004, "user name is null", "用户名不能为空"),
    HDFS_OPERATION_ERROR(10006, "hdfs operation error", "hdfs操作错误"),
    TASK_INSTANCE_NOT_FOUND(10008, "task instance not found", "任务实例不存在"),
    USER_NOT_EXIST(10010, "user {0} not exists", "用户[{0}]不存在"),
    DATASOURCE_EXIST(10015, "data source name already exists", "数据源名称已存在"),
    DATASOURCE_CONNECT_FAILED(10016, "data source connection failed", "建立数据源连接失败"),
    TASK_INSTANCE_NOT_EXISTS(10020, "task instance {0} does not exist", "任务实例[{0}]不存在"),
    CREATE_DATASOURCE_ERROR(10033, "create datasource error", "创建数据源错误"),
    UPDATE_DATASOURCE_ERROR(10034, "update datasource error", "更新数据源错误"),
    QUERY_DATASOURCE_ERROR(10035, "query datasource error", "查询数据源错误"),
    CONNECT_DATASOURCE_FAILURE(10036, "connect datasource failure", "建立数据源连接失败"),
    CONNECTION_TEST_FAILURE(10037, "connection test failure", "测试数据源连接失败"),
    DELETE_DATA_SOURCE_FAILURE(10038, "delete data source failure", "删除数据源失败"),
    VERIFY_DATASOURCE_NAME_FAILURE(10039, "verify datasource name failure", "验证数据源名称失败"),
    UNAUTHORIZED_DATASOURCE(10040, "unauthorized datasource", "未经授权的数据源"),
    AUTHORIZED_DATA_SOURCE(10041, "authorized data source", "授权数据源失败"),
    QUERY_RESOURCES_LIST_ERROR(10056, "query resources list error", "查询资源列表错误"),
    QUERY_RESOURCES_LIST_PAGING(10057, "query resources list paging", "分页查询资源列表错误"),
    DELETE_RESOURCE_ERROR(10058, "delete resource error", "删除资源错误"),
    VERIFY_RESOURCE_BY_NAME_AND_TYPE_ERROR(10059, "verify resource by name and type error", "资源名称或类型验证错误"),
    VIEW_RESOURCE_FILE_ON_LINE_ERROR(10060, "view resource file online error", "查看资源文件错误"),
    CREATE_RESOURCE_FILE_ON_LINE_ERROR(10061, "create resource file online error", "创建资源文件错误"),
    RESOURCE_FILE_IS_EMPTY(10062, "resource file is empty", "资源文件内容不能为空"),
    EDIT_RESOURCE_FILE_ON_LINE_ERROR(10063, "edit resource file online error", "更新资源文件错误"),
    DOWNLOAD_RESOURCE_FILE_ERROR(10064, "download resource file error", "下载资源文件错误"),
    CREATE_UDF_FUNCTION_ERROR(10065, "create udf function error", "创建UDF函数错误"),
    VIEW_UDF_FUNCTION_ERROR(10066, "view udf function error", "查询UDF函数错误"),
    UPDATE_UDF_FUNCTION_ERROR(10067, "update udf function error", "更新UDF函数错误"),
    QUERY_UDF_FUNCTION_LIST_PAGING_ERROR(10068, "query udf function list paging error", "分页查询UDF函数列表错误"),
    UDF_FUNCTION_NOT_EXIST(20001, "UDF function not found", "UDF函数不存在"),
    UDF_FUNCTION_EXISTS(20002, "UDF function already exists", "UDF函数已存在"),
    RESOURCE_NOT_EXIST(20004, "resource not exist", "资源不存在"),
    RESOURCE_EXIST(20005, "resource already exists", "资源已存在"),
    VERIFY_PARAMETER_NAME_FAILED(1300009, "The file name verify failed", "文件命名校验失败"),
    RESOURCE_FULL_NAME_TOO_LONG_ERROR(1300015, "resource's fullname is too long error", "资源文件名过长"),
    RESOURCE_SUFFIX_FORBID_CHANGE(20008, "resource suffix not allowed to be modified", "资源文件后缀不支持修改"),
    UDF_RESOURCE_SUFFIX_NOT_JAR(20009, "UDF resource suffix name must be jar", "UDF资源文件后缀名只支持[jar]"),
    RESOURCE_SIZE_EXCEED_LIMIT(20007, "upload resource file size exceeds limit", "上传资源文件大小超过限制"),
    ;
    private final int code;
    private final String enMsg;
    private final String zhMsg;

    Status(int code, String enMsg, String zhMsg) {
        this.code = code;
        this.enMsg = enMsg;
        this.zhMsg = zhMsg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        if (Locale.SIMPLIFIED_CHINESE.getLanguage().equals(LocaleContextHolder.getLocale().getLanguage())) {
            return this.zhMsg;
        } else {
            return this.enMsg;
        }
    }

    /**
     * Retrieve Status enum entity by status code.
     */
    public static Optional<Status> findStatusBy(int code) {
        for (Status status : Status.values()) {
            if (code == status.getCode()) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}
