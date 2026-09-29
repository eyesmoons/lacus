package com.lacus.alert.plugin.spi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyResult {

    private boolean success;

    private String requestPayload;

    private String responsePayload;

    private String responseSummary;

    private String errorMessage;

    private long costMs;
}
