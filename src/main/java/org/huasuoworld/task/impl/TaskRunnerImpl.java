package org.huasuoworld.task.impl;

import io.swagger.v3.oas.models.OpenAPI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.huasuoworld.input.OpenAPIBuilder;
import org.huasuoworld.input.URLS;
import org.huasuoworld.models.InputParameter;
import org.huasuoworld.task.TaskRunner;

/**
 * @author: huacailiang
 * @date: 2022/5/9
 * @description:
 **/
public class TaskRunnerImpl implements TaskRunner {

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
    Optional<String> taskNameOpt = OpenAPIBuilder.getVariables(Optional.ofNullable(verifiedParameter.getOpenAPI()), "tasks");
    if(!taskNameOpt.isPresent()) {
      return responseMap;
    }
    Optional<OpenAPI> taskOpenAPIOpt = OpenAPIBuilder.getOpenAPIBuilder().openAPI(taskNameOpt.get(), URLS.TASK);
    Optional<String> functionOpt = OpenAPIBuilder.getVariables(taskOpenAPIOpt, "function");
    if(!functionOpt.isPresent()) {
      return responseMap;
    }
    Optional<String> resourceOpt = OpenAPIBuilder.getVariables(taskOpenAPIOpt, "resource");
    if(!resourceOpt.isPresent()) {
      return responseMap;
    }
    Map<String, Object> parameter = new HashMap<>();
    parameter.putAll(verifiedParameter.getHeaders());
    parameter.putAll(verifiedParameter.getPayload());
    parameter.putAll(verifiedParameter.getCookies());

    //step1 find file by taskName

    //step2 fetch function

    //step3 run function

    //step4 response
    return parameter;
  }
}
