package com.lacus.domain.dig;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.core.page.PageDTO;
import com.lacus.dao.dig.entity.StTaskEntity;
import com.lacus.domain.dig.command.AddStJobCommand;
import com.lacus.domain.dig.command.UpdateStJobCommand;
import com.lacus.domain.dig.model.StJobModel;
import com.lacus.domain.dig.model.StJobModelFactory;
import com.lacus.domain.dig.query.StJobQuery;
import com.lacus.service.dig.IStJobService;
import com.lacus.service.dig.IStTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StJobBusiness {

    @Autowired
    private IStTaskService stTaskService;

    @Autowired
    private IStJobService stJobService;

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
        stTaskService.remove(new QueryWrapper<StTaskEntity>().eq("job_id", jobId));
    }

}
