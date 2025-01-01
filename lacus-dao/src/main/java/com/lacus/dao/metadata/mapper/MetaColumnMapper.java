package com.lacus.dao.metadata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lacus.dao.metadata.entity.MetaColumnEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MetaColumnMapper extends BaseMapper<MetaColumnEntity> {
    List<MetaColumnEntity> selectColumnsByTableName(@Param("datasourceId") Long datasourceId, @Param("dbName") String dbName, @Param("tableName") String tableName);
}
