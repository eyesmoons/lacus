package com.lacus.domain.dig;

import com.lacus.service.dig.IStJobService;
import com.lacus.service.dig.IStTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StJobBusiness {

    @Autowired
    private IStTaskService stTaskService;

    @Autowired
    private IStJobService stJobService;
}
