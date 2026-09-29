package com.lacus.domain.oneapi.parse;

import com.google.common.collect.Maps;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.scripting.xmltags.SqlNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MySQLParseProcessor {

    private static final String XML = "<select>%s</select>";

    private List<SqlNode> sqlNodeList;


    private void create(String apiScript) {
        String xmlFormat = String.format(XML, apiScript);
        XPathParser xPathParser = new XPathParser(xmlFormat);
        SQLSourceCreator sqlSourceCreator = new SQLSourceCreator(xPathParser, null);
        sqlSourceCreator.init();
        this.sqlNodeList = sqlSourceCreator.parseSQLNode();
    }


    public Map<String, Set<String>> doParse(String apiScript) {
        Map<String, Set<String>> resultMap = Maps.newHashMap();
        create(apiScript);
        DynamicSQLParser dynamicSQLParser = new DynamicSQLParser();
        if (sqlNodeList != null) {
            Set<String> reqList = dynamicSQLParser.parseReq(sqlNodeList);
            resultMap.put("req", reqList);
        }
        Set<String> returnList = dynamicSQLParser.parseReturn(apiScript);
        resultMap.put("return", returnList);
        return resultMap;
    }

}
