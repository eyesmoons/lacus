package com.lacus.domain.dig;

import com.lacus.service.dig.IStJobInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StJobInstanceBusiness {

    @Autowired
    private IStJobInstanceService stJobInstanceService;
}
