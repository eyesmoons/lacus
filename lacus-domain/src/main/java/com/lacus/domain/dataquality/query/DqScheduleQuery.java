package com.lacus.domain.dataquality.query;

import lombok.Data;

/**
 * 数据质量调度分页查询条件
 */
@Data
public class DqScheduleQuery {

    /**
     * 关键词（按任务名称模糊匹配）
     */
    private String keyword;

    /**
     * 状态（NORMAL/PAUSE）
     */
    private String status;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
