package com.lacus.domain.dataserver.query;

import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.dao.dataserver.entity.DataServerEntity;
import com.lacus.domain.dataserver.adapter.LoadDriverAdapter;
import com.lacus.domain.dataserver.command.AddDataServerCommand;
import com.lacus.domain.dataserver.command.ParseParamCommand;
import com.lacus.domain.dataserver.dto.ParseParamsDTO;
import com.lacus.service.dataserver.IDataServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by:
 *
 * @Author: lit
 * @Date: 2023/04/27/10:56
 * @Description:
 */
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
    private boolean checkUrlUnique(String apiUrl) {
        return dataServerService.checkUrlUnique(apiUrl);
    }


    public void add(AddDataServerCommand dataServiceCommand) {
        String apiUrl = dataServiceCommand.getApiUrl();
        boolean flag = checkUrlUnique(apiUrl);
        if (flag) {
            throw new ApiException(ErrorCode.Business.API_URL_IS_NOT_UNIQUE, apiUrl);
        }
        DataServerEntity dataServerEntity = new DataServerEntity();
        BeanUtils.copyProperties(dataServiceCommand, dataServerEntity);
        dataServerService.add(dataServerEntity);
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



}
