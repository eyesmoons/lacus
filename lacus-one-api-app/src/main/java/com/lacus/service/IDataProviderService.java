package com.lacus.service;

import com.lacus.service.dto.ApiConfigDTO;
import com.lacus.service.dto.DataResult;

import java.util.Map;

public interface IDataProviderService {

    DataResult provider(Long datasourceId, ApiConfigDTO apiConfig, Map<String, Object> params);

}
