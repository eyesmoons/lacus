package com.lacus.domain.scheduler.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProcessInstance implements Serializable {
    private static final long serialVersionUID = -7336000042090567998L;

    private String processInstanceState;

    List<ProcessTaskInstance> taskList;
}
