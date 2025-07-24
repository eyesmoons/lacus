package com.lacus.domain.dig;

import com.lacus.dao.dig.entity.StJobInstanceEntity;
import com.lacus.service.dig.IStJobInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StExecuteBusiness {

    @Autowired
    private StJobInstanceBusiness stJobInstanceBusiness;

    public void start(Long jobId) {
        // 1. create job instance
        StJobInstanceEntity instance = stJobInstanceBusiness.createInstance(jobId);
        // 2. generate job config to file according to the job config
        // 2. execute job by seatunnel client
    }
}
