package com.lacus.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageDataResult extends DataResult {

    private Long total;

    private Integer pageNum;

    private Integer pageSize;

    public <T> PageDataResult(Long total, Integer pageNum, Integer pageSize, List<T> list) {
        super(list);
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
