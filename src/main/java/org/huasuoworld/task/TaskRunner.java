package org.huasuoworld.task;

import java.util.Map;

public interface TaskRunner {
  Map<String, Object> run(Map<String, Object> headers, Map<String, Object> cookies, Map<String, Object> payload, String taskName);
}
