package com.lacus.domain.rtc.jobCatalog;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.core.page.PageDTO;
import com.lacus.dao.rtc.entity.DataSyncJobCatalogEntity;
import com.lacus.domain.rtc.jobCatalog.command.AddJobCatalogCommand;
import com.lacus.domain.rtc.jobCatalog.command.UpdateJobCatalogCommand;
import com.lacus.domain.rtc.jobCatalog.model.DataSyncJobCatalogModel;
import com.lacus.domain.rtc.jobCatalog.model.DataSyncJobCatalogModelFactory;
import com.lacus.domain.rtc.jobCatalog.query.JobCatalogQuery;
import com.lacus.service.rtc.IDataSyncJobCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobCatalogBusiness {
    @Autowired
    private IDataSyncJobCatalogService dataSyncJobCatalogService;

    public PageDTO pageList(JobCatalogQuery query) {
        Page page = dataSyncJobCatalogService.page(query.toPage(), query.toQueryWrapper());
        return new PageDTO(page.getRecords(), page.getTotal());
    }

    public void addJobCatalog(AddJobCatalogCommand addCommand) {
        DataSyncJobCatalogModel model = DataSyncJobCatalogModelFactory.loadFromAddCommand(addCommand, new DataSyncJobCatalogModel());
        model.insert();
    }

    public void updateJobCatalog(UpdateJobCatalogCommand updateCommand) {
        DataSyncJobCatalogModel model = DataSyncJobCatalogModelFactory.loadFromDb(updateCommand.getCatalogId(), dataSyncJobCatalogService);
        model.loadUpdateCommand(updateCommand);
        model.updateById();
    }

    public void removeJobCatalog(List<String> catalogIds) {
        dataSyncJobCatalogService.removeBatchByIds(catalogIds);
    }

    public List<DataSyncJobCatalogEntity> list(String catalogName) {
        return dataSyncJobCatalogService.listByName(catalogName);
    }

    public DataSyncJobCatalogEntity detail(String catalogId) {
        return dataSyncJobCatalogService.getById(catalogId);
    }
}
