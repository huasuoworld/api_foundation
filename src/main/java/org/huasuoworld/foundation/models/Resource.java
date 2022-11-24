package org.huasuoworld.foundation.models;

import java.util.Map;

/**
 * @author: huacailiang
 * @date: 2022/6/6
 * @description:
 **/
public class Resource implements java.io.Serializable {

  private Map<String, Object> headers;

  private Map<String, Object> payload;

  private String username;
  private String password;

  public static Resource getResource(Map<String, Object> resourceMap) {
      Resource resource = new Resource();
      if(resourceMap.containsKey(resourceMap.get("Headers"))) {
        resource.setHeaders((Map<String, Object>) resourceMap.get("Headers"));
      }
      resource.payload(resourceMap);
      return  resource;
  }

  public Resource headers(Map<String, Object> headers) {
    this.headers = headers;
    return this;
  }

  public Resource payload(Map<String, Object> payload) {
    this.payload = payload;
    return this;
  }

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

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
