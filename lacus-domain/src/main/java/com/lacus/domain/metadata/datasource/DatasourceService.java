package com.lacus.domain.metadata.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.common.utils.beans.MetaDatasource;
import com.lacus.common.utils.sql.JdbcUtil;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.domain.metadata.datasource.command.AddMetaDatasourceCommand;
import com.lacus.domain.metadata.datasource.command.UpdateMetaDatasourceCommand;
import com.lacus.domain.metadata.datasource.dto.MetaDatasourceDTO;
import com.lacus.domain.metadata.datasource.model.MetaDatasourceModel;
import com.lacus.domain.metadata.datasource.model.MetaDatasourceModelFactory;
import com.lacus.domain.metadata.datasource.query.DatasourceQuery;
import com.lacus.service.metadata.IMetaDataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DatasourceService {

    @Autowired
    private IMetaDataSourceService metadataSourceService;

    public PageDTO pageList(DatasourceQuery query) {
        Page<MetaDatasourceEntity> page = metadataSourceService.page(query.toPage(), query.toQueryWrapper());
        page.getRecords().forEach(item -> item.setPassword("******"));
        return new PageDTO(page.getRecords(), page.getTotal());
    }

    public void addDatasource(AddMetaDatasourceCommand addCommand) {
        MetaDatasourceModel model = MetaDatasourceModelFactory.loadFromAddCommand(addCommand, new MetaDatasourceModel());
        model.checkDatasourceNameUnique(metadataSourceService);
        model.insert();
    }

    public void updateDatasource(UpdateMetaDatasourceCommand updateCommand) {
        MetaDatasourceModel model = MetaDatasourceModelFactory.loadFromDb(updateCommand.getDatasourceId(), metadataSourceService);
        model.loadUpdateCommand(updateCommand);
        model.checkDatasourceNameUnique(metadataSourceService);
        model.updateById();
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
        MetaDatasource datasource = new MetaDatasource();
        datasource.setIp(model.getIp());
        datasource.setPort(model.getPort());
        datasource.setDbName(model.getDefaultDbName());
        datasource.setUser(model.getUsername());
        datasource.setPassword(model.getPassword());
        try {
            JdbcUtil.executeQuery(datasource, "select 1");
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    public Boolean changeStatus(Long datasourceId, Integer status) {
        MetaDatasourceModel model = MetaDatasourceModelFactory.loadFromDb(datasourceId, metadataSourceService);
        model.setStatus(status);
        return model.updateById();
    }

    public List<MetaDatasourceModel> getDatasourceList(String datasourceName) {
        List<MetaDatasourceEntity> datasourceList = metadataSourceService.getDatasourceList(datasourceName);
        if (ObjectUtils.isNotEmpty(datasourceList)) {
            return datasourceList.stream().map((Function<MetaDatasourceEntity, MetaDatasourceModel>) entity -> {
                MetaDatasourceModel model = new MetaDatasourceModel(entity);
                model.setPassword("******");
                return model;
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
