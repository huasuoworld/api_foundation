package org.huasuoworld.models;

import io.swagger.v3.oas.models.OpenAPI;
import java.util.Map;

/**
 * @author: huacailiang
 * @date: 2022/6/6
 * @description:
 **/
public class Resource implements java.io.Serializable {

  private String name;

  private String requestURI;

  private Map<String, Object> payload;

  private OpenAPI openAPI;

  public Resource name(String name) {
    this.name = name;
    return this;
  }

  public Resource requestURI(String requestURI) {
    this.requestURI = requestURI;
    return this;
  }

  public Resource payload(Map<String, Object> payload) {
    this.payload = payload;
    return this;
  }

  public Resource openAPI(OpenAPI openAPI) {
    this.openAPI = openAPI;
    return this;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRequestURI() {
    return requestURI;
  }

  public void setRequestURI(String requestURI) {
    this.requestURI = requestURI;
  }

  public Map<String, Object> getPayload() {
    return payload;
  }

  public void setPayload(Map<String, Object> payload) {
    this.payload = payload;
  }

  public OpenAPI getOpenAPI() {
    return openAPI;
  }

  public void setOpenAPI(OpenAPI openAPI) {
    this.openAPI = openAPI;
  }
}
