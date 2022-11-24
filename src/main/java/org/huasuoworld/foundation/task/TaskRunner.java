package org.huasuoworld.foundation.task;

import io.swagger.v3.oas.models.OpenAPI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.huasuoworld.foundation.function.FunctionExecute;
import org.huasuoworld.foundation.input.OpenAPIBuilder;
import org.huasuoworld.foundation.models.InputParameter;
import org.huasuoworld.foundation.resource.ResourceFetcher;
import org.jetbrains.annotations.NotNull;

/**
 * @author: huacailiang
 * @date: 2022/5/9
 * @description:
 **/
public class TaskRunner implements java.util.function.BiFunction<InputParameter, String, Map<String, Object>> {

  @Override
  public Map<String, Object> apply(InputParameter verifiedParameter, String requestURI) {
    Map<String, Object> responseMap = new HashMap<>();
    //step1 find file by taskName
    TaskType taskType = verifiedParameter.getTaskType();
    try {
      Optional<String> taskNameOpt;
      if(TaskType.SECURITY == taskType) {
        taskNameOpt = OpenAPIBuilder.getVariables(Optional.ofNullable(verifiedParameter.getOpenAPI()), taskType.getType());
      } else {
        taskNameOpt = OpenAPIBuilder.getVariables(Optional.ofNullable(verifiedParameter.getOpenAPI()), requestURI);
      }
      if(!taskNameOpt.isPresent()) {
        return responseMap;
      }
      //根据taskName获取 OpenAPI对象
      Optional<OpenAPI> taskOpenAPIOpt = OpenAPIBuilder.getOpenAPIBuilder().openAPI(taskNameOpt.get());
      if(!taskOpenAPIOpt.isPresent()) {
        return responseMap;
      }
      //根据 OpenAPI对象获取资源列表
      List<String> variableResourcesEnums = OpenAPIBuilder.getVariableResourcesEnums(taskOpenAPIOpt.get());
      if(ObjectUtils.isEmpty(variableResourcesEnums) || variableResourcesEnums.isEmpty()) {
        return responseMap;
      }
      //根据资源列表获取资源对应的OpenAPI对象
      List<Optional<OpenAPI>> taskOpts = variableResourcesEnums.stream().map(variable -> {
        Optional<OpenAPI> openAPIOpt = OpenAPIBuilder.getOpenAPIBuilder().openAPI(variable);
        return openAPIOpt;
      }).filter(variable -> variable.isPresent()).collect(Collectors.toList());
      if(ObjectUtils.isEmpty(taskOpts) || taskOpts.isEmpty()) {
        return responseMap;
      }
      //组装需要执行的函数(函数类型与资源类型)
      List<Function<Map<String, Object>, Map<String, Object>>> functions = taskOpts.stream()
          .map(task -> {
            Optional<String> functionNameOpt = OpenAPIBuilder.getVariables(task, "functionName");
            Optional<String> resourceNameOpt = OpenAPIBuilder.getVariables(task, "resourceName");
            if (functionNameOpt.isPresent()) {
              return new FunctionExecute(functionNameOpt.get(), task);
            } else if (resourceNameOpt.isPresent()) {
              Optional<String> firstPath = OpenAPIBuilder.getFirstPath(task);
              return new ResourceFetcher(resourceNameOpt.get(), firstPath.get(), task);
            } else {
              return null;
            }
          }).filter(task -> ObjectUtils.isNotEmpty(task)).collect(Collectors.toList());
      if(ObjectUtils.isEmpty(functions) || functions.isEmpty()) {
        return responseMap;
      }
      //将入参转化为Map
      Map<String, Object> parameter = mergeResponse(verifiedParameter, responseMap);
      //调整执行方式，改为函数curFunction.compose(preFunction)
      Optional<Function<Map<String, Object>, Map<String, Object>>> preFunction = Optional.empty();
      for(Function<Map<String, Object>, Map<String, Object>> curFunction: functions) {
        if(!preFunction.isPresent()) {
          preFunction = Optional.ofNullable(curFunction);
        } else {
          preFunction = Optional.ofNullable(curFunction.compose(preFunction.get()));
        }
      }
      return preFunction.get().apply(parameter);
    } catch (Exception e) {
      e.printStackTrace();
      return responseMap;
    }
  }

  @NotNull
  private Map<String, Object> mergeResponse(InputParameter verifiedParameter,
      Map<String, Object> responseMap) {
    Map<String, Object> parameter = new HashMap<>();
    if(!ObjectUtils.isEmpty(verifiedParameter.getHeaders()) && !verifiedParameter.getHeaders().isEmpty()) {
      parameter.put("Headers", verifiedParameter.getHeaders());
    }
    if(!ObjectUtils.isEmpty(verifiedParameter.getCookies()) && !verifiedParameter.getCookies().isEmpty()) {
      parameter.put("Cookies", verifiedParameter.getCookies());
    }
    parameter.putAll(verifiedParameter.getPayload());
    if(!responseMap.isEmpty()) {
      parameter.putAll(responseMap);
    }
    return parameter;
  }
}
