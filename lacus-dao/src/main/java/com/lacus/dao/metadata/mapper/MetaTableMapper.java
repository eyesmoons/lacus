package com.lacus.dao.metadata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lacus.dao.metadata.entity.MetaDbTableEntity;
import com.lacus.dao.metadata.entity.MetaTableEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MetaTableMapper extends BaseMapper<MetaTableEntity> {
    List<MetaDbTableEntity> listMetaDbTable(@Param("datasourceId") Long datasourceId, @Param("dbName") String dbName, @Param("tableName") String tableName);

    List<MetaDbTableEntity> listMetaTable(@Param("list") List<MetaDbTableEntity> list);
}
