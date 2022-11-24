package org.huasuoworld.foundation.models;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import java.util.Map;

/**
 * @author: huacailiang
 * @date: 2022/6/6
 * @description:
 **/
public class Tasks implements java.io.Serializable {

  private Info info;
  private List<Task> tasks;
  private List<Tag> tags;
  private Map<String, Object> extensions;

  public Info getInfo() {
    return info;
  }

  public void setInfo(Info info) {
    this.info = info;
  }

  public List<Task> getTasks() {
    return tasks;
  }

  public void setTasks(List<Task> tasks) {
    this.tasks = tasks;
  }

  public List<Tag> getTags() {
    return tags;
  }

  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }

  public Map<String, Object> getExtensions() {
    return extensions;
  }

  public void setExtensions(Map<String, Object> extensions) {
    this.extensions = extensions;
  }
}
