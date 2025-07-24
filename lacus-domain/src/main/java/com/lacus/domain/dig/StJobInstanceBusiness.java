package com.lacus.domain.dig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.dig.entity.StJobEntity;
import com.lacus.dao.dig.entity.StJobInstanceEntity;
import com.lacus.domain.dig.helper.StPluginDiscoveryHelper;
import com.lacus.service.dig.IStJobInstanceService;
import com.lacus.service.dig.IStJobService;
import com.lacus.service.dig.IStTaskRelationService;
import com.lacus.service.dig.IStTaskService;
import com.lacus.service.metadata.IMetaDataSourceService;
import org.apache.seatunnel.api.common.PluginIdentifier;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.common.constants.PluginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class StJobInstanceBusiness {

    @Autowired
    private IStJobService stJobService;

    @Autowired
    private IStTaskService stTaskService;

    @Autowired
    private IStTaskRelationService stTaskRelationService;

    @Autowired
    private IStJobInstanceService stJobInstanceService;

    @Autowired
    private IMetaDataSourceService dataSourceService;

    public StJobInstanceEntity createInstance(Long jobId) {
        try {
            Map<PluginType, LinkedHashMap<PluginIdentifier, OptionRule>> allConnectors = StPluginDiscoveryHelper.getAllConnectors();
//            allConnectors.forEach((key, value) -> allConnectorOptionRule.putAll(value));
            StJobEntity job = stJobService.getById(jobId);
            String jobConfig = createJobConfig(jobId);
        } catch (Exception e) {
            throw new CustomException("Create job instance failed", e);
        }
        // TODO
        return null;
    }

    private String createJobConfig(Long jobId) throws JsonProcessingException {
        return null;
    }
}
