package com.lacus.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * types for whether to send warning when process ending;
 */
public enum WarningType {
  /**
   * 0 do not send warning;
   * 1 send if process success;
   * 2 send if process failed;
   * 3 send if process ending;
   */
  NONE(0, "none"),
  SUCCESS(1, "success"),
  FAILURE(2, "failure"),
  ALL(3, "all");


  WarningType(int code, String descp){
    this.code = code;
    this.descp = descp;
  }

  @EnumValue
  private final int code;
  private final String descp;

  public int getCode() {
    return code;
  }

  public String getDescp() {
    return descp;
  }
}
