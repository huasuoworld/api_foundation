package org.huasuoworld.models;

import java.util.Map;

/**
 * @author: huacailiang
 * @date: 2022/6/7
 * @description:
 **/
public class InputParameter {

  private Map<String, Object> headers;
  private Map<String, Object> payload;
  private Map<String, Object> cookies;
  private String requestURI;

  public Map<String, Object> getHeaders() {
    return headers;
  }

  public void setHeaders(Map<String, Object> headers) {
    this.headers = headers;
  }

  public Map<String, Object> getPayload() {
    return payload;
  }

  public void setPayload(Map<String, Object> payload) {
    this.payload = payload;
  }

  public Map<String, Object> getCookies() {
    return cookies;
  }

  public void setCookies(Map<String, Object> cookies) {
    this.cookies = cookies;
  }

  public String getRequestURI() {
    return requestURI;
  }

  public void setRequestURI(String requestURI) {
    this.requestURI = requestURI;
  }
}
