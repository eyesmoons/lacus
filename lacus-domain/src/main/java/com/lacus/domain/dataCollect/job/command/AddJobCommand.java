package com.lacus.domain.dataCollect.job.command;

import com.lacus.domain.dataCollect.job.dto.TableMapping;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AddJobCommand {
    @NotBlank(message = "任务名称不能为空")
    private String jobName;

    @NotBlank(message = "任务分组不能为空")
    private String catalogId;

    private String appContainer;

    private String remark;

    @NotNull(message = "master节点内存不能为空")
    private Integer jobManager;

    @NotNull(message = "worker节点内存不能为空")
    private Integer taskManager;

    @NotNull(message = "窗口大小不能为空")
    private Integer windowSize;

    @NotNull(message = "最大数据量不能为空")
    private Integer maxSize;

    @NotNull(message = "最大数据条数不能为空")
    private Integer maxCount;

    @ApiModelProperty("输入源ID")
    private Long sourceDatasourceId;

    @ApiModelProperty("输入源库名")
    private String sourceDbName;

    @ApiModelProperty("输出源ID")
    private Long sinkDatasourceId;

    @ApiModelProperty("输出源库名")
    private String sinkDbName;

    @ApiModelProperty("表映射列表")
    private List<TableMapping> tableMappings;
}
