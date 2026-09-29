package com.lacus.core.processor;

import com.lacus.common.enums.DatabaseType;
import com.lacus.common.annotation.ProcessorName;
import com.lacus.core.processor.jdbc.meta.SQLMeta;
import com.lacus.core.processor.jdbc.meta.SQLTemplate;
import com.lacus.core.processor.jdbc.template.SQLTemplateEngin;
import org.springframework.stereotype.Component;

import java.util.Map;

@ProcessorName(value = {DatabaseType.Doris, DatabaseType.MySQL})
@Component
public class JdbcProcessor extends AbstractProcessor {

    @Override
    public SQLMeta process(String template, Map<String, String> requestKeys, Map<String, Object> params) {
        SQLTemplateEngin sqlTemplateEngin = SQLTemplateEngin.getInstance();
        SQLTemplate sqlTemplate = sqlTemplateEngin.getSqlTemplate(template);
        return sqlTemplate.process(requestKeys, params);
    }
}
