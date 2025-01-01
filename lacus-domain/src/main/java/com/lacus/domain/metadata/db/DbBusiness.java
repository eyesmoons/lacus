package com.lacus.domain.metadata.db;

import com.lacus.dao.metadata.entity.MetaDbEntity;
import com.lacus.domain.metadata.db.model.MetaDbModel;
import com.lacus.service.metadata.IMetaDbService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DbBusiness {

    @Autowired
    private IMetaDbService metaDbService;

    public List<MetaDbModel> listByDatasourceId(Long datasourceId) {
        List<MetaDbEntity> list = metaDbService.listByDatasourceId(datasourceId);
        if (ObjectUtils.isNotEmpty(list)) {
            return list.stream().map(MetaDbModel::new).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
