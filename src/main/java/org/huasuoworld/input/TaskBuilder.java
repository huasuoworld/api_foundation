package org.huasuoworld.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.parser.ObjectMapperFactory;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.huasuoworld.models.Task;
import org.huasuoworld.models.Tasks;
import org.huasuoworld.util.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: huacailiang
 * @date: 2022/6/6
 * @description:
 **/
public class TaskBuilder {

  private static Logger log = LoggerFactory.getLogger(TaskBuilder.class);

  private static ObjectMapper YAML_MAPPER;
  static {
    YAML_MAPPER = ObjectMapperFactory.createYaml();
  }
  private static TaskBuilder taskBuilder;
  private TaskBuilder() {}

  public static TaskBuilder getTaskBuilder() {
    if(ObjectUtils.isEmpty(taskBuilder)) {
      synchronized (TaskBuilder.class) {
        if(ObjectUtils.isEmpty(taskBuilder)) {
          taskBuilder = new TaskBuilder();
        }
      }
    }
    return taskBuilder;
  }

  public Optional<Tasks> fetchTask(String taskName) {
    if(StringUtils.isEmpty(taskName)) {
      return Optional.empty();
    }
    String openapiNameEnv = System.getProperty("taskName");
    String openAPIUrl;
    if(StringUtils.isNotEmpty(openapiNameEnv)) {
      openAPIUrl = openapiNameEnv;
    } else {
      openAPIUrl = "src/main/resources/task/" + taskName + ".yaml";
    }
    try {
      Map<String, Object> yamlMap = YAML_MAPPER.readValue(new File(openAPIUrl), HashMap.class);
      String yamlStr = GsonUtil.toJson(yamlMap);
      log.info(yamlStr);
      Tasks tasks = GsonUtil.parseObject(yamlStr, Tasks.class);
      List<Map<String, Object>> tasksMaps = (List<Map<String, Object>>) yamlMap.get("tasks");
      if(!ObjectUtils.isEmpty(tasksMaps) && !tasksMaps.isEmpty()) {
        List<Task> taskList = tasksMaps.stream()
            .map(tasksMap -> GsonUtil.parseObject(GsonUtil.toJson(tasksMap.get("task")), Task.class))
            .collect(Collectors.toList());
        tasks.setTasks(taskList);
      }
      return Optional.ofNullable(tasks);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      e.printStackTrace();
      return Optional.empty();
    }
  }

  public static void main(String[] args) {
    System.setProperty("taskName", "src/test/resources/task/TestTask.yaml");
    Optional<Tasks> tasksOpt = TaskBuilder.getTaskBuilder().fetchTask("taskName");
    if(tasksOpt.isPresent()) {
      System.out.println(tasksOpt.get().getTasks().get(0).getFunction());
    } else {
      System.out.println("file is not found");
    }
  }
}
