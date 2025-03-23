package com.lacus.domain.metadata.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.CustomException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.datasource.api.DataSourcePlugin;
import com.lacus.datasource.manager.DataSourcePluginManager;
import com.lacus.domain.metadata.datasource.command.AddMetaDatasourceCommand;
import com.lacus.domain.metadata.datasource.command.UpdateMetaDatasourceCommand;
import com.lacus.domain.metadata.datasource.dto.MetaDatasourceDTO;
import com.lacus.domain.metadata.datasource.model.MetaDatasourceModel;
import com.lacus.domain.metadata.datasource.model.MetaDatasourceModelFactory;
import com.lacus.domain.metadata.datasource.query.DatasourceQuery;
import com.lacus.service.metadata.IMetaDataSourceService;
import com.lacus.utils.beans.MetaDatasource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DatasourceBusiness {

    @Autowired
    private IMetaDataSourceService metadataSourceService;

    @Autowired
    private DataSourcePluginManager dataSourcePluginManager;

    public PageDTO pageList(DatasourceQuery query) {
        Page<MetaDatasourceEntity> page = metadataSourceService.page(query.toPage(), query.toQueryWrapper());
        return new PageDTO(page.getRecords(), page.getTotal());
    }

    public void addDatasource(AddMetaDatasourceCommand addCommand) {
        MetaDatasourceModel model = MetaDatasourceModelFactory.loadFromAddCommand(addCommand, new MetaDatasourceModel());
        DataSourcePlugin processor = dataSourcePluginManager.getProcessor(model.getType().toUpperCase());
        if (ObjectUtils.isEmpty(processor)) {
            throw new CustomException("未找到合适的数据源适配器");
        }
        try {
            boolean testConnection = processor.testConnection(addCommand.getConnectionParams());
            if (testConnection) {
                model.checkDatasourceNameUnique(metadataSourceService);
                model.insert();
            } else {
                throw new CustomException("数据源连接失败");
            }
        } catch (Exception e) {
            throw new CustomException("数据源连接失败", e);
        }
    }

    public void updateDatasource(UpdateMetaDatasourceCommand updateCommand) {
        MetaDatasourceModel model = MetaDatasourceModelFactory.loadFromDb(updateCommand.getDatasourceId(), metadataSourceService);
        DataSourcePlugin processor = dataSourcePluginManager.getProcessor(model.getType());
        if (ObjectUtils.isEmpty(processor)) {
            throw new CustomException("未找到合适的数据源适配器");
        }
        try {
            boolean testConnection = processor.testConnection(updateCommand.getConnectionParams());
            if (testConnection) {
                model.loadUpdateCommand(updateCommand);
                model.checkDatasourceNameUnique(metadataSourceService);
                model.updateById();
            } else {
                throw new CustomException("数据源连接失败");
            }
        } catch (SQLException e) {
            throw new CustomException("数据源连接失败", e);
        }
    }

    public void removeDatasource(List<Long> datasourceIds) {
        metadataSourceService.removeBatchByIds(datasourceIds);
    }

    public MetaDatasourceDTO getDatasourceInfo(Long datasourceId) {
        MetaDatasourceEntity byId = metadataSourceService.getById(datasourceId);
        return new MetaDatasourceDTO(byId);
    }

    public Boolean testConnection(Long datasourceId) {
        MetaDatasourceModel model = MetaDatasourceModelFactory.loadFromDb(datasourceId, metadataSourceService);
        if (ObjectUtils.isEmpty(model)) {
            throw new ApiException(ErrorCode.Business.OBJECT_NOT_FOUND, datasourceId);
        }
        DataSourcePlugin processor = dataSourcePluginManager.getProcessor(model.getType());
        if (ObjectUtils.isEmpty(processor)) {
            throw new CustomException("未找到合适的数据源适配器");
        }
        try {
            return processor.testConnection(model.getConnectionParams());
        } catch (SQLException e) {
            log.error("数据源连接失败", e);
            return false;
        }
    }

    public Boolean changeStatus(Long datasourceId, Integer status) {
        MetaDatasourceModel model = MetaDatasourceModelFactory.loadFromDb(datasourceId, metadataSourceService);
        model.setStatus(status);
        return model.updateById();
    }

    public List<MetaDatasourceModel> getDatasourceList(String datasourceName, String sourceType) {
        List<MetaDatasourceEntity> datasourceList = metadataSourceService.getDatasourceList(datasourceName, sourceType);
        if (ObjectUtils.isNotEmpty(datasourceList)) {
            return datasourceList.stream().map(MetaDatasourceModel::new).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
