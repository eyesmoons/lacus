package com.lacus.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;

import static com.lacus.common.constant.Constants.HADOOP_SECURITY_AUTHENTICATION;
import static com.lacus.common.constant.Constants.HADOOP_SECURITY_AUTHENTICATION_STARTUP_STATE;
import static com.lacus.common.constant.Constants.JAVA_SECURITY_KRB5_CONF;
import static com.lacus.common.constant.Constants.JAVA_SECURITY_KRB5_CONF_PATH;
import static com.lacus.common.constant.Constants.KERBEROS;
import static com.lacus.common.constant.Constants.LOGIN_USER_KEY_TAB_PATH;
import static com.lacus.common.constant.Constants.LOGIN_USER_KEY_TAB_USERNAME;

/**
 * common utils
 */
@Slf4j
public class CommonUtils {

    private CommonUtils() {
        throw new UnsupportedOperationException("Construct CommonUtils");
    }

    /**
     * if upload resource is HDFS and kerberos startup is true , else false
     *
     * @return true if upload resource is HDFS and kerberos startup
     */
    public static boolean getKerberosStartupState() {
        return CommonPropertyUtils.getBoolean(HADOOP_SECURITY_AUTHENTICATION_STARTUP_STATE, false);
    }

    /**
     * load kerberos configuration
     *
     * @param configuration configuration
     * @return load kerberos config return true
     * @throws IOException errors
     */
    public static boolean loadKerberosConf(Configuration configuration) throws IOException {
        return loadKerberosConf(CommonPropertyUtils.getString(JAVA_SECURITY_KRB5_CONF_PATH),
                CommonPropertyUtils.getString(LOGIN_USER_KEY_TAB_USERNAME),
                CommonPropertyUtils.getString(LOGIN_USER_KEY_TAB_PATH), configuration);
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
                    CommonPropertyUtils.getString(JAVA_SECURITY_KRB5_CONF_PATH)));
            configuration.set(HADOOP_SECURITY_AUTHENTICATION, KERBEROS);
            UserGroupInformation.setConfiguration(configuration);
            UserGroupInformation.loginUserFromKeytab(
                    StringUtils.defaultIfBlank(loginUserKeytabUsername,
                            CommonPropertyUtils.getString(LOGIN_USER_KEY_TAB_USERNAME)),
                    StringUtils.defaultIfBlank(loginUserKeytabPath, CommonPropertyUtils.getString(LOGIN_USER_KEY_TAB_PATH)));
            return true;
        }
        return false;
    }
}
