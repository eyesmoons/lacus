package com.lacus.utils;

import com.lacus.enums.ResUploadType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;

import static com.lacus.common.constant.Constants.*;

/**
 * common utils
 */
@Slf4j
public class CommonUtils {

    private CommonUtils() {
        throw new UnsupportedOperationException("Construct CommonUtils");
    }

    private static String DEFAULT_DATA_QUALITY_JAR_PATH = null;

    /**
     * if upload resource is HDFS and kerberos startup is true , else false
     *
     * @return true if upload resource is HDFS and kerberos startup
     */
    public static boolean getKerberosStartupState() {
        String resUploadStartupType = PropertyUtils.getUpperCaseString(RESOURCE_STORAGE_TYPE);
        ResUploadType resUploadType = ResUploadType.valueOf(resUploadStartupType);
        Boolean kerberosStartupState = PropertyUtils.getBoolean(HADOOP_SECURITY_AUTHENTICATION_STARTUP_STATE, false);
        return resUploadType == ResUploadType.HDFS && kerberosStartupState;
    }

    /**
     * load kerberos configuration
     *
     * @param configuration
     * @return load kerberos config return true
     * @throws IOException errors
     */
    public static boolean loadKerberosConf(Configuration configuration) throws IOException {
        return loadKerberosConf(PropertyUtils.getString(JAVA_SECURITY_KRB5_CONF_PATH),
                PropertyUtils.getString(LOGIN_USER_KEY_TAB_USERNAME),
                PropertyUtils.getString(LOGIN_USER_KEY_TAB_PATH), configuration);
    }

    /**
     * load kerberos configuration
     *
     * @param javaSecurityKrb5Conf    javaSecurityKrb5Conf
     * @param loginUserKeytabUsername loginUserKeytabUsername
     * @param loginUserKeytabPath     loginUserKeytabPath
     * @throws IOException errors
     */
    public static synchronized void loadKerberosConf(String javaSecurityKrb5Conf,
                                                     String loginUserKeytabUsername,
                                                     String loginUserKeytabPath) throws IOException {
        Configuration configuration = new Configuration();
        configuration.setClassLoader(configuration.getClass().getClassLoader());
        loadKerberosConf(javaSecurityKrb5Conf, loginUserKeytabUsername, loginUserKeytabPath, configuration);
    }

    /**
     * load kerberos configuration
     *
     * @param javaSecurityKrb5Conf    javaSecurityKrb5Conf
     * @param loginUserKeytabUsername loginUserKeytabUsername
     * @param loginUserKeytabPath     loginUserKeytabPath
     * @param configuration           configuration
     * @return load kerberos config return true
     * @throws IOException errors
     */
    public static boolean loadKerberosConf(String javaSecurityKrb5Conf, String loginUserKeytabUsername,
                                           String loginUserKeytabPath, Configuration configuration) throws IOException {
        if (CommonUtils.getKerberosStartupState()) {
            System.setProperty(JAVA_SECURITY_KRB5_CONF, StringUtils.defaultIfBlank(javaSecurityKrb5Conf,
                    PropertyUtils.getString(JAVA_SECURITY_KRB5_CONF_PATH)));
            configuration.set(HADOOP_SECURITY_AUTHENTICATION, KERBEROS);
            UserGroupInformation.setConfiguration(configuration);
            UserGroupInformation.loginUserFromKeytab(
                    StringUtils.defaultIfBlank(loginUserKeytabUsername,
                            PropertyUtils.getString(LOGIN_USER_KEY_TAB_USERNAME)),
                    StringUtils.defaultIfBlank(loginUserKeytabPath, PropertyUtils.getString(LOGIN_USER_KEY_TAB_PATH)));
            return true;
        }
        return false;
    }

    /**
     * hdfs udf dir
     *
     * @param tenantCode tenant code
     * @return get udf dir on hdfs
     */
    public static String getHdfsUdfDir(String tenantCode) {
        return String.format("%s/udfs", getHdfsTenantDir(tenantCode));
    }

    /**
     * @param tenantCode tenant code
     * @return file directory of tenants on hdfs
     */
    public static String getHdfsTenantDir(String tenantCode) {
        return String.format("%s/%s", getHdfsDataBasePath(), tenantCode);
    }

    /**
     * get data hdfs path
     *
     * @return data hdfs path
     */
    public static String getHdfsDataBasePath() {
        String resourceUploadPath = PropertyUtils.getString(RESOURCE_UPLOAD_PATH, "/dolphinscheduler");
        if ("/".equals(resourceUploadPath)) {
            return "";
        } else {
            return resourceUploadPath;
        }
    }
}
