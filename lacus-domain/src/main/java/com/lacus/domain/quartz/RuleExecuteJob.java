package com.lacus.domain.quartz;

import com.lacus.domain.dataquality.DqTaskBusiness;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DQ 定时规则执行器 bean。
 *
 * <p>由 QuartzJobExecution 经 JobInvokeUtil#invokeMethod 反射调用。
 * invokeTarget 形如 {@code ruleExecuteJob.execute(123)}，JobInvokeUtil 解析出：
 * <ol>
 *   <li>第一个参数 ruleId = 123，因无后缀 L 被解析为 {@link Integer}；</li>
 *   <li>因 beanName 为 {@code ruleExecuteJob}，额外追加第二个参数 jobId（{@link String}）。</li>
 * </ol>
 *
 * <p>故 execute 签名必须为 {@code (Integer ruleId, String jobId)}，与 JobInvokeUtil 实际解析类型一致。
 * parseRuleId 返回 {@link Long} 与 {@link com.lacus.dao.dataquality.entity DqRuleEntity#getId} 类型一致，
 * 供 Task 5 做 ruleId 集合比较时不会因 Integer/Long 永不等而漏判。
 */
@Slf4j
@Component("ruleExecuteJob")
public class RuleExecuteJob {

    private static final Pattern RULE_ID_PATTERN = Pattern.compile("\\((\\d+)\\)");

    @Autowired
    private DqTaskBusiness dqTaskBusiness;

    /**
     * Quartz 到点调用。ruleId 由 JobInvokeUtil 从 invokeTarget 解析注入，
     * jobId 由 JobInvokeUtil 追加注入（仅用于日志）。
     *
     * @param ruleId 规则ID（invokeTarget 括号中的整数）
     * @param jobId  调度任务ID（Quartz 追加）
     */
    public void execute(Integer ruleId, String jobId) {
        log.info("RuleExecuteJob: trigger DQ ruleId={}, jobId={}", ruleId, jobId);
        dqTaskBusiness.submitTask(ruleId.longValue());
    }

    /**
     * 从 invokeTarget（形如 {@code ruleExecuteJob.execute(123)}）解析 ruleId。
     *
     * <p>用正则提取括号内的整数，返回 Long（与 DqRuleEntity.id 类型一致）；
     * 供 Task 5 在不触发执行的场景下复用。
     *
     * @param invokeTarget 调用目标字符串
     * @return ruleId，解析失败返回 null
     */
    public static Long parseRuleId(String invokeTarget) {
        if (StringUtils.isBlank(invokeTarget)) {
            return null;
        }
        Matcher m = RULE_ID_PATTERN.matcher(invokeTarget);
        if (m.find()) {
            try {
                return Long.valueOf(m.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
