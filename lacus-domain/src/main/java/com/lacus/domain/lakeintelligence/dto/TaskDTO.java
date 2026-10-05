package com.lacus.domain.lakeintelligence.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

@Data
public class TaskDTO {
    @ApiModelProperty("任务 ID")
    private Long taskId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("任务类型: SIMILARITY/CLASSIFICATION")
    private String taskType;

    @ApiModelProperty("任务状态")
    private String status;

    @ApiModelProperty("训练进度")
    private Integer trainingProgress;

    @ApiModelProperty("关联模型 ID")
    private Long modelId;

    @ApiModelProperty("错误信息")
    private String errorMessage;

    @ApiModelProperty("开始时间")
    private Date startedAt;

    @ApiModelProperty("完成时间")
    private Date completedAt;

    @ApiModelProperty("创建时间")
    private Date createTime;
}
