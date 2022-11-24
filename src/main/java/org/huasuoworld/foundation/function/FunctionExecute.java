package org.huasuoworld.foundation.function;

import org.huasuoworld.foundation.input.OpenAPIBuilder;
import org.huasuoworld.foundation.models.Resource;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @author: huacailiang
 * @date: 2022/5/9
 * @description:
 **/
public class FunctionExecute implements java.util.function.Function<Map<String, Object>, Map<String, Object>> {

  private String functionName;
  private Optional<OpenAPI> openAPIOpt;
  public FunctionExecute(String functionName, Optional<OpenAPI> openAPIOpt) {
    this.functionName = functionName;
    this.openAPIOpt = openAPIOpt;
  }

  @Override
  public Map<String, Object> apply(Map<String, Object> functionMap) {
    Resource function = Resource.getResource(functionMap);
    Map<String, Object> executeMap = new HashMap<>();
    try {
      if(ObjectUtils.isEmpty(function.getPayload()) || function.getPayload().isEmpty()) {
        return executeMap;
      }
      OpenAPI openAPI = openAPIOpt.get();
      Optional<String> inputOpt = OpenAPIBuilder.getVariables(Optional.ofNullable(openAPI), "input");
      if(!inputOpt.isPresent()) {
        return executeMap;
      }
      Schema schema = openAPI.getComponents().getSchemas().get(inputOpt.get());
      if(!ObjectUtils.isEmpty(schema)) {
        //step1 filter parameter map
        if(!ObjectUtils.isEmpty(schema.getAllOf()) && !schema.getAllOf().isEmpty()) {
          ComposedSchema composedSchema = (ComposedSchema) schema;
          Map<String, Object> properties = composedSchema.getAllOf().get(0).getProperties();
          if(!ObjectUtils.isEmpty(properties) && !properties.isEmpty()) {
            Map<String, Object> payloadVerified = new HashMap<>();
            properties.keySet().stream().forEach(key -> {
              Object object = function.getPayload().get(key);
              if(!ObjectUtils.isEmpty(object)) {
                payloadVerified.put(key, object);
              }
            });
            //reload payload
            function.setPayload(payloadVerified);
          }
        } else {
          Map<String, Object> properties = schema.getProperties();
          if(!ObjectUtils.isEmpty(properties) && !properties.isEmpty()) {
            Map<String, Object> payloadVerified = new HashMap<>();
            properties.keySet().stream().forEach(key -> {
              Object object = function.getPayload().get(key);
              if(!ObjectUtils.isEmpty(object)) {
                payloadVerified.put(key, object);
              }
            });
            //reload payload
            function.setPayload(payloadVerified);
          }
        }
      }
      Optional<String> classPathOpt = OpenAPIBuilder.getVariables(Optional.ofNullable(openAPI), "classPath");
      if(!classPathOpt.isPresent()) {
        return executeMap;
      }
      String classPath = classPathOpt.get();
      FunctionFactory factory = FunctionFactory.getInstance();
      if(!factory.getFunctionMap().containsKey(functionName)) {
        Class<?> aClass = Class.forName(classPath);
        java.util.function.Function executeFunction = (java.util.function.Function) aClass.getDeclaredConstructor().newInstance();
        factory.put(functionName, executeFunction);
      }
      if(ObjectUtils.isEmpty(factory.get(functionName))) {
        return executeMap;
      }
      //step3 run function
      Map<String, Object> applyMap = (Map<String, Object>) factory.get(functionName).apply(function);
      functionMap.putAll(applyMap);
      return functionMap;
    } catch (Exception e) {
      e.printStackTrace();
      return executeMap;
    }
  }
}
