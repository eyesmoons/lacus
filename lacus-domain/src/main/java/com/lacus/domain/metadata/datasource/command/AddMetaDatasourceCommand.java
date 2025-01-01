package com.lacus.domain.metadata.datasource.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AddMetaDatasourceCommand {

    @NotBlank(message = "数据源类型不能为空")
    private String type;

    @NotBlank(message = "输入/输出源类型不能为空")
    private String sourceType;

    @NotBlank(message = "数据源名称不能为空")
    @Size(max = 80, message = "数据源名称长度不能超过80个字符")
    private String datasourceName;

    @Size(max = 500, message = "数据源描述长度不能超过500个字符")
    private String remark;

    @NotBlank(message = "ip或主机名称不能为空")
    private String ip;

    @NotNull(message = "ip或主机名称不能为空")
    private Integer port;

    @NotNull(message = "数据源状态不能为空")
    private Integer status;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    @NotBlank(message = "默认数据库不能为空")
    private String defaultDbName;

    private String connectionParams;
}
