package org.huasuoworld.task;

import java.util.Map;
import org.huasuoworld.models.InputParameter;

public interface TaskRunner {
  Map<String, Object> run(InputParameter verifiedParameter, TaskType taskType);
}
