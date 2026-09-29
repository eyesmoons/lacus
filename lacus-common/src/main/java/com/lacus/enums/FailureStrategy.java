package com.lacus.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * failure policy when some task node failed.
 */
public enum FailureStrategy {

  /**
   * 0 ending process when some tasks failed.
   * 1 continue running when some tasks failed.
   **/
  END(0, "end"),
  CONTINUE(1, "continue");

  FailureStrategy(int code, String descp){
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
