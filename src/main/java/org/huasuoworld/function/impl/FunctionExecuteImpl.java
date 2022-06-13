package org.huasuoworld.function.impl;

import io.swagger.v3.oas.models.OpenAPI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.huasuoworld.function.FunctionExecute;
import org.huasuoworld.function.FunctionFactory;
import org.huasuoworld.input.OpenAPIBuilder;
import org.huasuoworld.models.Function;

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
    try {
      OpenAPI openAPI = function.getOpenAPI();
      Optional<String> classPathOpt = OpenAPIBuilder.getVariables(Optional.ofNullable(openAPI), "classPath");
      if(!classPathOpt.isPresent()) {
        return new HashMap<>();
      }
      String classPath = classPathOpt.get();
      FunctionFactory factory = FunctionFactory.getInstance();
      //TODO dynamic package
      Class<?> aClass = Class.forName(classPath);
      java.util.function.Function executeFunction = (java.util.function.Function) aClass.getDeclaredConstructor().newInstance();
      factory.put(function.getName(), executeFunction);
      //TODO run function
      //step3 run function
      Map<String, Object> applyMap = (Map<String, Object>) factory.get(function.getName()).apply(function);
      return applyMap;
    } catch (Exception e) {
      e.printStackTrace();
      return new HashMap<>();
    }
  }
}
