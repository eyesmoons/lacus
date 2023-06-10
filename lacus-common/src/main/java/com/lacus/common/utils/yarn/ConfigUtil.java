package com.lacus.common.utils.yarn;

import com.lacus.common.utils.PropertiesUtil;
import com.lacus.common.utils.hdfs.HdfsUtil;
import org.apache.hadoop.conf.Configuration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Map;
import java.util.Properties;

public class ConfigUtil {

    private static final String HDFS_SITE_XML = "hdfs-site.xml";
    private static final String CORE_SITE_XML = "core-site.xml";
    private static final String YARN_SITE_XML = "yarn-site.xml";
    private static final String FLINK_CONF_YAML = "flink-conf.yaml";

    public static  void initConfig(Configuration conf,String pathPrefix) {
        String separator = separator(pathPrefix);
        try {
            conf.addResource(new ByteArrayInputStream(HdfsUtil.readFile(pathPrefix + separator + HDFS_SITE_XML).getBytes()));
            conf.addResource(new ByteArrayInputStream(HdfsUtil.readFile( pathPrefix + separator + CORE_SITE_XML).getBytes()));
            conf.addResource(new ByteArrayInputStream(HdfsUtil.readFile( pathPrefix + separator + YARN_SITE_XML).getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("初始化资源错误,检查配置文件是否存在:hdfs-site.xml/core-site.xml/yarn-site.xml");
        }
    }

    public static org.apache.flink.configuration.Configuration getFlinkConf(String pathPrefix){
        try{
            org.apache.flink.configuration.Configuration config = new org.apache.flink.configuration.Configuration();;
            String separator = separator(pathPrefix);
            Properties properties = PropertiesUtil.loadPropertiesByStr(HdfsUtil.readFile(pathPrefix + separator + FLINK_CONF_YAML));
            for(Map.Entry<Object,Object> props : properties.entrySet()){
                config.setString(String.valueOf(props.getKey()),String.valueOf(props.getValue()));
            }
            return config;
        }catch (Exception e){
            throw new RuntimeException("初始化资源错误,检查配置文件是否存在:hdfs-site.xml/core-site.xml/yarn-site.xml");
        }
    }

    public static String separator(String path){
        String separator = File.separator;
        if(path.endsWith(separator)){
            separator = "";
        }
        return separator;
    }
}
