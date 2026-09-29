package com.lacus.alert.plugin.spi;

import java.util.Collections;
import java.util.List;

public interface AlertNotifier {

    String getTypeCode();

    String getTypeName();

    String getRemark();

    default Integer getSortOrder() {
        return 0;
    }

    default List<AlertConfigField> getConfigSchema() {
        return Collections.emptyList();
    }

    NotifyResult send(NotifyContext context);
}
