package com.lacus.domain.monitor.alert;

public interface AlertCenterConstants {

    String MASKED_VALUE = "******";

    String SYSTEM_OPERATOR = "system";

    interface ChannelTestStatus {
        String UNTESTED = "UNTESTED";
        String SUCCESS = "SUCCESS";
        String FAILED = "FAILED";
    }

    interface RecordStatus {
        String PENDING = "PENDING";
        String SENDING = "SENDING";
        String PARTIAL_SUCCESS = "PARTIAL_SUCCESS";
        String SUCCESS = "SUCCESS";
        String FAILED = "FAILED";
    }

    interface TaskStatus {
        String WAITING = "WAITING";
        String SENDING = "SENDING";
        String SUCCESS = "SUCCESS";
        String FAILED = "FAILED";
        String RETRYING = "RETRYING";
        String CANCELLED = "CANCELLED";
    }

    interface TriggerSource {
        String MANUAL = "MANUAL";
        String API = "API";
        String SYSTEM = "SYSTEM";
    }
}
