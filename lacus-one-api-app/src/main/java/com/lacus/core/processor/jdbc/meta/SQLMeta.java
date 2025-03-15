package com.lacus.core.processor.jdbc.meta;

import com.lacus.service.vo.RequestParamsVO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SQLMeta {


    private String sql;


    private List<RequestParamsVO> parameter ;


    public SQLMeta(String sql, List<RequestParamsVO> parameter) {
        this.sql = sql;
        this.parameter = parameter;
    }


}
