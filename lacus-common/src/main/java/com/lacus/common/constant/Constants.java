package com.lacus.common.constant;


import org.apache.commons.lang3.SystemUtils;

import java.time.Duration;

/**
 * 通用常量信息
 */
public class Constants {

    public static final int KB = 1024;

    public static final int MB = KB * 1024;

    public static final int GB = MB * 1024;

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";


    public static class Token {
        /**
         * 令牌
         */
        public static final String TOKEN_FIELD = "token";

        /**
         * 令牌前缀
         */
        public static final String TOKEN_PREFIX = "Bearer ";

        /**
         * 令牌前缀
         */
        public static final String LOGIN_USER_KEY = "login_user_key";

    }

    public static class Captcha {
        /**
         * 令牌
         */
        public static final String MATH_TYPE = "math";

        /**
         * 令牌前缀
         */
        public static final String CHAR_TYPE = "char";

    }

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "profile";

    public static class UploadSubDir {
        public static final String IMPORT_PATH = "import";
        public static final String AVATAR_PATH = "avatar";
        public static final String DOWNLOAD_PATH = "download";
        public static final String UPLOAD_PATH = "upload";
    }

    public static final String DEFAULT_HDFS = "hdfs.defaultFS";
    public static final String HADOOP_USER_NAME = "HADOOP_USER_NAME";
    public static final String HADOOP_USER = "hdfs";
    public static final String HDFS_SITE_XML = "hdfs-site.xml";
    public static final String CORE_SITE_XML = "core-site.xml";
    public static final String YARN_SITE_XML = "yarn-site.xml";
    public static final String FLINK_CONF_YAML = "flink-conf.yaml";
    public static final String HDFS_DEFAULT_FS = "fs.defaultFS";
    public static final String HADOOP_RM_STATE_ACTIVE = "ACTIVE";
    public static final String COMMON_PROPERTIES_PATH = "/common.properties";
    public static final String FORMAT_SS = "%s%s";
    public static final String FORMAT_S_S = "%s/%s";
    public static final String FORMAT_S_S_COLON = "%s:%s";
    public static final String FOLDER_SEPARATOR = "/";
    public static final String RESOURCE_TYPE_FILE = "resources";
    public static final String RESOURCE_TYPE_UDF = "udfs";
    public static final String EMPTY_STRING = "";
    public static final String FS_DEFAULT_FS = "resource.hdfs.fs.defaultFS";
    public static final String HADOOP_RESOURCE_MANAGER_HTTPADDRESS_PORT = "resource.manager.httpaddress.port";
    public static final String YARN_RESOURCEMANAGER_HA_RM_IDS = "yarn.resourcemanager.ha.rm.ids";
    public static final String YARN_APPLICATION_STATUS_ADDRESS = "yarn.application.status.address";
    public static final String YARN_JOB_HISTORY_STATUS_ADDRESS = "yarn.job.history.status.address";
    public static final String HDFS_ROOT_USER = "resource.hdfs.root.user";
    public static final String RESOURCE_UPLOAD_PATH = "resource.storage.upload.base.path";
    public static final String DATA_BASEDIR_PATH = "data.basedir.path";
    public static final String RESOURCE_VIEW_SUFFIXES = "resource.view.suffixs";
    public static final String RESOURCE_VIEW_SUFFIXES_DEFAULT_VALUE = "txt,log,sh,bat,conf,cfg,py,java,sql,xml,hql,properties,json,yml,yaml,ini,js";
    public static final String SUDO_ENABLE = "sudo.enable";
    public static final String RESOURCE_STORAGE_TYPE = "resource.storage.type";
    public static final String APPID_COLLECT = "appId.collect";
    public static final String DEFAULT_COLLECT_WAY = "log";
    public static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String PERIOD = ".";
    public static final String QUESTION = "?";
    public static final String SPACE = " ";
    public static final String SINGLE_SLASH = "/";
    public static final String DOUBLE_SLASH = "//";
    public static final String AT_SIGN = "@";
    public static final String SLASH = "/";
    public static final String SEMICOLON = ";";
    public static final String ADDRESS = "address";
    public static final String DATABASE = "database";
    public static final String OTHER = "other";
    public static final String USER = "user";
    public static final String JDBC_URL = "jdbcUrl";
    public static final String IMPORT_SUFFIX = "_import_";
    public static final String COPY_SUFFIX = "_copy_";
    public static final int HTTP_CONNECT_TIMEOUT = 60 * 1000;
    public static final int HTTP_CONNECTION_REQUEST_TIMEOUT = 60 * 1000;
    public static final int SOCKET_TIMEOUT = 60 * 1000;
    public static final String HTTP_HEADER_UNKNOWN = "unKnown";
    public static final String HTTP_X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String HTTP_X_REAL_IP = "X-Real-IP";
    public static final String UTF_8 = "UTF-8";
    public static final int READ_PERMISSION = 2;
    public static final int WRITE_PERMISSION = 2 * 2;
    public static final int EXECUTE_PERMISSION = 1;
    public static final int DEFAULT_ADMIN_PERMISSION = 7;
    public static final int DEFAULT_HASH_MAP_SIZE = 16;
    public static final int ALL_PERMISSIONS = READ_PERMISSION | WRITE_PERMISSION | EXECUTE_PERMISSION;
    public static final int MAX_TASK_TIMEOUT = 24 * 3600;
    public static final String FLOWNODE_RUN_FLAG_FORBIDDEN = "FORBIDDEN";
    public static final String FLOWNODE_RUN_FLAG_NORMAL = "NORMAL";
    public static final String COMMON_TASK_TYPE = "common";
    public static final String DEFAULT = "default";
    public static final String PASSWORD = "password";
    public static final String XXXXXX = "******";
    public static final String NULL = "NULL";
    public static final String THREAD_NAME_MASTER_SERVER = "Master-Server";
    public static final String THREAD_NAME_WORKER_SERVER = "Worker-Server";
    public static final String THREAD_NAME_ALERT_SERVER = "Alert-Server";
    public static final String CRC_SUFFIX = ".crc";
    public static final String DEFAULT_CRON_STRING = "0 0 0 * * ? *";
    public static final long SLEEP_TIME_MILLIS = 1_000L;
    public static final long SLEEP_TIME_MILLIS_SHORT = 100L;
    public static final Duration SERVER_CLOSE_WAIT_TIME = Duration.ofSeconds(3);
    public static final String JAR = "jar";
    public static final String HADOOP = "hadoop";
    public static final String D = "-D";
    public static final int EXIT_CODE_SUCCESS = 0;
    public static final int EXIT_CODE_FAILURE = -1;
    public static final int DEFINITION_FAILURE = -1;
    public static final int OPPOSITE_VALUE = -1;
    public static final int VERSION_FIRST = 1;
    public static final String FAILED = "FAILED";
    public static final String RUNNING = "RUNNING";
    public static final String UNDERLINE = "_";
    public static final String PID = SystemUtils.IS_OS_WINDOWS ? "handle" : "pid";
    public static final String STAR = "*";
    public static final char N = 'N';
    public static final String GLOBAL_PARAMS = "globalParams";
    public static final String LOCAL_PARAMS = "localParams";
    public static final String SUBPROCESS_INSTANCE_ID = "subProcessInstanceId";
    public static final String PROCESS_INSTANCE_STATE = "processInstanceState";
    public static final String PARENT_WORKFLOW_INSTANCE = "parentWorkflowInstance";
    public static final String CONDITION_RESULT = "conditionResult";
    public static final String SWITCH_RESULT = "switchResult";
    public static final String WAIT_START_TIMEOUT = "waitStartTimeout";
    public static final String DEPENDENCE = "dependence";
    public static final String TASK_LIST = "taskList";
    public static final String QUEUE = "queue";
    public static final String QUEUE_NAME = "queueName";
    public static final int LOG_QUERY_SKIP_LINE_NUMBER = 0;
    public static final int LOG_QUERY_LIMIT = 4096;
    public static final String ALIAS = "alias";
    public static final String CONTENT = "content";
    public static final String DEPENDENT_SPLIT = ":||";
    public static final long DEPENDENT_ALL_TASK_CODE = -1;
    public static final long DEPENDENT_WORKFLOW_CODE = 0;
    public static final int PREVIEW_SCHEDULE_EXECUTE_COUNT = 5;
    public static final String KERBEROS = "kerberos";
    public static final String KERBEROS_EXPIRE_TIME = "kerberos.expire.time";
    public static final String JAVA_SECURITY_KRB5_CONF = "java.security.krb5.conf";
    public static final String JAVA_SECURITY_KRB5_CONF_PATH = "java.security.krb5.conf.path";
    public static final String HADOOP_SECURITY_AUTHENTICATION = "hadoop.security.authentication";
    public static final String HADOOP_SECURITY_AUTHENTICATION_STARTUP_STATE = "hadoop.security.authentication.startup.state";
    public static final String LOGIN_USER_KEY_TAB_USERNAME = "login.user.keytab.username";
    public static final String LOGIN_USER_KEY_TAB_PATH = "login.user.keytab.path";
    public static final String SSO_LOGIN_USER_STATE = "sso.login.user.state";
    public static final String WORKFLOW_INSTANCE_ID_MDC_KEY = "workflowInstanceId";
    public static final String TASK_INSTANCE_ID_MDC_KEY = "taskInstanceId";
    public static final String DOUBLE_BRACKETS_LEFT = "{{";
    public static final String DOUBLE_BRACKETS_RIGHT = "}}";
    public static final String DOUBLE_BRACKETS_LEFT_SPACE = "{ {";
    public static final String DOUBLE_BRACKETS_RIGHT_SPACE = "} }";
    public static final String STATUS = "status";
    public static final String MSG = "msg";
    public static final String COUNT = "count";
    public static final String PAGE_SIZE = "pageSize";
    public static final String PAGE_NUMBER = "pageNo";
    public static final String DATA_LIST = "data";
    public static final String TOTAL_LIST = "totalList";
    public static final String TOTAL_PAGE = "totalPage";
    public static final String TOTAL = "total";
    public static final int SESSION_TIME_OUT = 7200;
    public static final int MAX_FILE_SIZE = 1024 * 1024 * 1024;
    public static final String UDF = "UDF";

    public static final String SYSTEM_LINE_SEPARATOR = System.lineSeparator();
    public static final String SH = "sh";
    public static final int RESOURCE_FULL_NAME_MAX_LENGTH = 128;
    public static final String STRING_TRUE = "true";
    public static final String STRING_FALSE = "false";
    public static final String STRING_YES = "YES";
    public static final String STRING_NO = "NO";
    public static final String SMALL = "small";
    public static final String CHANGE = "change";
    public static final String PARAMETER_DATETIME = "system.datetime";
    public static final String PARAMETER_FORMAT_TIME = "yyyyMMddHHmmss";
    public static final String PARAMETER_SHECDULE_TIME = "schedule.time";
}