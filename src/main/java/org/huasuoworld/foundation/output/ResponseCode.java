package org.huasuoworld.foundation.output;

public enum ResponseCode {
  SUCCESS("200"),
  FAIL("500")
  ;

  private String code;
  ResponseCode(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
