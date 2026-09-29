package com.lacus.domain.dataquality;

import com.lacus.dao.dataquality.entity.DqCheckResultEntity;
import com.lacus.dao.dataquality.entity.DqCheckResultVO;
import com.lacus.dao.dataquality.entity.DqRuleTemplateEntity;
import com.lacus.service.dataquality.IDqCheckResultService;
import com.lacus.service.dataquality.IDqRuleTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据质量检测结果查询业务逻辑
 */
@Slf4j
@Service
public class DqCheckResultBusiness {

    @Autowired
    private IDqCheckResultService dqCheckResultService;

    @Autowired
    private IDqRuleTemplateService dqRuleTemplateService;

    // ===== 期望值类型中文映射 =====
    private static final Map<String, String> EXPECTED_TYPE_LABEL = new HashMap<>();
    static {
        EXPECTED_TYPE_LABEL.put("FIXED",           "固定值");
        EXPECTED_TYPE_LABEL.put("DAILY_AVG",       "日均值");
        EXPECTED_TYPE_LABEL.put("WEEKLY_AVG",      "周均值");
        EXPECTED_TYPE_LABEL.put("MONTHLY_AVG",     "月均值");
        EXPECTED_TYPE_LABEL.put("LAST_7_DAYS_AVG", "近7日均值");
        EXPECTED_TYPE_LABEL.put("LAST_30_DAYS_AVG","近30日均值");
    }

    // ===== 校验方式中文映射 =====
    private static final Map<String, String> CHECK_METHOD_LABEL = new HashMap<>();
    static {
        CHECK_METHOD_LABEL.put("fixed_check",           "固定值比较");
        CHECK_METHOD_LABEL.put("fluctuation_check",     "波动率比较");
        CHECK_METHOD_LABEL.put("expected_minus_actual", "期望值 − 实际值");
        CHECK_METHOD_LABEL.put("actual_minus_expected", "实际值 − 期望值");
        CHECK_METHOD_LABEL.put("actual_div_expected",   "实际值 ÷ 期望值");
    }

    /**
     * 根据执行记录ID查询检测结果列表（含中文展示字段）
     */
    public List<DqCheckResultVO> listByLogId(Long logId) {
        List<DqCheckResultEntity> entities = dqCheckResultService.listByLogId(logId);
        if (entities == null || entities.isEmpty()) return new java.util.ArrayList<>();

        // 批量查模板名（按 templateCode 去重查）
        Map<String, String> templateNameMap = buildTemplateNameMap(entities);

        return entities.stream().map(e -> {
            DqCheckResultVO vo = DqCheckResultVO.from(e);
            // 模板名（key 统一大写匹配）
            if (e.getTemplateCode() != null) {
                vo.setTemplateName(templateNameMap.getOrDefault(e.getTemplateCode().toUpperCase(), e.getTemplateCode()));
            }
            // 期望类型中文
            if (e.getExpectedType() != null) {
                vo.setExpectedTypeLabel(
                    EXPECTED_TYPE_LABEL.getOrDefault(e.getExpectedType().toUpperCase(), e.getExpectedType()));
            }
            // 校验方式中文
            if (e.getCheckMethod() != null) {
                vo.setCheckMethodLabel(
                    CHECK_METHOD_LABEL.getOrDefault(e.getCheckMethod().toLowerCase(), e.getCheckMethod()));
            }
            return vo;
        }).collect(Collectors.toList());
    }

    private Map<String, String> buildTemplateNameMap(List<DqCheckResultEntity> entities) {
        Map<String, String> result = new HashMap<>();
        try {
            List<DqRuleTemplateEntity> templates = dqRuleTemplateService.list();
            if (templates != null) {
                templates.forEach(t -> {
                    if (t.getTemplateCode() != null && t.getTemplateName() != null) {
                        result.put(t.getTemplateCode().toUpperCase(), t.getTemplateName());
                    }
                });
            }
        } catch (Exception e) {
            log.warn("Failed to load template names for check result VO", e);
        }
        return result;
    }
}
