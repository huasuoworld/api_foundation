package org.huasuoworld.function.impl;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.huasuoworld.function.FunctionExecute;
import org.huasuoworld.function.FunctionFactory;
import org.huasuoworld.input.OpenAPIBuilder;
import org.huasuoworld.models.Function;
import org.huasuoworld.resource.Operations;
import org.huasuoworld.resource.Schemas;

/**
 * @author: huacailiang
 * @date: 2022/5/9
 * @description:
 **/
public class FunctionExecuteImpl implements FunctionExecute {

  private static FunctionExecuteImpl instance;

  private FunctionExecuteImpl() {
  }

  public static FunctionExecuteImpl getInstance() {
    if(ObjectUtils.isEmpty(instance)) {
      synchronized(FunctionExecuteImpl.class) {
        if(ObjectUtils.isEmpty(instance)) {
          instance = new FunctionExecuteImpl();
        }
      }
    }
    return instance;
  }

  @Override
  public Map<String, Object> exec(Function function) {
    Map<String, Object> executeMap = new HashMap<>();
    try {
      if(ObjectUtils.isEmpty(function.getPayload()) || function.getPayload().isEmpty()) {
        return executeMap;
      }
      OpenAPI openAPI = function.getOpenAPI();
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
      if(!factory.getFunctionMap().containsKey(function.getName())) {
        Class<?> aClass = Class.forName(classPath);
        java.util.function.Function executeFunction = (java.util.function.Function) aClass.getDeclaredConstructor().newInstance();
        factory.put(function.getName(), executeFunction);
      }
      if(ObjectUtils.isEmpty(factory.get(function.getName()))) {
        return executeMap;
      }
      //step3 run function
      Map<String, Object> applyMap = (Map<String, Object>) factory.get(function.getName()).apply(function);
      return applyMap;
    } catch (Exception e) {
      e.printStackTrace();
      return executeMap;
    }
  }
}
