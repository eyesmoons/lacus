package com.lacus.dao.entity;

import com.lacus.common.enums.DataSourceStatusEnum;
import com.lacus.common.enums.DatabaseType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataSourceEntity {

    private Long id;


    private String name;

    /**
     * 数据源类型
     */
    private DatabaseType databaseType;

    /**
     * ip/域名
     */
    private String ip;

    /**
     * port
     */
    private String port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 状态，0：未知，1：可用，2，不可用
     */
    private DataSourceStatusEnum status;

    private String defaultDbName;


    /**
     * 自定义参数
     */
    private String customParams;




    @Getter
    @Setter
    public static class CustomParamPair {

        /**
         * key
         */
        private String key;

        /**
         * value
         */
        private String value;

    }

}
