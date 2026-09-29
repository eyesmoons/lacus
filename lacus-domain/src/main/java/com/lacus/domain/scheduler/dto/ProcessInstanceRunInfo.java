package com.lacus.domain.scheduler.dto;

import com.lacus.enums.Flag;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ProcessInstanceRunInfo implements Serializable {
    private static final long serialVersionUID = -2189434639285092053L;

    private Integer id;

    private Integer processDefinitionId;

    private String state;

    private Flag recovery;

    private Date startTime;

    private Date endTime;

    private int runTimes;

    private String name;

    private String host;

    private Date scheduleTime;

    private Integer executorId;

    private String executorName;

    private String duration;
}
