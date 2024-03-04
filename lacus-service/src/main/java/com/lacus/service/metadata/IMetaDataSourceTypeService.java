package com.lacus.service.metadata;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.metadata.entity.MetaDatasourceTypeEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface IMetaDataSourceTypeService extends IService<MetaDatasourceTypeEntity> {
    List<MetaDatasourceTypeEntity> listDatasourceType(String typeName);
    boolean isTypeNameDuplicated(Long typeId, String typeName);
}
