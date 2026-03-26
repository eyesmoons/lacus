package com.lacus.service.quartz.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.lacus.dao.quartz.entity.SysJobLog;
import com.lacus.dao.quartz.mapper.SysJobLogMapper;
import com.lacus.service.quartz.ISysJobLogService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时任务调度日志信息 服务层
 */
@Service
public class SysJobLogServiceImpl implements ISysJobLogService {
    @Autowired
    private SysJobLogMapper jobLogMapper;

    /**
     * 获取quartz调度器日志的计划任务
     *
     * @param jobLog 调度日志信息
     * @return 调度任务日志集合
     */
    @Override
    public List<SysJobLog> selectJobLogList(SysJobLog jobLog) {
        LambdaQueryWrapper<SysJobLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectUtils.isNotEmpty(jobLog.getJobGroup()), SysJobLog::getJobGroup, jobLog.getJobGroup());
        wrapper.like(ObjectUtils.isNotEmpty(jobLog.getJobName()), SysJobLog::getJobName, jobLog.getJobName());
        wrapper.eq(ObjectUtils.isNotEmpty(jobLog.getStatus()), SysJobLog::getStatus, jobLog.getStatus());
        wrapper.like(ObjectUtils.isNotEmpty(jobLog.getInvokeTarget()), SysJobLog::getInvokeTarget, jobLog.getInvokeTarget());
        wrapper.orderByDesc((SFunction<SysJobLog, Object>) SysJobLog::getCreateTime);
        return jobLogMapper.selectList(wrapper);
    }

    /**
     * 通过调度任务日志ID查询调度信息
     *
     * @param jobLogId 调度任务日志ID
     * @return 调度任务日志对象信息
     */
    @Override
    public SysJobLog selectJobLogById(Long jobLogId) {
        return jobLogMapper.selectById(jobLogId);
    }

    /**
     * 新增任务日志
     *
     * @param jobLog 调度日志信息
     */
    @Override
    public void addJobLog(SysJobLog jobLog) {
        jobLogMapper.insert(jobLog);
    }

    /**
     * 批量删除调度日志信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteJobLogByIds(String ids) {
        return jobLogMapper.deleteBatchIds(Convert.toList(ids));
    }

    /**
     * 删除任务日志
     *
     * @param jobId 调度日志ID
     */
    @Override
    public int deleteJobLogById(Long jobId) {
        return jobLogMapper.deleteById(jobId);
    }

    /**
     * 清空任务日志
     */
    @Override
    public void cleanJobLog() {
        jobLogMapper.cleanJobLog();
    }
}
