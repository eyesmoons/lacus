package com.lacus.domain.dataserver.adapter;

import com.alibaba.druid.DbType;
import com.google.common.collect.Lists;
import com.lacus.domain.dataserver.dto.ParseParamsDTO;
import com.lacus.domain.dataserver.parse.SQLSourceCreator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.util.List;
import java.util.Set;

/**
 * Created by:
 *
 * @Author: lit
 * @Date: 2023/04/28/17:37
 * @Description:
 */
@Component
public class MysqlDriverAdapter extends AbstractDriverAdapter<ParseParamsDTO> {


    private static final String XML = "<select>%s</select>";

    private static final String TEXT = "text";

    private SQLSourceCreator sqlSourceCreator;


    public void create(String apiScript) {
        String apiScriptDecode = new String(Base64Utils.decodeFromString(apiScript));
        String xmlFormat = String.format(XML, apiScriptDecode);
        XPathParser xPathParser = new XPathParser(xmlFormat);
        SQLSourceCreator sqlSourceCreator = new SQLSourceCreator(xPathParser, null);
        sqlSourceCreator.init();
        this.sqlSourceCreator = sqlSourceCreator;
    }


    @Override
    public ParseParamsDTO parse(String apiScript) {
        create(apiScript);
        List<SqlNode> sqlNodeList = sqlSourceCreator.parseSQLNode();
        ParseParamsDTO parseParamsDTO = new ParseParamsDTO();
        //解析动态请求参数
        List<ParseParamsDTO.RequestParams> requestParams = parseReqParams(sqlNodeList);
        parseParamsDTO.setRequestParams(requestParams);
        //解析静态返回参数
        List<ParseParamsDTO.ReturnParams> returnParams = parseReturnParams(sqlNodeList);
        parseParamsDTO.setReturnParams(returnParams);
        return parseParamsDTO;
    }


    public List<ParseParamsDTO.RequestParams> parseReqParams(List<SqlNode> sqlNodeList) {
        Set<String> reqParams = sqlSourceCreator.parseDynamicRequestParam(sqlNodeList);
        List<ParseParamsDTO.RequestParams> requestList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(reqParams)) {
            reqParams.forEach(param -> {
                ParseParamsDTO.RequestParams requestP = ParseParamsDTO.buildReqParams();
                requestP.setColumnName(param);
                requestList.add(requestP);
            });
        }
        return requestList;
    }


    public List<ParseParamsDTO.ReturnParams> parseReturnParams(List<SqlNode> sqlNodeList) {
        String staticSql = sqlSourceCreator.parseStaticReturnParam(sqlNodeList);
        List<ParseParamsDTO.ReturnParams> returnList = Lists.newArrayList();
        if (null != staticSql) {
            Set<String> returnParams = super.staticReturnParam(staticSql, DbType.mysql);
            returnParams.forEach(rp -> {
                ParseParamsDTO.ReturnParams returnP = ParseParamsDTO.buildReturnParams();
                returnP.setColumnName(rp);
                returnList.add(returnP);
            });
        }
        return returnList;
    }


}
