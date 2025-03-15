package com.lacus.core.processor.jdbc.fragment;


import cn.hutool.core.collection.CollectionUtil;
import com.lacus.common.enums.ColumnTypeEnum;
import com.lacus.core.processor.jdbc.meta.Context;
import com.lacus.core.processor.jdbc.token.GenericTokenParser;
import com.lacus.core.processor.jdbc.token.TokenHandler;

import java.util.Map;
import java.util.Objects;

public class ForEachFragment extends AbstractFragment {

    public static final String ITEM_PREFIX = "__frch_";

    private ExpressionEvaluator evaluator;
    private String collectionExpression;
    private AbstractFragment contents;
    private String open;
    private String close;
    private String separator;
    private String item;
    private String index;

    private Map<String, String> requestKeys;

    public ForEachFragment(AbstractFragment contents, String collectionExpression,
                           String index, String item, String open, String close,
                           String separator) {

        this.contents = contents;

        this.evaluator = new ExpressionEvaluator();
        this.collectionExpression = collectionExpression;
        this.index = index;
        this.item = item;
        this.open = open;
        this.close = close;
        this.separator = separator;

    }

    @Override
    public boolean apply(Context context) {
        //(字段,字段类型)
        if (CollectionUtil.isEmpty(requestKeys)) {
            this.requestKeys = context.getRequestKeys();
        }
        //(字段，数据)
        Map<String, Object> bindings = context.getBinding();
        final Iterable<?> iterable = evaluator.evaluateIterable(
                collectionExpression, bindings);
        boolean first = true;
        if (iterable != null) {
            applyOpen(context);
            int i = 0;
            for (Object o : iterable) {
                Context oldContext = context;
                if (first) {
                    context = new PrefixedContext(context, "");
                } else {
                    if (separator != null) {
                        context = new PrefixedContext(context, separator);
                    } else {
                        context = new PrefixedContext(context, "");
                    }
                }
                int uniqueNumber = context.getUniqueNumber();
                if (o instanceof Map.Entry) { // Issue #709
                    @SuppressWarnings("unchecked")
                    Map.Entry<Object, Object> mapEntry = (Map.Entry<Object, Object>) o;
                    applyIndex(context, mapEntry.getKey(), uniqueNumber);
                    applyItem(context, mapEntry.getValue(), uniqueNumber);
                } else {
                    applyIndex(context, i, uniqueNumber);
                    applyItem(context, o, uniqueNumber);
                }
                contents.apply(new FilteredContext(context, index, item,
                        uniqueNumber));
                if (first)
                    first = !((PrefixedContext) context).isPrefixApplied();
                context = oldContext;
                i++;
            }
            applyClose(context);
        }

        return true;
    }

    private void applyIndex(Context context, Object o, int i) {
        if (index != null) {
            context.bind(index, o);
            context.bind(itemizeItem(index, i), o);
        }
    }

    private void applyItem(Context context, Object obj, int i) {
        if (item != null) {
            String keyType = requestKeys.get(collectionExpression);
            if (Objects.equals(ColumnTypeEnum.C_STRING_ARRAY.getName(), keyType)) {
                obj = "'" + obj + "'";
            }
            context.bind(item, obj);
            context.bind(itemizeItem(item, i), obj);
        }
    }

    private void applyOpen(Context context) {
        if (open != null) {
            context.appendSql(open);
        }
    }

    private void applyClose(Context context) {
        if (close != null) {
            context.appendSql(close);
        }
    }

    private static String itemizeItem(String item, int i) {
        return new StringBuilder(ITEM_PREFIX).append(item).append("_")
                .append(i).toString();
    }

    private static class FilteredContext extends Context {
        private Context delegate;
        private int index;
        private String itemIndex;
        private String item;

        public FilteredContext(Context delegate, String itemIndex, String item,
                               int i) {
            super(null, null);
            this.delegate = delegate;
            this.index = i;
            this.itemIndex = itemIndex;
            this.item = item;
        }

        @Override
        public Map<String, Object> getBinding() {
            return delegate.getBinding();
        }

        @Override
        public void bind(String name, Object value) {
            delegate.bind(name, value);
        }

        @Override
        public String getSql() {
            return delegate.getSql();
        }

        @Override
        public void appendSql(String sql) {
            GenericTokenParser parser = new GenericTokenParser("#{", "}",
                    new TokenHandler() {
                        @Override
                        public String handleToken(String content) {
                            String newContent = content.replaceFirst("^\\s*"
                                            + item + "(?![^.,:\\s])",
                                    itemizeItem(item, index));
                            if (itemIndex != null && newContent.equals(content)) {
                                newContent = content.replaceFirst("^\\s*"
                                                + itemIndex + "(?![^.,:\\s])",
                                        itemizeItem(itemIndex, index));
                            }
                            return new StringBuilder("#{").append(newContent)
                                    .append("}").toString();
                        }
                    });

            delegate.appendSql(parser.parse(sql));
        }

        @Override
        public int getUniqueNumber() {
            return delegate.getUniqueNumber();
        }

    }

    private class PrefixedContext extends Context {
        private Context delegate;
        private String prefix;
        private boolean prefixApplied;

        public PrefixedContext(Context delegate, String prefix) {
            super(null, null);
            this.delegate = delegate;
            this.prefix = prefix;
            this.prefixApplied = false;
        }

        public boolean isPrefixApplied() {
            return prefixApplied;
        }

        @Override
        public Map<String, Object> getBinding() {
            return delegate.getBinding();
        }

        @Override
        public void bind(String name, Object value) {
            delegate.bind(name, value);
        }

        @Override
        public void appendSql(String sql) {
            if (!prefixApplied && sql != null && sql.trim().length() > 0) {
                delegate.appendSql(prefix);
                prefixApplied = true;
            }
            delegate.appendSql(sql);
        }

        @Override
        public String getSql() {
            return delegate.getSql();
        }

        @Override
        public int getUniqueNumber() {
            return delegate.getUniqueNumber();
        }
    }

}
