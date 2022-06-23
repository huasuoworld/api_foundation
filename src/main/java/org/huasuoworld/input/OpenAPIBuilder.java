package org.huasuoworld.input;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.servers.ServerVariables;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.huasuoworld.resource.Operations;

/**
 * @author: huacailiang
 * @date: 2022/5/11
 * @description:
 **/
public class OpenAPIBuilder {

  private static OpenAPIBuilder openAPIBuilder;
  public static Map<String, Optional<OpenAPI>> pathMap = new HashMap<>();

  private OpenAPIBuilder() {

  }
  public static OpenAPIBuilder getOpenAPIBuilder() {
    if(ObjectUtils.isEmpty(openAPIBuilder)) {
      synchronized(OpenAPIBuilder.class) {
        if(ObjectUtils.isEmpty(openAPIBuilder)) {
          openAPIBuilder = new OpenAPIBuilder();
        }
      }
    }
    return openAPIBuilder;
  }

  /**
   * 根据requestURI请求路径获取validation目录下面yaml文件
   * @param openAPIUrl
   * @return
   */
  public Optional<OpenAPI> openAPI(String openAPIUrl) {
    if(StringUtils.isEmpty(openAPIUrl)) {
      return Optional.empty();
    }
    ParseOptions options = new ParseOptions();
    options.setResolveFully(true);
    options.setResolveCombinators(true);
    OpenAPI openAPI = new OpenAPIV3Parser().readLocation(openAPIUrl, null, options).getOpenAPI();
    return Optional.ofNullable(openAPI);
  }

  public static Boolean isFunction(Optional<OpenAPI> openAPIOpt) {
    if(!openAPIOpt.isPresent()) {
      return Boolean.FALSE;
    }
    OpenAPI openAPI = openAPIOpt.get();
    String version = openAPI.getInfo().getVersion();
    if("function".equals(version)) {
      return Boolean.TRUE;
    } else if("resource".equals(version)) {
      return Boolean.FALSE;
    } else {
      return Boolean.TRUE;
    }
  }

  public static Optional<String> getFirstPath(Optional<OpenAPI> openAPIOpt) {
    if(!openAPIOpt.isPresent()) {
      return Optional.empty();
    }
    Optional<String> first = openAPIOpt.get().getPaths().keySet().stream().findFirst();
    return first;
  }

  public static Optional<String> getVariables(Optional<OpenAPI> openAPIOpt, String extensionName) {
    if(!openAPIOpt.isPresent()) {
      return Optional.empty();
    }
    OpenAPI openAPI = openAPIOpt.get();
    Object extension = getVariables(openAPI).get(extensionName);
    return ObjectUtils.isEmpty(extension) ? Optional.empty() : Optional.ofNullable(extension.toString());
  }

  public static Map<String, Object> getVariables(OpenAPI openAPI) {
    ServerVariables variables = openAPI.getServers().get(0).getVariables();
    Map<String, Object> variablesMap = new HashMap<>();
    if(!ObjectUtils.isEmpty(variables)) {
      variables.keySet().stream().forEach(key -> {
        variablesMap.put(key, variables.get(key).getDefault());
      });
    }
    return variablesMap;
  }

  public static List<Map<String, Object>> getVariableList(OpenAPI openAPI) {
    List<Map<String, Object>> variablesList = new LinkedList<>();
    ServerVariables variables = openAPI.getServers().get(0).getVariables();
    if(!ObjectUtils.isEmpty(variables)) {
      variables.keySet().stream().forEach(key -> {
        Map<String, Object> serverMap = new HashMap<>();
        serverMap.put(key, variables.get(key).getDefault());
        variablesList.add(serverMap);
      });
    }
    return variablesList;
  }

  public static List<String> getVariableResourcesEnums(OpenAPI openAPI) {
    String resources = "resources";
    List<String> variableList = new ArrayList<>();
    ServerVariables variables = openAPI.getServers().get(0).getVariables();
    if(!ObjectUtils.isEmpty(variables)) {
      List<List<String>> resourcesKeys = variables.keySet().stream()
          .filter(variableKey -> variableKey.equals(resources))
          .map(variableKey -> variables.get(variableKey).getEnum()).collect(Collectors.toList());
      if(!ObjectUtils.isEmpty(resourcesKeys)) {
        Optional<List<String>> resourcesKey = resourcesKeys.stream().findFirst();
        if(resourcesKey.isPresent()) {
          return resourcesKey.get();
        }
      }
    }
    return variableList;
  }

  public static Boolean isFunction(String resourceName) {
    if(StringUtils.isEmpty(resourceName)) {
      return Boolean.TRUE;
    } else if(resourceName.indexOf("/") > 0) {
      return Boolean.FALSE;
    } else {
      return Boolean.TRUE;
    }
  }

  public static ObjectSchema fetchSchema(OpenAPI openAPI, String requestURI) {
    PathItem pathItem = openAPI.getPaths().get(requestURI);
    switch (Operations.operation(pathItem)) {
      case POST: return (ObjectSchema) pathItem.getPost().getRequestBody().getContent().get("application/json").getSchema();
      default: return (ObjectSchema) pathItem.getGet().getRequestBody().getContent().get("application/json").getSchema();
    }
  }
}
