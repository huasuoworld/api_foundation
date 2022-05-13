package org.huasuoworld.input;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: huacailiang
 * @date: 2022/5/11
 * @description:
 **/
public class OpenAPIBuilder {

  private static OpenAPIBuilder instance;

  private OpenAPIBuilder() {

  }
  public static OpenAPIBuilder getInstance() {
    if(ObjectUtils.isEmpty(instance)) {
      synchronized(OpenAPIBuilder.class) {
        if(ObjectUtils.isEmpty(instance)) {
          instance = new OpenAPIBuilder();
        }
      }
    }
    return instance;
  }
  public OpenAPI openAPI(String requestURI) {
    String openapiName = requestURI.substring(requestURI.lastIndexOf("/"));
    String openapiNameEnv = System.getProperty("openapiName");
    String openAPIUrl;
    if(StringUtils.isNotEmpty(openapiNameEnv)) {
      openAPIUrl = openapiNameEnv;
    } else {
      openAPIUrl = "src/main/resources/validation/" + openapiName + ".yaml";
    }
    ParseOptions options = new ParseOptions();
    options.setResolveFully(true);
    options.setResolveCombinators(true);
    OpenAPI openAPI = new OpenAPIV3Parser().readLocation(openAPIUrl, null, options).getOpenAPI();
    return openAPI;
  }
}
