package com.lacus.domain.oneapi.parse;

import com.lacus.common.exception.CustomException;
import com.lacus.utils.ReflectUtil;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.session.Configuration;

import java.util.List;

public class SQLSourceCreator {

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
        this.configuration = configuration == null ? new Configuration() : configuration;
    }

    public void init() {
        createSqlSource();
    }


    private void createSqlSource() {
        List<XNode> xNodes = xPathParser.evalNodes(EVAL_NODE);
        if (xNodes == null || xNodes.size() != 1) {
            throw new CustomException("动态SQL解析异常");
        }
        LanguageDriver languageDriver = configuration.getLanguageDriver(null);
        XNode node = xNodes.get(0);
        this.sqlSource = languageDriver.createSqlSource(configuration, node, null);
    }

    @SuppressWarnings("unchecked")
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

}
