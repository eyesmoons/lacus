package com.lacus.domain.quartz;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * 验证 RuleExecuteJob.parseRuleId 从 invokeTarget 解析 ruleId 的逻辑。
 */
public class RuleExecuteJobParseRuleIdTest {

    @Test
    public void parseRuleId_valid() {
        assertEquals(Long.valueOf(123), RuleExecuteJob.parseRuleId("ruleExecuteJob.execute(123)"));
    }

    @Test
    public void parseRuleId_nullOrBlank() {
        assertNull(RuleExecuteJob.parseRuleId(null));
        assertNull(RuleExecuteJob.parseRuleId(""));
    }

    @Test
    public void parseRuleId_noDigits() {
        assertNull(RuleExecuteJob.parseRuleId("ruleExecuteJob.execute()"));
    }
}
