package com.lacus.admin.controller.dig;

import com.lacus.domain.dig.StJobBusiness;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "数据集成任务定义", tags = {"数据集成任务定义管理"})
@RestController
@RequestMapping("/st/job")
public class StJobDefineController {

    @Autowired
    private StJobBusiness stJobBusiness;

}
