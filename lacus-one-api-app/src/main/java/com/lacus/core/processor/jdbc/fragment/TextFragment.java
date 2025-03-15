package com.lacus.core.processor.jdbc.fragment;


import cn.hutool.core.collection.CollectionUtil;
import com.lacus.common.enums.ColumnTypeEnum;
import com.lacus.core.processor.jdbc.meta.Context;
import com.lacus.core.processor.jdbc.token.GenericTokenParser;

import java.util.Map;
import java.util.Objects;

public class TextFragment extends AbstractFragment {

    private String sql;

    public TextFragment(String sql) {
        this.sql = sql;
    }

    @Override
    public boolean apply(final Context context) {

        GenericTokenParser parser2 = new GenericTokenParser("${", "}",
                content -> {
                    Object value = OgnlCache.getValue(content, context.getBinding());
                    Map<String, String> requestKeys = context.getRequestKeys();
                    if (CollectionUtil.isNotEmpty(requestKeys)) {
                        String type = requestKeys.get(content);
                        ColumnTypeEnum enums = ColumnTypeEnum.match(type);
                        switch (Objects.requireNonNull(enums)) {
                            case C_INT:
                            case C_INT_ARRAY:
                                break;
                            default:
                                if (value == null) {
                                    value = "";
                                } else {
                                    value = "'" + value + "'";
                                }
                                break;
                        }
                    }
                    return value.toString();
                });

        context.appendSql(parser2.parse(sql));
        return true;
    }


}
