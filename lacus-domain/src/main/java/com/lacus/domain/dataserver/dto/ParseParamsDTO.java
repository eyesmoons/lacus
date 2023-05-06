package com.lacus.domain.dataserver.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by:
 *
 * @Author: lit
 * @Date: 2023/05/06/13:26
 * @Description:
 */
@Data
public class ParseParamsDTO {


    @Data
    public static class RequestParams {

        private String columnName;

        private String columnType;

        private boolean required;

        private String columnDesc;

        @ApiModelProperty("参数样例")
        private String requestDemo;

    }


    @Data
    public static class ReturnParams {

        private String columnName;

        private String columnDesc;

        private String columnType;
    }

    @ApiModelProperty("请求参数")
    private List<RequestParams> requestParams;


    @ApiModelProperty("返回参数")
    private List<ReturnParams> returnParams;


    public static ParseParamsDTO.RequestParams buildReqParams() {
        return new RequestParams();
    }


    public static ParseParamsDTO.ReturnParams buildReturnParams() {
        return new ReturnParams();
    }


}
