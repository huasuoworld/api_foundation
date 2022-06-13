package org.huasuoworld.task.impl;

import io.swagger.v3.oas.models.OpenAPI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.huasuoworld.function.FunctionFactory;
import org.huasuoworld.function.impl.FunctionExecuteImpl;
import org.huasuoworld.input.OpenAPIBuilder;
import org.huasuoworld.input.URLS;
import org.huasuoworld.models.Function;
import org.huasuoworld.models.InputParameter;
import org.huasuoworld.models.Resource;
import org.huasuoworld.resource.impl.ResourceFetcherImpl;
import org.huasuoworld.task.TaskRunner;
import org.huasuoworld.util.GsonUtil;

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
  public Map<String, Object> run(InputParameter verifiedParameter) {
    Map<String, Object> responseMap = new HashMap<>();
    //step1 find file by taskName
    try {
      Optional<String> taskNameOpt = OpenAPIBuilder.getVariables(Optional.ofNullable(verifiedParameter.getOpenAPI()), "tasks");
      if(!taskNameOpt.isPresent()) {
        return responseMap;
      }
      Optional<OpenAPI> taskOpenAPIOpt = OpenAPIBuilder.getOpenAPIBuilder().openAPI(taskNameOpt.get(), URLS.TASK);
      if(taskOpenAPIOpt.isPresent()) {
        List<Map<String, Object>> variableList = OpenAPIBuilder.getVariableList(taskOpenAPIOpt.get());
        for(Map<String, Object> variable: variableList) {
          System.out.println(GsonUtil.toJson(variable));
          if(variable.containsKey(FUNCTION)) {
            System.out.println("task run function start");
            //step2 fetch function
            Optional<String> functionOpt = OpenAPIBuilder.getVariables(taskOpenAPIOpt, FUNCTION);
            //run function or fetch resource
            //run function
            if(functionOpt.isPresent()) {
              //package function
              String functionName = functionOpt.get();
              Optional<OpenAPI> funOpenAPIOpt = OpenAPIBuilder.getOpenAPIBuilder().openAPI(functionName, URLS.FUNCTION);
              if(funOpenAPIOpt.isPresent()) {
                OpenAPI functionOpenAPI = funOpenAPIOpt.get();
                Map<String, Object> parameter = new HashMap<>();
                parameter.putAll(verifiedParameter.getHeaders());
                parameter.putAll(verifiedParameter.getPayload());
                parameter.putAll(verifiedParameter.getCookies());
                if(!responseMap.isEmpty()) {
                  parameter.putAll(responseMap);
                }
                Function function = new Function();
                function.name(functionName).payload(parameter).openAPI(functionOpenAPI);
                Map<String, Object> functionApplyMap = FunctionExecuteImpl.getInstance().exec(function);
                responseMap = functionApplyMap;
              }
            }
            System.out.println("task run function end");
          } else {
            System.out.println("task run resource start");
            //step2 fetch resource
            Optional<String> resourceOpt = OpenAPIBuilder.getVariables(taskOpenAPIOpt, RESOURCE);
            if(resourceOpt.isPresent()) {
              //package resource
              String resourceName = ResourceFetcherImpl.getResourceName(resourceOpt.get());
              Optional<OpenAPI> resOpenAPIOpt = OpenAPIBuilder.getOpenAPIBuilder().openAPI(resourceName, URLS.RESOURCE);
              if(resOpenAPIOpt.isPresent()) {
                Map<String, Object> parameter = new HashMap<>();
                parameter.putAll(verifiedParameter.getHeaders());
                parameter.putAll(verifiedParameter.getPayload());
                parameter.putAll(verifiedParameter.getCookies());
                if(!responseMap.isEmpty()) {
                  parameter.putAll(responseMap);
                }
                Resource resource = new Resource();
                resource.name(resourceName).payload(parameter)
                    .requestURI(ResourceFetcherImpl.getPathName(resourceOpt.get())).openAPI(resOpenAPIOpt.get());
                //TODO run resource
                //step3 run resource
                Map<String, Object> httpResponseMap = ResourceFetcherImpl.getInstance().resourceFetch(resource);
                responseMap = httpResponseMap;
              }
            }
            System.out.println("task run resource end");
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      //step4 response
      return responseMap;
    }
  }
}
