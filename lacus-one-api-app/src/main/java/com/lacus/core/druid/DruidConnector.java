package com.lacus.core.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.alibaba.fastjson2.JSONArray;
import com.google.common.collect.Maps;
import com.lacus.common.exception.ResultCode;
import com.lacus.common.exception.ApiException;
import com.lacus.common.utils.DecryptUtils;
import com.lacus.dao.entity.DataSourceEntity;
import com.lacus.common.enums.DatabaseType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Slf4j
public class DruidConnector {

    private static final DruidConnector druidConnector = new DruidConnector();

    public static DruidConnector getInstance() {
        return druidConnector;
    }

    private DruidConnector() {
    }

    private DataSourceEntity dataSourceInfo;

    private static final ConcurrentMap<Long, DruidDataSource> datasourceMap = new ConcurrentHashMap<>();

    private static final String VALIDATION_QUERY = "SELECT 99";

    private DruidDataSource getDatasource(Long datasourceId) {
        return datasourceMap.get(datasourceId);
    }

    public Connection getConnect(DataSourceEntity dataSourceInfo) {
        long start = System.currentTimeMillis();
        this.dataSourceInfo = dataSourceInfo;
        Long datasourceId = dataSourceInfo.getId();
        DruidDataSource druidDataSource;
        if (datasourceMap.containsKey(datasourceId)) {
            druidDataSource = getDatasource(datasourceId);
        } else {
            synchronized (datasourceId) {
                druidDataSource = createDatasource();
            }
        }
        try {
            Connection conn = druidDataSource.getConnection();
            if (conn == null) {
                druidDataSource.close();
                datasourceMap.remove(datasourceId);
                log.info("Druid连接池:{}未获取到连接，关闭当前连接池", druidDataSource.getName());
            }
            log.info("数据库建立连接时间：{}ms", System.currentTimeMillis() - start);
            return conn;
        } catch (Exception e) {
            log.info("数据源连接获取失败:{}", e.getMessage());
            throw new ApiException(ResultCode.DATASOURCE_CANNOT_GET_CONNECT);
        }
    }

    private DruidDataSource createDatasource() {
        JSONArray customParams = JSONArray.parse(dataSourceInfo.getCustomParams());
        Properties prop = null;
        Map<String, String> confMap = Maps.newHashMap();
        if (!customParams.isEmpty()) {
            Map<String, String> customParamPairs = customParams
                    .toJavaList(DataSourceEntity
                            .CustomParamPair.class)
                    .stream()
                    .collect(Collectors.toMap(DataSourceEntity.CustomParamPair::getKey, DataSourceEntity.CustomParamPair::getValue));
            prop = new Properties();
            prop.putAll(customParamPairs);
        }
        DatabaseType databaseType = dataSourceInfo.getDatabaseType();
        confMap.put(DruidDataSourceFactory.PROP_DRIVERCLASSNAME, databaseType.getDriverClassName());
        String urlFormat = databaseType.getUrlFormat();
        String realUrl = String.format(urlFormat, dataSourceInfo.getIp(), dataSourceInfo.getPort(), dataSourceInfo.getDefaultDbName());
        confMap.put(DruidDataSourceFactory.PROP_URL, realUrl);
        confMap.put(DruidDataSourceFactory.PROP_USERNAME, dataSourceInfo.getUsername());
        String password = dataSourceInfo.getPassword();
        if (StringUtils.isNotBlank(password)) {
            confMap.put(DruidDataSourceFactory.PROP_PASSWORD, DecryptUtils.decryptPwd(password));
        }
        // 初始化链接数
        confMap.put(DruidDataSourceFactory.PROP_INITIALSIZE, "5");
        confMap.put(DruidDataSourceFactory.PROP_MINIDLE, "5");
        confMap.put(DruidDataSourceFactory.PROP_MAXACTIVE, "20");
        confMap.put(DruidDataSourceFactory.PROP_MAXWAIT, "10000");
        // 检查空闲连接的频率，单位毫秒, 非正整数时表示不进行检查
        confMap.put(DruidDataSourceFactory.PROP_TIMEBETWEENEVICTIONRUNSMILLIS, "600000");
        confMap.put(DruidDataSourceFactory.PROP_TESTWHILEIDLE, "true");
        confMap.put(DruidDataSourceFactory.PROP_VALIDATIONQUERY, VALIDATION_QUERY);
        confMap.put(DruidDataSourceFactory.PROP_TESTONBORROW, "true"); //检测链接是否可用
        //druid监控页面开启
        confMap.put(DruidDataSourceFactory.PROP_FILTERS, "stat");
        //druid回收机制
        confMap.put(DruidDataSourceFactory.PROP_REMOVEABANDONED, "true");
        confMap.put(DruidDataSourceFactory.PROP_REMOVEABANDONEDTIMEOUT, "1800");
        confMap.put(DruidDataSourceFactory.PROP_LOGABANDONED, "true");
        try {
            DruidDataSourceBuilder.create().build();
            DruidDataSource druidDS = (DruidDataSource) DruidDataSourceFactory.createDataSource(confMap);
            if (prop != null) {
                druidDS.setConnectProperties(prop);
            }
            druidDS.setBreakAfterAcquireFailure(true);
            druidDS.setConnectionErrorRetryAttempts(5);
            druidDS.setName(dataSourceInfo.getName());
            datasourceMap.put(dataSourceInfo.getId(), druidDS);
            return druidDS;
        } catch (Exception e) {
            log.info("数据源连接池初始化失败:{}", e.getMessage());
            throw new ApiException(ResultCode.DATASOURCE_INIT_ERROR);
        }
    }

}
