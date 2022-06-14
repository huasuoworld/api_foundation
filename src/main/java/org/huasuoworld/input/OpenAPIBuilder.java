package org.huasuoworld.input;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.ServerVariables;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: huacailiang
 * @date: 2022/5/11
 * @description:
 **/
public class OpenAPIBuilder {

  private static OpenAPIBuilder openAPIBuilder;

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
   * @param yamlResourcePath
   * @return
   */
  public Optional<OpenAPI> openAPI(String yamlResourcePath, URLS urls) {
    if(StringUtils.isEmpty(yamlResourcePath)) {
      return Optional.empty();
    }
    Optional<String> fileUrlOpt = URLS.getUrlByFilename(urls, yamlResourcePath);
    if(!fileUrlOpt.isPresent()) {
      return Optional.empty();
    }
    String openAPIUrl = fileUrlOpt.get();
    ParseOptions options = new ParseOptions();
    options.setResolveFully(true);
    options.setResolveCombinators(true);
    OpenAPI openAPI = new OpenAPIV3Parser().readLocation(openAPIUrl, null, options).getOpenAPI();
    return Optional.ofNullable(openAPI);
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
}
