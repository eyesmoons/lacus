package com.lacus.domain.metadata.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.CustomException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.common.utils.beans.MetaDatasource;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.dao.metadata.entity.MetaDatasourceTypeEntity;
import com.lacus.domain.metadata.datasource.command.AddMetaDatasourceCommand;
import com.lacus.domain.metadata.datasource.command.UpdateMetaDatasourceCommand;
import com.lacus.domain.metadata.datasource.dto.MetaDatasourceDTO;
import com.lacus.domain.metadata.datasource.factory.MetaDatasourceFactory;
import com.lacus.domain.metadata.datasource.model.MetaDatasourceModel;
import com.lacus.domain.metadata.datasource.model.MetaDatasourceModelFactory;
import com.lacus.domain.metadata.datasource.procesors.IDatasourceProcessor;
import com.lacus.domain.metadata.datasource.query.DatasourceQuery;
import com.lacus.service.metadata.IMetaDataSourceService;
import com.lacus.service.metadata.IMetaDataSourceTypeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DatasourceService {

    @Autowired
    private IMetaDataSourceService metadataSourceService;

    @Autowired
    private IMetaDataSourceTypeService dataSourceTypeService;

    @Autowired
    private MetaDatasourceFactory metaDatasourceFactory;

    public PageDTO pageList(DatasourceQuery query) {
        Page<MetaDatasourceEntity> page = metadataSourceService.page(query.toPage(), query.toQueryWrapper());
        page.getRecords().forEach(item -> item.setPassword("******"));
        return new PageDTO(page.getRecords(), page.getTotal());
    }

    public void addDatasource(AddMetaDatasourceCommand addCommand) {
        MetaDatasourceModel model = MetaDatasourceModelFactory.loadFromAddCommand(addCommand, new MetaDatasourceModel());
        IDatasourceProcessor processor = metaDatasourceFactory.getProcessor(model.getType());
        if (ObjectUtils.isEmpty(processor)) {
            throw new CustomException("未找到合适的数据源适配器");
        }
        MetaDatasourceTypeEntity metaDatasourceTypeEntity = dataSourceTypeService.getByDatasourceName(model.getType());
        MetaDatasource datasource = new MetaDatasource();
        datasource.setIp(model.getIp());
        datasource.setPort(model.getPort());
        datasource.setDbName(model.getDefaultDbName());
        datasource.setUser(model.getUsername());
        datasource.setPassword(model.getPassword());
        datasource.setDriver(metaDatasourceTypeEntity.getDriverName());
        if (processor.testDatasourceConnection(datasource)) {
            model.checkDatasourceNameUnique(metadataSourceService);
            model.insert();
        } else {
            throw new CustomException("数据源连接失败");
        }
    }

    public void updateDatasource(UpdateMetaDatasourceCommand updateCommand) {
        MetaDatasourceModel model = MetaDatasourceModelFactory.loadFromDb(updateCommand.getDatasourceId(), metadataSourceService);
        IDatasourceProcessor processor = metaDatasourceFactory.getProcessor(model.getType());
        if (ObjectUtils.isEmpty(processor)) {
            throw new CustomException("未找到合适的数据源适配器");
        }
        MetaDatasourceTypeEntity metaDatasourceTypeEntity = dataSourceTypeService.getByDatasourceName(model.getType());
        MetaDatasource datasource = new MetaDatasource();
        datasource.setIp(model.getIp());
        datasource.setPort(model.getPort());
        datasource.setDbName(model.getDefaultDbName());
        datasource.setUser(model.getUsername());
        datasource.setPassword(model.getPassword());
        datasource.setDriver(metaDatasourceTypeEntity.getDriverName());
        if (processor.testDatasourceConnection(datasource)) {
            model.loadUpdateCommand(updateCommand);
            model.checkDatasourceNameUnique(metadataSourceService);
            model.updateById();
        } else {
            throw new CustomException("数据源连接失败");
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
        MetaDatasourceTypeEntity metaDatasourceTypeEntity = dataSourceTypeService.getByDatasourceName(model.getType());
        MetaDatasource datasource = new MetaDatasource();
        datasource.setIp(model.getIp());
        datasource.setPort(model.getPort());
        datasource.setDbName(model.getDefaultDbName());
        datasource.setUser(model.getUsername());
        datasource.setPassword(model.getPassword());
        datasource.setDriver(metaDatasourceTypeEntity.getDriverName());
        IDatasourceProcessor processor = metaDatasourceFactory.getProcessor(model.getType());
        if (ObjectUtils.isEmpty(processor)) {
            throw new CustomException("未找到合适的数据源适配器");
        }
        return processor.testDatasourceConnection(datasource);
    }

    public Boolean changeStatus(Long datasourceId, Integer status) {
        MetaDatasourceModel model = MetaDatasourceModelFactory.loadFromDb(datasourceId, metadataSourceService);
        model.setStatus(status);
        return model.updateById();
    }

    public List<MetaDatasourceModel> getDatasourceList(String datasourceName, String sourceType) {
        List<MetaDatasourceEntity> datasourceList = metadataSourceService.getDatasourceList(datasourceName, sourceType);
        if (ObjectUtils.isNotEmpty(datasourceList)) {
            return datasourceList.stream().map(entity -> {
                MetaDatasourceModel model = new MetaDatasourceModel(entity);
                model.setPassword("******");
                return model;
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
