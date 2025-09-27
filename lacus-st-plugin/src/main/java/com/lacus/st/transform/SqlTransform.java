package com.lacus.st.transform;

import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStTransform;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import com.lacus.st.interfaces.StComponentInterface;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * Sql转换组件
 * SQL转换插件，使用SQL来转换给定的输入行
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.TRANSFORM,
        name = "sql_transform",
        displayName = "Sql查询转换",
        description = "使用SQL来转换给定的输入行，支持基本的SQL函数和条件过滤操作",
        version = "2.0.0",
        author = "lacus"
)
@StTag({
        @TagDefinition(name = "SQL配置", displayName = "SQL配置", order = 1, description = "SQL查询和转换相关配置")
})
@AutoService(StComponentInterface.class)
public class SqlTransform extends AbstractStTransform {

    @StField(
            tag = "SQL配置",
            order = 1,
            required = true,
            enName = "plugin_input",
            cnName = "输入表名",
            description = "源表名称，查询SQL表名称必须与此字段匹配",
            placeHolder = "fake",
            formType = StField.FormType.TEXT
    )
    private String pluginInput;

    @StField(
            tag = "SQL配置",
            order = 2,
            
            required = true,
            enName = "plugin_output",
            cnName = "输出表名",
            description = "输出表名称，转换后的数据表名",
            placeHolder = "fake1",
            formType = StField.FormType.TEXT
    )
    private String pluginOutput;

    @StField(
            tag = "SQL配置",
            order = 3,
            
            required = true,
            enName = "query",
            cnName = "查询SQL",
            description = "查询SQL语句，支持基本的函数和条件过滤操作",
            placeHolder = "select id, concat(name, '_') as name, age+1 as age from dual where id>0",
            formType = StField.FormType.TEXT_AREA
    )
    private String query;

    @StField(
            tag = "SQL配置",
            order = 4,
            
            required = false,
            enName = "validate_sql",
            cnName = "验证SQL语法",
            defaultValue = "true",
            description = "是否验证SQL语法的基本有效性",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean validateSql;

    @StField(
            tag = "SQL配置",
            order = 5,
            
            required = false,
            enName = "allow_nested_query",
            cnName = "允许嵌套查询",
            defaultValue = "false",
            description = "是否允许嵌套查询访问复合数据类型（如Row、Map、Array）",
            formType = StField.FormType.CHECKBOX
    )
    private Boolean allowNestedQuery;

    @StField(
            tag = "SQL配置",
            order = 6,
            
            required = false,
            enName = "query_timeout",
            cnName = "查询超时时间(秒)",
            defaultValue = "300",
            description = "SQL查询的超时时间，单位：秒",
            placeHolder = "300",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer queryTimeout;

    // SQL关键字的正则表达式模式
    private static final Pattern SELECT_PATTERN = Pattern.compile("^\\s*select\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern FROM_PATTERN = Pattern.compile("\\s+from\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern DANGEROUS_PATTERNS = Pattern.compile(
            "\\b(delete|update|insert|drop|create|alter|truncate|exec|execute)\\b", 
            Pattern.CASE_INSENSITIVE
    );

    public boolean doCheckConnection() {
        try {
            // 验证必填字段
            if (pluginInput == null || pluginInput.trim().isEmpty()) {
                log.error("Sql转换组件配置错误：plugin_input不能为空");
                return false;
            }

            if (pluginOutput == null || pluginOutput.trim().isEmpty()) {
                log.error("Sql转换组件配置错误：plugin_output不能为空");
                return false;
            }

            if (query == null || query.trim().isEmpty()) {
                log.error("Sql转换组件配置错误：query不能为空");
                return false;
            }

            // 验证SQL语法
            boolean shouldValidate = validateSql != null ? validateSql : true;
            if (shouldValidate) {
                if (!validateSqlSyntax(query.trim())) {
                    return false;
                }
            }

            // 验证超时时间
            int timeout = queryTimeout != null ? queryTimeout : 300;
            if (timeout <= 0) {
                log.error("Sql转换组件配置错误：query_timeout必须大于0");
                return false;
            }

            log.info("Sql转换组件连接检查成功，输入表：{}，输出表：{}", pluginInput, pluginOutput);
            return true;
        } catch (Exception e) {
            log.error("Sql转换组件连接检查失败", e);
            return false;
        }
    }

    /**
     * 验证SQL语法的基本有效性
     */
    private boolean validateSqlSyntax(String sql) {
        try {
            // 检查是否为SELECT语句
            if (!SELECT_PATTERN.matcher(sql).find()) {
                log.error("Sql转换组件配置错误：只支持SELECT查询语句");
                return false;
            }

            // 检查是否包含FROM子句
            if (!FROM_PATTERN.matcher(sql).find()) {
                log.error("Sql转换组件配置错误：SQL语句必须包含FROM子句");
                return false;
            }

            // 检查是否包含危险的SQL操作
            if (DANGEROUS_PATTERNS.matcher(sql).find()) {
                log.error("Sql转换组件配置错误：不允许包含修改数据的SQL操作（DELETE、UPDATE、INSERT等）");
                return false;
            }

            // 验证表名是否匹配plugin_input
            if (!sql.toLowerCase().contains(pluginInput.toLowerCase())) {
                log.warn("Sql转换组件警告：SQL语句中可能没有引用输入表名 '{}'", pluginInput);
            }

            // 检查嵌套查询语法（简单检查）
            boolean allowNested = allowNestedQuery != null ? allowNestedQuery : false;
            if (!allowNested && sql.contains(".") && !sql.matches(".*\\btable_name\\.column_name\\b.*")) {
                // 检查是否有嵌套结构访问语法（如 c_row.c_inner_row.column_b）
                Pattern nestedPattern = Pattern.compile("\\w+\\.\\w+\\.\\w+");
                if (nestedPattern.matcher(sql).find()) {
                    log.error("Sql转换组件配置错误：不允许嵌套查询访问，请设置allow_nested_query=true");
                    return false;
                }
            }

            log.info("SQL语法验证通过");
            return true;
        } catch (Exception e) {
            log.error("SQL语法验证失败", e);
            return false;
        }
    }

    /**
     * 获取格式化的SQL查询语句
     */
    public String getFormattedQuery() {
        if (query == null) {
            return null;
        }
        
        // 基本的SQL格式化：去除多余空白，统一换行
        String formatted = query.trim()
                .replaceAll("\\s+", " ")
                .replaceAll("\\s*,\\s*", ", ")
                .replaceAll("\\s*=\\s*", " = ")
                .replaceAll("\\bselect\\b", "SELECT")
                .replaceAll("\\bfrom\\b", "FROM")
                .replaceAll("\\bwhere\\b", "WHERE")
                .replaceAll("\\border\\s+by\\b", "ORDER BY")
                .replaceAll("\\bgroup\\s+by\\b", "GROUP BY");
        
        return formatted;
    }

    /**
     * 检查SQL是否引用了指定的表名
     */
    public boolean referencesTable(String tableName) {
        if (query == null || tableName == null) {
            return false;
        }
        
        String lowerQuery = query.toLowerCase();
        String lowerTableName = tableName.toLowerCase();
        
        // 简单的表名引用检查
        Pattern tablePattern = Pattern.compile("\\bfrom\\s+" + Pattern.quote(lowerTableName) + "\\b", Pattern.CASE_INSENSITIVE);
        return tablePattern.matcher(lowerQuery).find();
    }

    /**
     * 获取输入表名
     */
    public String getPluginInput() {
        return pluginInput;
    }

    /**
     * 设置输入表名
     */
    public void setPluginInput(String pluginInput) {
        this.pluginInput = pluginInput;
    }

    /**
     * 获取输出表名
     */
    public String getPluginOutput() {
        return pluginOutput;
    }

    /**
     * 设置输出表名
     */
    public void setPluginOutput(String pluginOutput) {
        this.pluginOutput = pluginOutput;
    }

    /**
     * 获取查询SQL
     */
    public String getQuery() {
        return query;
    }

    /**
     * 设置查询SQL
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * 获取是否验证SQL语法
     */
    public Boolean getValidateSql() {
        return validateSql != null ? validateSql : true;
    }

    /**
     * 设置是否验证SQL语法
     */
    public void setValidateSql(Boolean validateSql) {
        this.validateSql = validateSql;
    }

    /**
     * 获取是否允许嵌套查询
     */
    public Boolean getAllowNestedQuery() {
        return allowNestedQuery != null ? allowNestedQuery : false;
    }

    /**
     * 设置是否允许嵌套查询
     */
    public void setAllowNestedQuery(Boolean allowNestedQuery) {
        this.allowNestedQuery = allowNestedQuery;
    }

    /**
     * 获取查询超时时间
     */
    public Integer getQueryTimeout() {
        return queryTimeout != null ? queryTimeout : 300;
    }

    /**
     * 设置查询超时时间
     */
    public void setQueryTimeout(Integer queryTimeout) {
        this.queryTimeout = queryTimeout;
    }
}