package com.lacus.domain.system.logininfo;

import com.lacus.common.core.page.PageDTO;
import com.lacus.domain.common.command.BulkOperationCommand;
import com.lacus.domain.system.logininfo.dto.LoginInfoDTO;
import com.lacus.domain.system.logininfo.query.LoginInfoQuery;
import com.lacus.dao.system.entity.SysLoginInfoEntity;
import com.lacus.service.system.ISysLoginInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginInfoBusiness {

    @Autowired
    private ISysLoginInfoService loginInfoService;

    public PageDTO getLoginInfoList(LoginInfoQuery query) {
        Page<SysLoginInfoEntity> page = loginInfoService.page(query.toPage(), query.toQueryWrapper());
        List<LoginInfoDTO> records = page.getRecords().stream().map(LoginInfoDTO::new).collect(Collectors.toList());
        return new PageDTO(records, page.getTotal());
    }

    public void deleteLoginInfo(BulkOperationCommand<Long> deleteCommand) {
        QueryWrapper<SysLoginInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("info_id", deleteCommand.getIds());
        loginInfoService.remove(queryWrapper);
    }

}
