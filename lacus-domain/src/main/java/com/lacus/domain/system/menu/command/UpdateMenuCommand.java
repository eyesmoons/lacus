package com.lacus.domain.system.menu.command;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateMenuCommand extends AddMenuCommand {

    @NotNull
    private Long menuId;

}
