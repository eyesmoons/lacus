package com.lacus.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class DataResult implements Serializable {

    List<?> list;

    public <T> DataResult(List<T> list) {
        this.list = list;
    }

    public DataResult() {
    }

}
