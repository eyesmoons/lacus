package com.lacus.admin.controller.monitor;

import com.lacus.common.core.base.BaseController;
import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.domain.monitor.dashboard.DashboardBusiness;
import com.lacus.domain.monitor.dashboard.dto.DashboardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工作台仪表盘
 */
@RestController
@RequestMapping("/monitor")
public class DashboardController extends BaseController {

    @Autowired
    private DashboardBusiness dashboardBusiness;

    /**
     * 工作台聚合数据（登录即可访问，首页全员可见）
     */
    @GetMapping("/dashboard")
    public ResponseDTO<DashboardDTO> dashboard(@RequestParam(value = "days", defaultValue = "7") Integer days) {
        return ResponseDTO.ok(dashboardBusiness.getDashboard(days));
    }
}
