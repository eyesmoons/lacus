package com.lacus.domain.dataServer.adapter;

import com.lacus.domain.dataServer.command.ParseParamCommand;
import com.lacus.domain.dataServer.dto.ParseParamsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class LoadDriverAdapter {

    @Autowired
    private MysqlDriverAdapter mysqlDriverAdapter;


    /**
     * TODO  需要根据驱动类型动态解析脚本
     * @param requestParamCommand
     * @return
     */
    public ParseParamsDTO process(ParseParamCommand requestParamCommand) {
        return mysqlDriverAdapter.parse(requestParamCommand.getApiScript());
    }


}
