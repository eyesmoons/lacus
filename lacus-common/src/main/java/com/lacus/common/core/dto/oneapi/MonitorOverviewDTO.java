package com.lacus.common.core.dto.oneapi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MonitorOverviewDTO {

    @ApiModelProperty("聚合行列表")
    private List<MonitorOverviewItemDTO> rows;

    public MonitorOverviewDTO() {
    }

    public MonitorOverviewDTO(List<MonitorOverviewItemDTO> rows) {
        this.rows = rows;
    }
}
