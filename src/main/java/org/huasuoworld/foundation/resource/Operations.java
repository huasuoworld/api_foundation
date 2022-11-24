package org.huasuoworld.foundation.resource;

import io.swagger.v3.oas.models.PathItem;
import org.apache.commons.lang3.ObjectUtils;

public enum Operations {
  GET("get"),
  POST("post"),
  PUT("put"),
  DELETE("delete"),
  PATCH("patch")
  ;

  private String name;

  Operations(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static Operations operation(PathItem pathItem) {
    if(!ObjectUtils.isEmpty(pathItem.getGet())) {
      return Operations.GET;
    } else if(!ObjectUtils.isEmpty(pathItem.getPost())) {
      return Operations.POST;
    } else if(!ObjectUtils.isEmpty(pathItem.getPut())) {
      return Operations.PUT;
    } else if(!ObjectUtils.isEmpty(pathItem.getDelete())) {
      return Operations.DELETE;
    } else if(!ObjectUtils.isEmpty(pathItem.getPatch())) {
      return Operations.PATCH;
    } else {
      return Operations.GET;
    }
  }
}
