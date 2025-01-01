package com.lacus.domain.metadata.datasourceType;

import com.lacus.dao.metadata.entity.MetaDatasourceTypeEntity;
import com.lacus.dao.metadata.enums.DatasourceCatalogEnum;
import com.lacus.domain.metadata.datasourceType.command.AddMetaDatasourceTypeCommand;
import com.lacus.domain.metadata.datasourceType.command.UpdateMetaDatasourceTypeCommand;
import com.lacus.domain.metadata.datasourceType.dto.DatasourceCatalogDTO;
import com.lacus.domain.metadata.datasourceType.dto.DatasourceTypeDTO;
import com.lacus.domain.metadata.datasourceType.model.MetaDatasourceTypeModel;
import com.lacus.domain.metadata.datasourceType.model.MetaDatasourceTypeModelFactory;
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
public class DatasourceTypeBusiness {

    @Autowired
    private IMetaDataSourceTypeService dataSourceTypeService;

    public List<MetaDatasourceTypeModel> list(String typeName) {
        List<MetaDatasourceTypeEntity> list = dataSourceTypeService.listDatasourceType(typeName);
        if (ObjectUtils.isNotEmpty(list)) {
            return list.stream().map(entity -> new MetaDatasourceTypeModel(entity)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public void add(AddMetaDatasourceTypeCommand addCommand) {
        MetaDatasourceTypeModel model = MetaDatasourceTypeModelFactory.loadFromAddCommand(addCommand, new MetaDatasourceTypeModel());
        model.checkDatasourceTypeNameUnique(dataSourceTypeService);
        model.insert();
    }

    public void update(UpdateMetaDatasourceTypeCommand updateCommand) {
        MetaDatasourceTypeModel model = MetaDatasourceTypeModelFactory.loadFromDb(updateCommand.getTypeId(), dataSourceTypeService);
        model.loadUpdateCommand(updateCommand);
        model.checkDatasourceTypeNameUnique(dataSourceTypeService);
        model.updateById();
    }

    public void remove(List<Long> datasourceTypeIds) {
        dataSourceTypeService.removeBatchByIds(datasourceTypeIds);
    }

    public DatasourceTypeDTO getDatasourceTypeInfo(Long typeId) {
        MetaDatasourceTypeEntity byId = dataSourceTypeService.getById(typeId);
        return new DatasourceTypeDTO(byId);
    }

    public List<DatasourceCatalogDTO> listDatasourceCatalog() {
        List<DatasourceCatalogDTO> list = new ArrayList<>();
        DatasourceCatalogEnum[] values = DatasourceCatalogEnum.values();
        for (DatasourceCatalogEnum value : values) {
            DatasourceCatalogDTO dto = new DatasourceCatalogDTO();
            dto.setName(value.getName());
            dto.setRemark(value.getRemark());
            list.add(dto);
        }
        return list;
    }
}
