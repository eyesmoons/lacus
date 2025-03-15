package com.lacus.core.processor.jdbc.fragment;

import com.lacus.core.processor.jdbc.meta.Context;

public abstract  class AbstractFragment {

    public abstract boolean apply(Context context);
}
