package com.lacus.domain.dataquality.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 可选规则下拉项（已过滤被 DQ 调度绑定的规则）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DqRuleOptionVO {

    private Long id;

    private String ruleName;
}
