package com.lacus.common.core.dto.oneapi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class StatsTrendDTO {

    @ApiModelProperty("时间桶列表")
    private List<TrendBucketDTO> buckets;

    public StatsTrendDTO() {
    }

    public StatsTrendDTO(List<TrendBucketDTO> buckets) {
        this.buckets = buckets;
    }
}
