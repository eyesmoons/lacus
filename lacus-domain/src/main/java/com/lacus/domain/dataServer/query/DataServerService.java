package com.lacus.domain.dataServer.query;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.dao.dataserver.entity.DataServerEntity;
import com.lacus.dao.dataserver.enums.DriverTypeEnum;
import com.lacus.domain.common.constant.Constants;
import com.lacus.domain.dataServer.adapter.LoadDriverAdapter;
import com.lacus.domain.dataServer.command.AddDataServerCommand;
import com.lacus.domain.dataServer.command.ParseParamCommand;
import com.lacus.domain.dataServer.dto.ParseParamsDTO;
import com.lacus.domain.dataServer.dto.ScriptDTO;
import com.lacus.service.dataserver.IDataServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
@Slf4j
public class DataServerService {

    @Autowired
    private IDataServerService dataServerService;

    @Autowired
    private LoadDriverAdapter loadDriverAdapter;

    /**
     * 校验数据服务接口url是否唯一
     *
     * @param apiUrl
     * @return
     */
    private void checkUrlUnique(String apiUrl) {
        boolean flag = dataServerService.checkUrlUnique(apiUrl);
        if (flag) {
            throw new ApiException(ErrorCode.Business.API_URL_IS_NOT_UNIQUE, apiUrl);
        }
    }

    private Integer checkTimeout(Integer timeout) {
        return timeout == null ? Constants.TIMEOUT_MAX : timeout;
    }

    private Integer checkCurrentLimit(Integer currentLimit) {
        return currentLimit == null ? Constants.DEFAULT_VALUE : currentLimit;
    }

    private Long checkMaxReturnRow(Long maxReturnRows) {
        return maxReturnRows == null ? Constants.DEFAULT_MAX_RETURN_ROW : maxReturnRows;
    }

    private void checkQueryScript(String apiSQL) {
        if (null == apiSQL || "".equals(apiSQL)) {
            throw new ApiException(ErrorCode.Business.API_SQL_SCRIPT_IS_EMPTY);
        }
        String trimSQL = apiSQL.trim();
        if (!(trimSQL.startsWith("SELECT") || trimSQL.startsWith("select"))) {
            throw new ApiException(ErrorCode.Business.API_SQL_SCRIPT_ONLY_SUPPORT_SELECT);
        }
    }


    public void add(AddDataServerCommand command) {
        //check url unique
        String apiUrl = command.getApiUrl();
        checkUrlUnique(apiUrl);

        //check timeout
        Integer timeout = command.getQueryTimeout();
        command.setQueryTimeout(checkTimeout(timeout));
        //check currentLimit
        Integer currentLimit = command.getCurrentLimit();
        command.setCurrentLimit(checkCurrentLimit(currentLimit));

        command.setApiStatus(Constants.DEFAULT_VALUE);
        //check max return rows
        Long maxReturnRows = command.getMaxReturnRows();
        command.setMaxReturnRows(checkMaxReturnRow(maxReturnRows));

        //check API script type by driverType
        String driverType = command.getDriverType();
        ScriptDTO scriptDTO = command.getApiScript();
        if (Objects.equals(driverType, DriverTypeEnum.MYSQL.getDescription()) || Objects.equals(driverType, DriverTypeEnum.DORIS.getDescription())) {
            checkQueryScript(scriptDTO.getApiSQL());
        } else {
            //TODO
        }
        DataServerEntity entity = new DataServerEntity();
        BeanUtils.copyProperties(command, entity);
        entity.setApiScript(JSON.toJSONString(command.getApiScript()));
        dataServerService.add(entity);
    }

    /**
     * 解析脚本
     *
     * @param requestParamCommand
     * @return
     */
    public ParseParamsDTO parseSQLReqParams(ParseParamCommand requestParamCommand) {
        return loadDriverAdapter.process(requestParamCommand);
    }


    public PageDTO pageList(DataServerQuery query) {
        Page page = dataServerService.page(query.toPage(), query.toQueryWrapper());
        return new PageDTO(page);

    }
}
