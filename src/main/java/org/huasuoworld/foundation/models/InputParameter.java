package org.huasuoworld.foundation.models;

import org.huasuoworld.foundation.task.TaskType;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.Map;

/**
 * @author: huacailiang
 * @date: 2022/6/7
 * @description:
 **/
public class InputParameter {

  private Map<String, Object> headers;
  private Map<String, Object> payload;
  private Map<String, Object> cookies;
  private OpenAPI openAPI;

  private TaskType taskType;

  public Map<String, Object> getHeaders() {
    return headers;
  }

  public void setHeaders(Map<String, Object> headers) {
    this.headers = headers;
  }

  public Map<String, Object> getPayload() {
    return payload;
  }

  public void setPayload(Map<String, Object> payload) {
    this.payload = payload;
  }

  public Map<String, Object> getCookies() {
    return cookies;
  }

  public void setCookies(Map<String, Object> cookies) {
    this.cookies = cookies;
  }

  public OpenAPI getOpenAPI() {
    return openAPI;
  }

  public void setOpenAPI(OpenAPI openAPI) {
    this.openAPI = openAPI;
  }

  public TaskType getTaskType() {
    return taskType;
  }

  public void setTaskType(TaskType taskType) {
    this.taskType = taskType;
  }
}
