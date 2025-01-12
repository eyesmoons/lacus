package com.lacus.service.flink.dto;

import lombok.Data;

import java.util.List;

@Data
public class YarnApplicationListDTO {
  private List<YarnApplicationDTO> yarnApplicationList;
}
