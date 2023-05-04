package com.lacus.domain.system.user.command;

import lombok.Data;

@Data
public class ChangeStatusCommand {

    private Long userId;
    private String status;

}
