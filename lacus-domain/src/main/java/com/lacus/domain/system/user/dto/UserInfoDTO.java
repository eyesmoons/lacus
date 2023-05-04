package com.lacus.domain.system.user.dto;

import com.lacus.domain.system.role.dto.RoleDTO;
import lombok.Data;

@Data
public class UserInfoDTO {

    private UserDTO user;
    private RoleDTO role;

}
