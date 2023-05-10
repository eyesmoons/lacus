package com.lacus.domain.datasync.jobCatelog;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.core.page.PageDTO;
import com.lacus.dao.datasync.entity.DataSyncJobCatelogEntity;
import com.lacus.domain.datasync.jobCatelog.command.AddJobCatelogCommand;
import com.lacus.domain.datasync.jobCatelog.command.UpdateJobCatelogCommand;
import com.lacus.domain.datasync.jobCatelog.model.DataSyncJobCatelogModel;
import com.lacus.domain.datasync.jobCatelog.model.DataSyncJobCatelogModelFactory;
import com.lacus.domain.datasync.jobCatelog.query.JobCateLogQuery;
import com.lacus.service.datasync.IDataSyncJobCatelogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobCatelogService {
    @Autowired
    private IDataSyncJobCatelogService dataSyncJobCatelogService;

    public PageDTO pageList(JobCateLogQuery query) {
        Page page = dataSyncJobCatelogService.page(query.toPage(), query.toQueryWrapper());
        return new PageDTO(page.getRecords(), page.getTotal());
    }

    public void addJobCatelog(AddJobCatelogCommand addCommand) {
        DataSyncJobCatelogModel model = DataSyncJobCatelogModelFactory.loadFromAddCommand(addCommand, new DataSyncJobCatelogModel());
        model.insert();
    }

    public void updateJobCatelog(UpdateJobCatelogCommand updateCommand) {
        DataSyncJobCatelogModel model = DataSyncJobCatelogModelFactory.loadFromDb(updateCommand.getCatelogId(), dataSyncJobCatelogService);
        model.loadUpdateCommand(updateCommand);
        model.updateById();
    }

    public void removeJobCatelog(List<Long> catelogIds) {
        dataSyncJobCatelogService.removeBatchByIds(catelogIds);
    }

    public List<DataSyncJobCatelogEntity> list(String catelogName) {
        return dataSyncJobCatelogService.listByName(catelogName);
    }

    public DataSyncJobCatelogEntity detail(Long catelogId) {
        return dataSyncJobCatelogService.getById(catelogId);
    }
}
