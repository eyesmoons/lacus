package com.lacus.domain.dataquality;

import com.lacus.dao.dataquality.entity.DqRuleEntity;
import com.lacus.dao.quartz.entity.SysJob;
import com.lacus.domain.dataquality.command.DqScheduleCommand;
import com.lacus.domain.dataquality.vo.DqRuleOptionVO;
import com.lacus.service.dataquality.IDqRuleService;
import com.lacus.service.quartz.ISysJobService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 验证 DqScheduleBusiness 的 CRUD + 1:1 校验逻辑。
 */
public class DqScheduleBusinessTest {

    private final DqScheduleBusiness business = new DqScheduleBusiness();
    private final ISysJobService sysJobService = mock(ISysJobService.class);
    private final IDqRuleService dqRuleService = mock(IDqRuleService.class);

    {
        ReflectionTestUtils.setField(business, "sysJobService", sysJobService);
        ReflectionTestUtils.setField(business, "dqRuleService", dqRuleService);
    }

    // ==================== add ====================

    @Test
    public void add_success_returnsJobId() throws Exception {
        // 无已绑定规则
        when(sysJobService.selectJobList(any(SysJob.class))).thenReturn(Collections.emptyList());
        // cron 合法
        when(sysJobService.checkCronExpressionIsValid("0 0 1 * * ?")).thenReturn(true);
        // insertJob 后回填 jobId（模拟 MyBatis-Plus IdType.AUTO）
        when(sysJobService.insertJob(any(SysJob.class))).thenAnswer(invocation -> {
            SysJob job = invocation.getArgument(0);
            job.setJobId(100L);
            return 1;
        });

        DqScheduleCommand cmd = new DqScheduleCommand();
        cmd.setJobName("调度A");
        cmd.setRuleId(5L);
        cmd.setCronExpression("0 0 1 * * ?");

        Long jobId = business.add(cmd);
        assertEquals(Long.valueOf(100L), jobId);
        verify(sysJobService).insertJob(any(SysJob.class));
    }

    @Test(expected = com.lacus.common.exception.CustomException.class)
    public void add_rejectsAlreadyBoundRuleId() throws Exception {
        // 已存在绑定 ruleId=5 的 DQ 调度
        SysJob boundJob = SysJob.builder()
                .jobId(10L)
                .jobGroup("DQ")
                .invokeTarget("ruleExecuteJob.execute(5)")
                .build();
        when(sysJobService.selectJobList(any(SysJob.class))).thenReturn(Collections.singletonList(boundJob));

        DqScheduleCommand cmd = new DqScheduleCommand();
        cmd.setJobName("调度A");
        cmd.setRuleId(5L);
        cmd.setCronExpression("0 0 1 * * ?");

        business.add(cmd);
    }

    @Test(expected = com.lacus.common.exception.CustomException.class)
    public void add_rejectsInvalidCron() throws Exception {
        when(sysJobService.selectJobList(any(SysJob.class))).thenReturn(Collections.emptyList());
        when(sysJobService.checkCronExpressionIsValid("not-a-cron")).thenReturn(false);

        DqScheduleCommand cmd = new DqScheduleCommand();
        cmd.setJobName("调度A");
        cmd.setRuleId(5L);
        cmd.setCronExpression("not-a-cron");

        business.add(cmd);
    }

    // ==================== edit ====================

    @Test
    public void edit_success_updatesAndCallsUpdateJob() throws Exception {
        SysJob job = SysJob.builder()
                .jobId(10L).jobGroup("DQ").status("NORMAL")
                .jobName("旧名称").cronExpression("0 0 0 * * ?")
                .build();
        when(sysJobService.selectJobById(10L)).thenReturn(job);

        DqScheduleCommand cmd = new DqScheduleCommand();
        cmd.setJobId(10L);
        cmd.setJobName("新名称");
        cmd.setRuleId(5L);
        cmd.setCronExpression("0 0 1 * * ?");
        cmd.setMisfirePolicy("1");
        cmd.setConcurrent("0");
        cmd.setStatus("PAUSE");
        cmd.setRemark("新备注");

        business.edit(cmd);
        verify(sysJobService).updateJob(job);
        assertEquals("新名称", job.getJobName());
        assertEquals("0 0 1 * * ?", job.getCronExpression());
        assertEquals("1", job.getMisfirePolicy());
        assertEquals("0", job.getConcurrent());
        assertEquals("PAUSE", job.getStatus());
        assertEquals("新备注", job.getRemark());
    }

    @Test(expected = com.lacus.common.exception.CustomException.class)
    public void edit_throwsWhenJobNotFound() throws Exception {
        when(sysJobService.selectJobById(999L)).thenReturn(null);

        DqScheduleCommand cmd = new DqScheduleCommand();
        cmd.setJobId(999L);
        cmd.setJobName("调度A");
        cmd.setRuleId(5L);
        cmd.setCronExpression("0 0 1 * * ?");

        business.edit(cmd);
    }

    @Test(expected = com.lacus.common.exception.CustomException.class)
    public void edit_throwsWhenJobNotInDqGroup() throws Exception {
        SysJob job = SysJob.builder().jobId(10L).jobGroup("OTHER").status("NORMAL").build();
        when(sysJobService.selectJobById(10L)).thenReturn(job);

        DqScheduleCommand cmd = new DqScheduleCommand();
        cmd.setJobId(10L);
        cmd.setJobName("调度A");
        cmd.setRuleId(5L);
        cmd.setCronExpression("0 0 1 * * ?");

        business.edit(cmd);
    }

    // ==================== optionalRules ====================

    @Test
    public void optionalRules_filtersBoundRules() {
        // ruleId=5 已绑定
        SysJob boundJob = SysJob.builder()
                .jobId(10L)
                .jobGroup("DQ")
                .invokeTarget("ruleExecuteJob.execute(5)")
                .build();
        when(sysJobService.selectJobList(any(SysJob.class))).thenReturn(Collections.singletonList(boundJob));

        // 启用规则：5（已绑定）、6（未绑定）
        DqRuleEntity rule5 = new DqRuleEntity();
        rule5.setId(5L);
        rule5.setRuleName("规则5");
        rule5.setEnabled(1);
        DqRuleEntity rule6 = new DqRuleEntity();
        rule6.setId(6L);
        rule6.setRuleName("规则6");
        rule6.setEnabled(1);
        when(dqRuleService.list(any())).thenReturn(Arrays.asList(rule5, rule6));

        List<DqRuleOptionVO> options = business.optionalRules();
        assertEquals(1, options.size());
        assertEquals(Long.valueOf(6L), options.get(0).getId());
        assertEquals("规则6", options.get(0).getRuleName());
    }

    // ==================== pause / resume / run / delete ====================

    @Test
    public void pause_fetchThenAct() throws Exception {
        SysJob job = SysJob.builder().jobId(10L).jobGroup("DQ").status("NORMAL").build();
        when(sysJobService.selectJobById(10L)).thenReturn(job);

        business.pause(10L);
        verify(sysJobService).pauseJob(job);
        assertEquals("PAUSE", job.getStatus());
    }

    @Test
    public void resume_fetchThenAct() throws Exception {
        SysJob job = SysJob.builder().jobId(10L).jobGroup("DQ").status("PAUSE").build();
        when(sysJobService.selectJobById(10L)).thenReturn(job);

        business.resume(10L);
        verify(sysJobService).resumeJob(job);
        assertEquals("NORMAL", job.getStatus());
    }

    @Test
    public void run_fetchThenAct() throws Exception {
        SysJob job = SysJob.builder().jobId(10L).jobGroup("DQ").status("NORMAL").build();
        when(sysJobService.selectJobById(10L)).thenReturn(job);

        business.run(10L);
        verify(sysJobService).run(job);
    }

    @Test
    public void delete_fetchThenAct() throws Exception {
        SysJob job = SysJob.builder().jobId(10L).jobGroup("DQ").status("NORMAL").build();
        when(sysJobService.selectJobById(10L)).thenReturn(job);

        business.delete(10L);
        verify(sysJobService).deleteJob(job);
    }

    @Test(expected = com.lacus.common.exception.CustomException.class)
    public void pause_throwsWhenJobNotFound() throws Exception {
        when(sysJobService.selectJobById(999L)).thenReturn(null);
        business.pause(999L);
    }

    @Test(expected = com.lacus.common.exception.CustomException.class)
    public void delete_throwsWhenJobNotInDqGroup() throws Exception {
        SysJob job = SysJob.builder().jobId(10L).jobGroup("OTHER").status("NORMAL").build();
        when(sysJobService.selectJobById(10L)).thenReturn(job);
        business.delete(10L);
    }
}
