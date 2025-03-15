package com.lacus.core.processor.jdbc.fragment;


import com.lacus.core.processor.jdbc.meta.Context;
import com.lacus.service.vo.RequestParamsVO;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TrimFragment extends AbstractFragment {

    private AbstractFragment contents;
    private String prefix;
    private String suffix;
    private List<String> prefixesToOverride;
    private List<String> suffixesToOverride;

    public TrimFragment(AbstractFragment contents, String prefix, String suffix,
                        String prefixesToOverride, String suffixesToOverride) {
        this(contents, prefix, suffix, prefixesToOverride == null ? null : Arrays.asList(prefixesToOverride
                .split("\\|")), suffixesToOverride == null ? null : Arrays.asList(suffixesToOverride.split("\\|")));
    }

    public TrimFragment(AbstractFragment contents, String prefix, String suffix,
                        List<String> prefixesToOverride, List<String> suffixesToOverride) {
        this.contents = contents;
        this.prefix = prefix;
        this.suffix = suffix;
        this.prefixesToOverride = prefixesToOverride;
        this.suffixesToOverride = suffixesToOverride;
    }

    @Override
    public boolean apply(Context context) {
        FilteredContent fContext = new FilteredContent(context);

        contents.apply(fContext);

        fContext.applyAll();

        return false;
    }

    private class FilteredContent extends Context {
        private Context delegate;

        private boolean trimed = false;

        private StringBuilder sql = new StringBuilder();

        public FilteredContent(Context delegate) {
            super(null, null);
            this.delegate = delegate;
        }

        public void applyAll() {
            if (trimed) {
                return;
            }
            sql = new StringBuilder(sql.toString().trim());
            String upperSql = sql.toString().toUpperCase();
            if (!upperSql.isEmpty()) {
                applyPrefix(upperSql);
                applySuffix(upperSql);
            }
            delegate.appendSql(sql.toString());

        }

        private void applySuffix(String upperSql) {

            if (suffixesToOverride != null) {
                for (String toRemove : suffixesToOverride) {
                    if (upperSql.endsWith(toRemove)) {
                        int start = sql.length() - toRemove.length();
                        int end = sql.length();

                        sql.delete(start, end);

                        break;
                    }
                }
            }

            if (suffix != null) {
                this.appendSql(suffix);
            }

        }

        private void applyPrefix(String upperSql) {
            if (prefixesToOverride != null) {
                for (String toRemove : prefixesToOverride) {
                    if (upperSql.startsWith(toRemove.toUpperCase())) {
                        sql.delete(0, toRemove.length());
                        break;
                    }
                }
            }

            if (prefix != null) {
                sql.insert(0, prefix + " ");
            }

        }

        @Override
        public void bind(String key, Object value) {
            delegate.bind(key, value);
        }

        @Override
        public void appendSql(String sqlFragement) {
            sql.append(sqlFragement).append(" ");
        }

        @Override
        public Map<String, Object> getBinding() {
            return delegate.getBinding();
        }

        @Override
        public List<RequestParamsVO> getParameter() {
            return delegate.getParameter();
        }

        @Override
        public void addParameter(RequestParamsVO param) {
            delegate.addParameter(param);
        }

        @Override
        public String getSql() {
            return delegate.toString();
        }

    }

}
