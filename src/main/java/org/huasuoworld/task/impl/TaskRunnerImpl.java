package org.huasuoworld.task.impl;

import io.swagger.v3.oas.models.OpenAPI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.huasuoworld.function.impl.FunctionExecuteImpl;
import org.huasuoworld.input.OpenAPIBuilder;
import org.huasuoworld.models.Function;
import org.huasuoworld.models.InputParameter;
import org.huasuoworld.models.Resource;
import org.huasuoworld.resource.impl.ResourceFetcherImpl;
import org.huasuoworld.task.TaskRunner;
import org.huasuoworld.task.TaskType;
import org.huasuoworld.util.GsonUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author: huacailiang
 * @date: 2022/5/9
 * @description:
 **/
public class TaskRunnerImpl implements TaskRunner {

  public static final String FUNCTION = "function";
  public static final String RESOURCE = "resource";
  private static TaskRunnerImpl instance;

  private TaskRunnerImpl() {
  }

  public static TaskRunnerImpl getInstance() {
    if(ObjectUtils.isEmpty(instance)) {
      synchronized(TaskRunnerImpl.class) {
        if(ObjectUtils.isEmpty(instance)) {
          instance = new TaskRunnerImpl();
        }
      }
    }
    return instance;
  }

  @Override
  public Map<String, Object> run(InputParameter verifiedParameter, TaskType taskType) {
    Map<String, Object> responseMap = new HashMap<>();
    //step1 find file by taskName
    try {
      Optional<String> taskNameOpt;
      if(TaskType.SECURITY == taskType) {
        taskNameOpt = OpenAPIBuilder.getVariables(Optional.ofNullable(verifiedParameter.getOpenAPI()), taskType.getType());
      } else {
        taskNameOpt = OpenAPIBuilder.getVariables(Optional.ofNullable(verifiedParameter.getOpenAPI()), verifiedParameter.getRequestURI());
      }
      if(!taskNameOpt.isPresent()) {
        return responseMap;
      }
      Optional<OpenAPI> taskOpenAPIOpt = OpenAPIBuilder.getOpenAPIBuilder().openAPI(taskNameOpt.get());
      if(!taskOpenAPIOpt.isPresent()) {
        return responseMap;
      }
      List<String> variableResourcesEnums = OpenAPIBuilder.getVariableResourcesEnums(taskOpenAPIOpt.get());
      for(String variable: variableResourcesEnums) {
        System.out.println(GsonUtil.toJson(variable));
        Optional<OpenAPI> openAPIOpt = OpenAPIBuilder.getOpenAPIBuilder().openAPI(variable);
        if(!openAPIOpt.isPresent()) {
          continue;
        }
        Boolean isFunction = OpenAPIBuilder.isFunction(openAPIOpt);
        Map<String, Object> parameter = mergeResponse(verifiedParameter, responseMap);
        if(isFunction) {
          System.out.println("task run function start");
          Function function = new Function();
          function.payload(parameter).openAPI(openAPIOpt.get());
          Optional<String> functionNameOpt = OpenAPIBuilder.getVariables(openAPIOpt, "functionName");
          if(functionNameOpt.isPresent()) {
            function.setName(functionNameOpt.get());
          }
          Map<String, Object> functionApplyMap = FunctionExecuteImpl.getInstance().exec(function);
          if(!ObjectUtils.isEmpty(functionApplyMap) && !functionApplyMap.isEmpty()) {
            responseMap.putAll(functionApplyMap);
          }
          System.out.println("task run function end");
        } else {
          System.out.println("task run resource start");
          //step2 fetch resource
          Resource resource = new Resource();
          resource.payload(parameter).openAPI(openAPIOpt.get());
          Optional<String> resourceNameOpt = OpenAPIBuilder.getVariables(openAPIOpt, "resourceName");
          if(resourceNameOpt.isPresent()) {
            resource.setName(resourceNameOpt.get());
          }
          Optional<String> firstPath = OpenAPIBuilder.getFirstPath(openAPIOpt);
          if(firstPath.isPresent()) {
            resource.setRequestURI(firstPath.get());
          }
          //TODO run resource
          //step3 run resource
          Map<String, Object> httpResponseMap = ResourceFetcherImpl.getInstance().resourceFetch(resource);
          if(!ObjectUtils.isEmpty(httpResponseMap) && !httpResponseMap.isEmpty()) {
            responseMap.putAll(httpResponseMap);
          }
          System.out.println("task run resource end");
        }
      }
      return responseMap;
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
      parameter.putAll(verifiedParameter.getHeaders());
    }
    if(!ObjectUtils.isEmpty(verifiedParameter.getCookies()) && !verifiedParameter.getCookies().isEmpty()) {
      parameter.putAll(verifiedParameter.getCookies());
    }
    parameter.putAll(verifiedParameter.getPayload());
    if(!responseMap.isEmpty()) {
      parameter.putAll(responseMap);
    }
    return parameter;
  }
}
