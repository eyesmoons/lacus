package com.lacus.core.buried;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BuriedModel {

    private String apiName;

    private String apiUrl;

    private List<String> stackLogs;

    private List<Long> delayTime;


}
