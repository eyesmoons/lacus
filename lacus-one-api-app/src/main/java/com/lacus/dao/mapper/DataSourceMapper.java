package com.lacus.dao.mapper;

import com.lacus.dao.entity.DataSourceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

public interface DataSourceMapper {

    DataSourceEntity getDatasourceById(@Param("id") Long id);
}
