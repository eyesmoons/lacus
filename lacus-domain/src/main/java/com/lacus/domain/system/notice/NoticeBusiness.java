package com.lacus.domain.system.notice;

import com.lacus.common.core.page.PageDTO;
import com.lacus.domain.common.command.BulkOperationCommand;
import com.lacus.domain.system.notice.command.NoticeAddCommand;
import com.lacus.domain.system.notice.command.NoticeUpdateCommand;
import com.lacus.domain.system.notice.model.NoticeModel;
import com.lacus.domain.system.notice.model.NoticeModelFactory;
import com.lacus.domain.system.notice.query.NoticeQuery;
import com.lacus.domain.system.notice.dto.NoticeDTO;
import com.lacus.dao.system.entity.SysNoticeEntity;
import com.lacus.service.system.ISysNoticeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoticeBusiness {

    @Autowired
    private ISysNoticeService noticeService;

    public PageDTO getNoticeList(NoticeQuery query) {
        Page<SysNoticeEntity> page = noticeService.getNoticeList(query.toPage(), query.toQueryWrapper());
        List<NoticeDTO> records = page.getRecords().stream().map(NoticeDTO::new).collect(Collectors.toList());
        return new PageDTO(records, page.getTotal());
    }


    public NoticeDTO getNoticeInfo(Long id) {
        SysNoticeEntity byId = noticeService.getById(id);
        return new NoticeDTO(byId);
    }


    public void addNotice(NoticeAddCommand addCommand) {
        NoticeModel noticeModel = NoticeModelFactory.loadFromAddCommand(addCommand, new NoticeModel());

        noticeModel.checkFields();

        noticeModel.insert();
    }


    public void updateNotice(NoticeUpdateCommand updateCommand) {
        NoticeModel noticeModel = NoticeModelFactory.loadFromDb(updateCommand.getNoticeId(), noticeService);
        noticeModel.loadUpdateCommand(updateCommand);

        noticeModel.checkFields();

        noticeModel.updateById();
    }

    public void deleteNotice(BulkOperationCommand<Long> deleteCommand) {
        noticeService.removeBatchByIds(deleteCommand.getIds());
    }




}
