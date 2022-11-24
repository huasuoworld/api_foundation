package org.huasuoworld.foundation.function.validation;

import org.huasuoworld.foundation.models.InputParameter;
import org.huasuoworld.foundation.task.TaskRunner;
import org.huasuoworld.foundation.task.TaskType;
import org.huasuoworld.foundation.util.Pair;
import java.util.Map;

/**
 * @author: huacailiang
 * @date: 2022/11/21
 * @description:
 **/
public class HeaderValidationFunction implements java.util.function.BiFunction<InputParameter, String, Pair<Boolean, Object>> {

  @Override
  public Pair<Boolean, Object> apply(InputParameter inputParameter, String requestURI) {
    //step1 run security task
    TaskRunner taskRunner = new TaskRunner();
    inputParameter.setTaskType(TaskType.SECURITY);
    Map<String, Object> headersValidMap = taskRunner.apply(inputParameter, requestURI);
    //step2 validation headers
    inputParameter.getHeaders().putAll(headersValidMap);
    return Pair.of(Boolean.TRUE, inputParameter.getHeaders());
  }
}
