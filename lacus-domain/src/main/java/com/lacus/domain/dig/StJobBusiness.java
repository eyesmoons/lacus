package com.lacus.domain.dig;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.dig.entity.StJobEntity;
import com.lacus.dao.dig.entity.StJobInstanceEntity;
import com.lacus.dao.dig.entity.StTaskEntity;
import com.lacus.dao.dig.entity.StTaskRelationEntity;
import com.lacus.domain.dig.command.AddStJobCommand;
import com.lacus.domain.dig.command.UpdateStJobCommand;
import com.lacus.domain.dig.model.StJobModel;
import com.lacus.domain.dig.model.StJobModelFactory;
import com.lacus.domain.dig.query.StJobQuery;
import com.lacus.service.dig.IStJobInstanceService;
import com.lacus.service.dig.IStJobService;
import com.lacus.service.dig.IStTaskRelationService;
import com.lacus.service.dig.IStTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class StJobBusiness {

    @Autowired
    private IStTaskService stTaskService;

    @Autowired
    private IStJobInstanceService stJobInstanceService;

    @Autowired
    private IStTaskRelationService stTaskRelationService;

    @Autowired
    private IStJobService stJobService;

    @Autowired
    private StJobInstanceBusiness stJobInstanceBusiness;

    @SuppressWarnings("unchecked")
    public PageDTO pageList(StJobQuery query) {
        Page<?> page = stJobService.page(query.toPage(), query.toQueryWrapper());
        return new PageDTO(page.getRecords(), page.getTotal());
    }

    public StJobModel addJob(AddStJobCommand addCommand) {
        StJobModel model = StJobModelFactory.loadFromAddCommand(addCommand, new StJobModel());
        model.checkJobNameUnique(stJobService);
        model.insert();
        return model;
    }

    public void updateJob(UpdateStJobCommand updateCommand) {
        StJobModel model = StJobModelFactory.loadFromUpdateCommand(updateCommand, new StJobModel());
        model.checkJobNameUnique(stJobService);
        model.updateById();
    }

    public void deleteJob(Long jobId) {
        stJobService.removeById(jobId);
        stJobInstanceService.remove(new QueryWrapper<StJobInstanceEntity>().eq("job_id", jobId));
        stTaskService.remove(new QueryWrapper<StTaskEntity>().eq("job_id", jobId));
        stTaskRelationService.remove(new QueryWrapper<StTaskRelationEntity>().eq("job_id", jobId));
    }

    public void publishJob(Long jobId, Integer publicStatus) {
        StJobEntity byId = stJobService.getById(jobId);
        if (ObjectUtils.isEmpty(byId)) {
            throw new CustomException("任务不存在");
        }
        Integer status = byId.getStatus();
        if (Objects.equals(status, publicStatus)) {
            if (Objects.equals(1, publicStatus)) {
                throw new CustomException("任务已发布，无需再次发布");
            } else {
                throw new CustomException("任务已取消发布，无需再次取消");
            }
        }
        byId.setStatus(publicStatus);
        stJobService.updateById(byId);
    }

    public String getJobHocon(Long jobId) {
        StJobEntity job = stJobService.getById(jobId);
        if (ObjectUtils.isEmpty(job)) {
            log.error("Job with ID {} does not exist", jobId);
            throw new CustomException("任务[" + jobId + "]不存在");
        }
        return getJobHocon(job);
    }

    public String getJobHocon(StJobEntity job) {
        log.debug("Building job config for job {}", job.getJobId());
        // 获取task列表
        List<StTaskEntity> taskList = stTaskService.getTaskListByJobId(job.getJobId());
        // 获取task连线列表
        List<StTaskRelationEntity> taskRelationList = stTaskRelationService.getTaskRelationsByJobId(job.getJobId());

        if (taskList.isEmpty()) {
            log.error("No tasks found for job {}", job.getJobId());
            throw new CustomException("任务[" + job.getJobId() + "]没有配置任何任务");
        }
        return stJobInstanceBusiness.buildJobJson(job, taskList, taskRelationList);
    }

    public StJobEntity detail(Long jobId) {
        return stJobService.getById(jobId);
    }
}
