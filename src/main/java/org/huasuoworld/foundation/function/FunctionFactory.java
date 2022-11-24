package org.huasuoworld.foundation.function;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @author: huacailiang
 * @date: 2022/6/13
 * @description:
 **/
public class FunctionFactory {

  private static FunctionFactory instance;

  private FunctionFactory() {}

  public static FunctionFactory getInstance() {
    if(ObjectUtils.isEmpty(instance)) {
      synchronized(FunctionFactory.class) {
        if(ObjectUtils.isEmpty(instance)) {
          instance = new FunctionFactory();
        }
      }
    }
    return instance;
  }

  private Map<String, Function> functionMap = new HashMap<>();

  public Map<String, Function> getFunctionMap() {
    return functionMap;
  }

  public void setFunctionMap(Map<String, Function> functionMap) {
    this.functionMap = functionMap;
  }

  public void put(String key, Function function) {
    functionMap.put(key, function);
  }

  public Function get(String key) {
    return functionMap.get(key);
  }
}
