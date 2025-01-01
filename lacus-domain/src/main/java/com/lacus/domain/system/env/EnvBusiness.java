package com.lacus.domain.system.env;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.core.page.PageDTO;
import com.lacus.dao.system.entity.SysEnvEntity;
import com.lacus.domain.common.command.BulkOperationCommand;
import com.lacus.domain.system.env.command.EnvAddCommand;
import com.lacus.domain.system.env.command.EnvUpdateCommand;
import com.lacus.domain.system.env.dto.EnvDTO;
import com.lacus.domain.system.env.model.EnvModel;
import com.lacus.domain.system.env.model.EnvModelFactory;
import com.lacus.domain.system.env.query.EnvQuery;
import com.lacus.service.system.ISysEnvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shengyu
 * @date 2024/4/30 17:25
 */
@Service
public class EnvBusiness {

    @Autowired
    private ISysEnvService envService;

    @SuppressWarnings("unchecked")
    public PageDTO getEnvList(EnvQuery query) {
        Page<SysEnvEntity> page = envService.page(query.toPage(), query.toQueryWrapper());
        List<EnvDTO> records = page.getRecords().stream().map(EnvDTO::new).collect(Collectors.toList());
        return new PageDTO(records, page.getTotal());
    }

    public EnvDTO getEnvInfo(Long envId) {
        SysEnvEntity byId = envService.getById(envId);
        return new EnvDTO(byId);
    }

    public void addEnv(EnvAddCommand addCommand) {
        EnvModel model = EnvModelFactory.loadFromAddCommand(addCommand, new EnvModel());
        model.insert();
    }

    public void updateEnv(EnvUpdateCommand updateCommand) {
        EnvModel model = EnvModelFactory.loadFromDb(updateCommand.getEnvId(), envService);
        model.loadUpdateCommand(updateCommand);
        model.updateById();
    }

    public void deleteEnv(BulkOperationCommand<Long> command) {
        envService.removeBatchByIds(command.getIds());
    }
}
