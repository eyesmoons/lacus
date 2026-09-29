package com.lacus.domain.dataquality;

import com.lacus.dao.dataquality.entity.DqRuleEntity;
import com.lacus.domain.monitor.alert.AlertCenterBusiness;
import com.lacus.domain.monitor.alert.AlertCenterModels;
import com.lacus.service.dataquality.IDqRuleService;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * 验证 DqTaskBusiness.triggerAlertOnFailure 的告警触发逻辑。
 */
public class DqTaskBusinessAlertTest {

    private final DqTaskBusiness dqTaskBusiness = new DqTaskBusiness();

    private final AlertCenterBusiness alertCenterBusiness = Mockito.mock(AlertCenterBusiness.class);

    private final IDqRuleService dqRuleService = Mockito.mock(IDqRuleService.class);

    {
        ReflectionTestUtils.setField(dqTaskBusiness, "alertCenterBusiness", alertCenterBusiness);
        ReflectionTestUtils.setField(dqTaskBusiness, "dqRuleService", dqRuleService);
    }

    private void invokeTriggerAlertOnFailure(DqRuleEntity rule, Long logId, String errorMsg) throws Exception {
        Method method = DqTaskBusiness.class.getDeclaredMethod(
                "triggerAlertOnFailure", DqRuleEntity.class, Long.class, String.class);
        method.setAccessible(true);
        method.invoke(dqTaskBusiness, rule, logId, errorMsg);
    }

    @Test
    public void triggerAlertOnFailure_skipsWhenNoAlertGroup() throws Exception {
        DqRuleEntity rule = new DqRuleEntity();
        rule.setId(1L);
        rule.setRuleName("r");
        rule.setAlertGroupCode(null);
        invokeTriggerAlertOnFailure(rule, 10L, "boom");
        verify(alertCenterBusiness, never()).execute(any());
    }

    @Test
    public void triggerAlertOnFailure_invokesExecuteWhenGroupPresent() throws Exception {
        DqRuleEntity rule = new DqRuleEntity();
        rule.setId(1L);
        rule.setRuleName("r");
        rule.setTableName("t");
        rule.setFieldNames("f");
        rule.setAlertGroupCode("G1");
        invokeTriggerAlertOnFailure(rule, 10L, "boom");
        ArgumentCaptor<AlertCenterModels.AlertExecuteCommand> cap = ArgumentCaptor.forClass(AlertCenterModels.AlertExecuteCommand.class);
        verify(alertCenterBusiness).execute(cap.capture());
        assertEquals("G1", cap.getValue().getGroupCode());
        assertEquals("DQ_SCHEDULE", cap.getValue().getTriggerSource());
        assertEquals("WARN", cap.getValue().getAlertLevel());
    }

    @Test
    public void triggerAlertOnFailure_swallowsExceptionWhenGroupInvalid() throws Exception {
        DqRuleEntity rule = new DqRuleEntity();
        rule.setId(1L);
        rule.setRuleName("r");
        rule.setAlertGroupCode("BAD");
        doThrow(new RuntimeException("group not found")).when(alertCenterBusiness).execute(any());
        // 不应抛异常
        invokeTriggerAlertOnFailure(rule, 10L, "boom");
        verify(alertCenterBusiness).execute(any());
    }
}
