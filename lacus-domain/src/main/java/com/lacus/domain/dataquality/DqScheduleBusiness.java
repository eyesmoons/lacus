package com.lacus.domain.dataquality;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.dataquality.entity.DqRuleEntity;
import com.lacus.dao.quartz.entity.SysJob;
import com.lacus.domain.dataquality.command.DqScheduleCommand;
import com.lacus.domain.dataquality.query.DqScheduleQuery;
import com.lacus.domain.dataquality.vo.DqRuleOptionVO;
import com.lacus.domain.dataquality.vo.DqScheduleVO;
import com.lacus.domain.quartz.RuleExecuteJob;
import com.lacus.service.dataquality.IDqRuleService;
import com.lacus.service.quartz.ISysJobService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据质量调度业务逻辑（受控 CRUD + 1:1 校验）
 *
 * <p>包装 Quartz {@link ISysJobService}，固定 jobGroup='DQ'，
 * 负责 DQ 调度任务的增删改查、启停、手动触发，以及规则绑定唯一性校验。
 */
@Slf4j
@Service
public class DqScheduleBusiness {

    private static final String DQ_JOB_GROUP = "DQ";

    @Autowired
    private ISysJobService sysJobService;

    @Autowired
    private IDqRuleService dqRuleService;

    /**
     * 分页查询 DQ 调度任务（固定 jobGroup='DQ'）
     *
     * <p>由于 {@link ISysJobService#selectJobList(SysJob)} 无分页能力，
     * 按 jobGroup + 状态过滤后在内存中分页。
     *
     * @param q 查询条件（keyword 按任务名称模糊匹配，status 精确匹配）
     * @return 分页结果
     */
    public PageDTO list(DqScheduleQuery q) {
        SysJob queryJob = new SysJob();
        queryJob.setJobGroup(DQ_JOB_GROUP);
        if (StringUtils.isNotBlank(q.getKeyword())) {
            // selectJobList 的 XML 对 jobName 使用 LIKE CONCAT('%',#{jobName},'%')
            queryJob.setJobName(q.getKeyword());
        }
        if (StringUtils.isNotBlank(q.getStatus())) {
            queryJob.setStatus(q.getStatus());
        }
        List<SysJob> allJobs = sysJobService.selectJobList(queryJob);
        if (allJobs == null) {
            allJobs = Collections.emptyList();
        }

        // 按 createTime 降序（null 排最后）
        allJobs.sort((a, b) -> {
            Date ta = a.getCreateTime();
            Date tb = b.getCreateTime();
            if (ta == null && tb == null) return 0;
            if (ta == null) return 1;
            if (tb == null) return -1;
            return tb.compareTo(ta);
        });

        int total = allJobs.size();
        int pageNum = q.getPageNum() != null ? q.getPageNum() : 1;
        int pageSize = q.getPageSize() != null ? q.getPageSize() : 10;
        int fromIndex = Math.min((pageNum - 1) * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<SysJob> pageJobs = fromIndex < toIndex ? allJobs.subList(fromIndex, toIndex) : Collections.emptyList();

        // 预加载规则名称映射
        Map<Long, String> ruleNameMap = loadRuleNameMap();

        List<DqScheduleVO> voList = pageJobs.stream()
                .map(this::toScheduleVO)
                .peek(vo -> vo.setRuleName(ruleNameMap.get(vo.getRuleId())))
                .collect(Collectors.toList());
        return new PageDTO(voList, (long) total);
    }

    /**
     * 查询单个调度任务详情
     *
     * @param jobId 任务ID
     * @return 调度详情
     */
    public DqScheduleVO detail(Long jobId) {
        SysJob job = fetchDqJobOrThrow(jobId);
        DqScheduleVO vo = toScheduleVO(job);
        vo.setRuleName(loadRuleNameMap().get(vo.getRuleId()));
        return vo;
    }

    /**
     * 校验 cron 表达式是否合法
     *
     * @param cronExpression cron 表达式
     * @return 是否合法
     */
    public boolean validateCron(String cronExpression) {
        return sysJobService.checkCronExpressionIsValid(cronExpression);
    }

    /**
     * 新增调度任务
     *
     * <p>1:1 校验：同一规则仅可绑定一个 DQ 调度；cron 表达式合法性校验。
     *
     * @param cmd 命令
     * @return 新增任务ID
     */
    public Long add(DqScheduleCommand cmd) throws SchedulerException {
        if (listAllDqJobRuleIds().contains(cmd.getRuleId())) {
            throw new CustomException("该规则已被调度绑定，同一规则仅可绑定一个调度");
        }
        if (!sysJobService.checkCronExpressionIsValid(cmd.getCronExpression())) {
            throw new CustomException("cron表达式无效：" + cmd.getCronExpression());
        }
        SysJob job = SysJob.builder()
                .jobName(cmd.getJobName())
                .jobGroup(DQ_JOB_GROUP)
                .invokeTarget("ruleExecuteJob.execute(" + cmd.getRuleId() + ")")
                .cronExpression(cmd.getCronExpression())
                .misfirePolicy(StringUtils.defaultIfBlank(cmd.getMisfirePolicy(), "3"))
                .concurrent(StringUtils.defaultIfBlank(cmd.getConcurrent(), "1"))
                .status(StringUtils.defaultIfBlank(cmd.getStatus(), "NORMAL"))
                .remark(cmd.getRemark())
                .build();
        sysJobService.insertJob(job);
        // MyBatis-Plus IdType.AUTO 回填 jobId
        return job.getJobId();
    }

    /**
     * 编辑调度任务
     *
     * @param cmd 命令（含 jobId）
     */
    public void edit(DqScheduleCommand cmd) throws SchedulerException {
        SysJob job = sysJobService.selectJobById(cmd.getJobId());
        if (job == null || !DQ_JOB_GROUP.equals(job.getJobGroup())) {
            throw new CustomException("调度不存在");
        }
        job.setJobName(cmd.getJobName());
        job.setCronExpression(cmd.getCronExpression());
        job.setMisfirePolicy(StringUtils.defaultIfBlank(cmd.getMisfirePolicy(), "3"));
        job.setConcurrent(StringUtils.defaultIfBlank(cmd.getConcurrent(), "1"));
        job.setStatus(StringUtils.defaultIfBlank(cmd.getStatus(), "NORMAL"));
        job.setRemark(cmd.getRemark());
        sysJobService.updateJob(job);
    }

    /**
     * 暂停任务
     *
     * @param jobId 任务ID
     */
    public void pause(Long jobId) throws SchedulerException {
        SysJob job = fetchDqJobOrThrow(jobId);
        job.setStatus("PAUSE");
        sysJobService.pauseJob(job);
    }

    /**
     * 恢复任务
     *
     * @param jobId 任务ID
     */
    public void resume(Long jobId) throws SchedulerException {
        SysJob job = fetchDqJobOrThrow(jobId);
        job.setStatus("NORMAL");
        sysJobService.resumeJob(job);
    }

    /**
     * 立即运行一次
     *
     * @param jobId 任务ID
     */
    public void run(Long jobId) throws SchedulerException {
        SysJob job = fetchDqJobOrThrow(jobId);
        sysJobService.run(job);
    }

    /**
     * 删除任务
     *
     * @param jobId 任务ID
     */
    public void delete(Long jobId) throws SchedulerException {
        SysJob job = fetchDqJobOrThrow(jobId);
        sysJobService.deleteJob(job);
    }

    /**
     * 可选规则列表（启用规则中过滤已被 DQ 调度绑定的）
     *
     * @return 可选规则下拉项
     */
    public List<DqRuleOptionVO> optionalRules() {
        Set<Long> boundRuleIds = listAllDqJobRuleIds();
        LambdaQueryWrapper<DqRuleEntity> w = new LambdaQueryWrapper<>();
        w.eq(DqRuleEntity::getEnabled, 1);
        return dqRuleService.list(w).stream()
                .filter(r -> !boundRuleIds.contains(r.getId()))
                .map(r -> new DqRuleOptionVO(r.getId(), r.getRuleName()))
                .collect(Collectors.toList());
    }

    // ==================== 私有方法 ====================

    /**
     * 查询所有 DQ 调度任务已绑定的 ruleId 集合
     */
    private Set<Long> listAllDqJobRuleIds() {
        SysJob queryJob = new SysJob();
        queryJob.setJobGroup(DQ_JOB_GROUP);
        List<SysJob> jobs = sysJobService.selectJobList(queryJob);
        if (jobs == null) {
            return Collections.emptySet();
        }
        return jobs.stream()
                .map(j -> RuleExecuteJob.parseRuleId(j.getInvokeTarget()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 根据 jobId 查询并校验属于 DQ 组，不存在或非 DQ 组则抛异常
     */
    private SysJob fetchDqJobOrThrow(Long jobId) {
        SysJob job = sysJobService.selectJobById(jobId);
        if (job == null || !DQ_JOB_GROUP.equals(job.getJobGroup())) {
            throw new CustomException("调度不存在");
        }
        return job;
    }

    /**
     * SysJob 转 DqScheduleVO（ruleId 从 invokeTarget 解析）
     */
    private DqScheduleVO toScheduleVO(SysJob job) {
        DqScheduleVO vo = new DqScheduleVO();
        vo.setJobId(job.getJobId());
        vo.setJobName(job.getJobName());
        vo.setRuleId(RuleExecuteJob.parseRuleId(job.getInvokeTarget()));
        vo.setCronExpression(job.getCronExpression());
        vo.setMisfirePolicy(job.getMisfirePolicy());
        vo.setConcurrent(job.getConcurrent());
        vo.setStatus(job.getStatus());
        vo.setRemark(job.getRemark());
        vo.setCreateTime(job.getCreateTime());
        return vo;
    }

    /**
     * 加载启用规则的 id→ruleName 映射（供列表展示规则名称）
     */
    private Map<Long, String> loadRuleNameMap() {
        LambdaQueryWrapper<DqRuleEntity> w = new LambdaQueryWrapper<>();
        w.eq(DqRuleEntity::getEnabled, 1);
        return dqRuleService.list(w).stream()
                .collect(Collectors.toMap(DqRuleEntity::getId, DqRuleEntity::getRuleName, (a, b) -> a));
    }
}
