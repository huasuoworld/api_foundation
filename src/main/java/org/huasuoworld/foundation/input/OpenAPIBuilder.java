package org.huasuoworld.foundation.input;

import org.huasuoworld.foundation.util.Pair;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.ServerVariables;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.huasuoworld.foundation.output.ResponseCode;
import org.huasuoworld.foundation.resource.Operations;
import org.jetbrains.annotations.NotNull;

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

  public static ObjectSchema fetchRequestBodySchema(OpenAPI openAPI, String requestURI) {
    PathItem pathItem = openAPI.getPaths().get(requestURI);
    switch (Operations.operation(pathItem)) {
      case POST: return (ObjectSchema) pathItem.getPost().getRequestBody()
          .getContent().get("application/json").getSchema();
      case PUT: return (ObjectSchema) pathItem.getPut().getRequestBody()
          .getContent().get("application/json").getSchema();
      case DELETE: return (ObjectSchema) pathItem.getDelete().getRequestBody()
          .getContent().get("application/json").getSchema();
      case PATCH: return (ObjectSchema) pathItem.getPatch().getRequestBody()
          .getContent().get("application/json").getSchema();
      default: return (ObjectSchema) pathItem.getGet().getRequestBody()
          .getContent().get("application/json").getSchema();
    }
  }

  public static List<String> fetchRequired(OpenAPI openAPI, String requestURI) {
    PathItem pathItem = openAPI.getPaths().get(requestURI);
    switch (Operations.operation(pathItem)) {
      case POST: return pathItem.getPost().getRequestBody()
          .getContent().get("application/json").getSchema().getRequired();
      case PUT: return pathItem.getPut().getRequestBody()
          .getContent().get("application/json").getSchema().getRequired();
      case DELETE:
        if(ObjectUtils.isEmpty(pathItem.getDelete().getRequestBody())) {
          return fetchRequiredFromParameters(pathItem.getDelete());
        }
        return pathItem.getDelete().getRequestBody()
          .getContent().get("application/json").getSchema().getRequired();
      case PATCH: return pathItem.getPatch().getRequestBody()
          .getContent().get("application/json").getSchema().getRequired();
      default:
        if(ObjectUtils.isEmpty(pathItem.getGet().getRequestBody())) {
          return fetchRequiredFromParameters(pathItem.getGet());
        }
        return pathItem.getGet().getRequestBody()
          .getContent().get("application/json").getSchema().getRequired();
    }
  }

  public static List<Parameter> fetchParameters(OpenAPI openAPI, String requestURI) {
    PathItem pathItem = openAPI.getPaths().get(requestURI);
    switch (Operations.operation(pathItem)) {
      case POST: return fetchParametersFromRequestBody(pathItem.getPost());
      case PUT: return fetchParametersFromRequestBody(pathItem.getPut());
      case DELETE:
        if(ObjectUtils.isEmpty(pathItem.getDelete().getRequestBody())) {
          return filterParameters(pathItem.getDelete());
        }
        return fetchParametersFromRequestBody(pathItem.getDelete());
      case PATCH: return fetchParametersFromRequestBody(pathItem.getPatch());
      default:
        if(ObjectUtils.isEmpty(pathItem.getGet().getRequestBody())) {
          return filterParameters(pathItem.getGet());
        }
        return fetchParametersFromRequestBody(pathItem.getGet());
    }
  }



  public static Map<String, String> fetchNameAndTypeFromParameters(OpenAPI openAPI, String requestURI) {
    List<Parameter> parameters = fetchParameters(openAPI, requestURI);
    return parameters.stream().map(p -> Pair.of(p.getName(), p.getSchema().getType()))
        .collect(Collectors.toMap(p -> p.fst, p -> p.snd));
  }

  public static List<String> fetchNameFromParameters(OpenAPI openAPI, String requestURI) {
    List<Parameter> parameters = fetchParameters(openAPI, requestURI);
    return parameters.stream().map(p -> p.getName()).collect(Collectors.toList());
  }

  /**
   * get parameters
   * @param operation
   * @return
   */
  private static List<String> fetchRequiredFromParameters(Operation operation) {
    List<Parameter> parameters = operation.getParameters();
    if(!ObjectUtils.isEmpty(parameters) && !parameters.isEmpty()) {
      return parameters.stream().filter(p -> "query".equals(p.getIn()) && p.getRequired())
          .map(p -> p.getName()).collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  /**
   * get parameters
   * @param operation
   * @return
   */
  private static List<Parameter> filterParameters(Operation operation) {
    List<Parameter> parameters = operation.getParameters();
    if(!ObjectUtils.isEmpty(parameters) && !parameters.isEmpty()) {
      parameters = parameters.stream().filter(p -> "query".equals(p.getIn())).collect(Collectors.toList());
    }
    return parameters;
  }

  /**
   * get parameters from requestBody
   * @param operation
   * @return
   */
  @NotNull
  private static List<Parameter> fetchParametersFromRequestBody(Operation operation) {
    Map<String, Schema> properties = operation.getRequestBody()
        .getContent().get("application/json").getSchema().getProperties();
    List<Parameter> parameters = properties.keySet().stream().map(key -> {
      Parameter parameter = new Parameter();
      Schema schema = properties.get(key);
      parameter.schema(schema);
      if(StringUtils.isEmpty(schema.getName())) {
        parameter.setName(key);
      } else {
        parameter.setName(schema.getName());
      }
      return parameter;
    }).collect(Collectors.toList());
    return parameters;
  }

  public static Schema fetchResponseBodySchema(OpenAPI openAPI, String requestURI) {
    PathItem pathItem = openAPI.getPaths().get(requestURI);
    switch (Operations.operation(pathItem)) {
      case POST: return pathItem.getPost().getResponses().get(ResponseCode.SUCCESS.getCode())
          .getContent().get("application/json").getSchema();
      case PUT: return pathItem.getPut().getResponses().get(ResponseCode.SUCCESS.getCode())
          .getContent().get("application/json").getSchema();
      case DELETE: return pathItem.getDelete().getResponses().get(ResponseCode.SUCCESS.getCode())
          .getContent().get("application/json").getSchema();
      case PATCH: return pathItem.getPatch().getResponses().get(ResponseCode.SUCCESS.getCode())
          .getContent().get("application/json").getSchema();
      default: return pathItem.getGet().getResponses().get(ResponseCode.SUCCESS.getCode())
          .getContent().get("application/json").getSchema();
    }
  }
}
