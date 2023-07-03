package com.lacus.job.constants;


import lombok.Data;

@Data
public class SinkResponse {

    private Integer status;

    private String msg;

    private String content;

    private String label;


    public SinkResponse() {

    }

    public SinkResponse(Integer status, String msg, String content, String label) {
        this.status = status;
        this.msg = msg;
        this.content = content;
        this.label = label;
    }
}
