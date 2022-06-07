package org.huasuoworld.function;

import java.util.Map;
import org.huasuoworld.models.Function;

public interface FunctionExecute {
  void exec(Map<String, Object> parameter, Function function);
}
