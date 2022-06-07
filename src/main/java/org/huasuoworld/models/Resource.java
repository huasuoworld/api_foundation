package org.huasuoworld.models;

import io.swagger.v3.oas.models.OpenAPI;
import java.util.Optional;
import org.huasuoworld.input.OpenAPIBuilder;
import org.huasuoworld.input.URLS;

/**
 * @author: huacailiang
 * @date: 2022/6/6
 * @description:
 **/
public class Resource implements java.io.Serializable {

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Optional<OpenAPI> getOpenapi() {
    Optional<OpenAPI> openAPIOpt = OpenAPIBuilder.getOpenAPIBuilder().openAPI(this.name, URLS.RESOURCE);
    return openAPIOpt;
  }
}
