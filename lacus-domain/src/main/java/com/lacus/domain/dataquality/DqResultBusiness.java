package com.lacus.domain.dataquality;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.core.page.PageDTO;
import com.lacus.domain.dataquality.query.DqExecutionLogQuery;
import com.lacus.service.dataquality.IDqExecutionLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据质量执行结果查询业务逻辑
 */
@Slf4j
@Service
public class DqResultBusiness {

    @Autowired
    private IDqExecutionLogService dqExecutionLogService;

    /**
     * 分页查询执行记录
     */
    @SuppressWarnings("unchecked")
    public PageDTO pageList(DqExecutionLogQuery query) {
        Page<?> page = dqExecutionLogService.page(query.toPage(), query.toQueryWrapper());
        return new PageDTO(page.getRecords(), page.getTotal());
    }
}
