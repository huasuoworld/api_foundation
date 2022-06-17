package org.huasuoworld.task;

public enum TaskType {

  TASKS("tasks"),
  SECURITY("security")
  ;
  private String type;
  TaskType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
