package com.lacus.domain.dig.dto;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * @author shengyu
 * @date 2026/1/29 18:51
 */
@Data
public class Node {
    private String taskId;
    private JSONObject position;
}
