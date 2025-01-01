package com.lacus.domain.dataServer.parse;

import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.domain.dataServer.parse.util.ReflectUtil;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;


public class SQLSourceCreator {

    protected Logger Logger = LoggerFactory.getLogger(this.getClass());

    protected XPathParser xPathParser;

    protected Configuration configuration;

    private static final String EVAL_NODE = "select";

    private static final String ROOT_SQL_NODE = "rootSqlNode";

    private static final String CONTENTS = "contents";

    protected SqlSource sqlSource;

    private SQLSourceCreator() {

    }


    public SQLSourceCreator(XPathParser xPathParser, Configuration configuration) {
        this.xPathParser = xPathParser;
        if (configuration == null) {
            this.configuration = new Configuration();
        }

    }

    public void init() {
        createSqlSource();
    }


    private void createSqlSource() {
        List<XNode> xNodes = xPathParser.evalNodes(EVAL_NODE);
        if (xNodes == null || xNodes.size() != 1) {
            Logger.info("动态SQL解析异常");
            throw new ApiException(ErrorCode.Business.DYNAMIC_SQL_PARSE_ERROR);
        }
        LanguageDriver languageDriver = configuration.getLanguageDriver(null);
        XNode node = xNodes.get(0);
        this.sqlSource = languageDriver.createSqlSource(configuration, node, null);
    }


    public List<SqlNode> parseSQLNode() {
        if (sqlSource instanceof DynamicSqlSource) {
            DynamicSqlSource dynamicSqlSource = (DynamicSqlSource) sqlSource;
            SqlNode sqlNode = (SqlNode) ReflectUtil.reflectPrivateFiled(ROOT_SQL_NODE, dynamicSqlSource);
            if (sqlNode != null) {
                return (List<SqlNode>) ReflectUtil.reflectPrivateFiled(CONTENTS, sqlNode);
            }
        }
        return null;
    }


    public Set<String> parseDynamicRequestParam(List<SqlNode> sqlNodeList) {
        DynamicSQLParser dynamicSQLParser = new DynamicSQLParser();
        return dynamicSQLParser.parseDynamic(sqlNodeList);
    }

    public String parseStaticReturnParam(List<SqlNode> sqlNodeList) {
        DynamicSQLParser dynamicSQLParser = new DynamicSQLParser();
        return dynamicSQLParser.parseStatic(sqlNodeList);
    }

}
