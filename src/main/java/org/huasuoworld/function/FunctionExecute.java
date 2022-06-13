package org.huasuoworld.function;

import java.util.Map;
import org.huasuoworld.models.Function;

public interface FunctionExecute {
  Map<String, Object> exec(Function function);
}
