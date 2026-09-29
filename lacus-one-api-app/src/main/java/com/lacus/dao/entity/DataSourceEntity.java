package com.lacus.dao.entity;

import com.lacus.common.enums.DataSourceStatusEnum;
import com.lacus.common.enums.DatabaseType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataSourceEntity {

    private Long datasourceId;


    private String datasourceName;

    private String connectionParams;

    public DatabaseType databaseType;

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
