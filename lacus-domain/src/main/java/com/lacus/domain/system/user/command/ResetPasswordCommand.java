package com.lacus.domain.system.user.command;

import lombok.Data;

@Data
public class ResetPasswordCommand {

    private Long userId;
    private String password;

}
