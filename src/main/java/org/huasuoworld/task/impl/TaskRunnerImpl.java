package org.huasuoworld.task.impl;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
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
  public Map<String, Object> run(Map<String, Object> headers, Map<String, Object> payload,
      Map<String, Object> cookies, String taskName) {
    Map<String, Object> parameter = new HashMap<>();
    parameter.putAll(headers);
    parameter.putAll(payload);
    parameter.putAll(cookies);

    Map<String, Object> responseMap = new HashMap<>();
    //step1 find file by taskName

    //step2 fetch function

    //step3 run function

    //step4 response
    return parameter;
  }
}