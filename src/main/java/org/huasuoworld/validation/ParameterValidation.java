package org.huasuoworld.validation;

import java.util.Map;
import org.huasuoworld.models.InputParameter;
import org.huasuoworld.util.Pair;

public interface ParameterValidation {
  Pair<Boolean, Object> headersValid(InputParameter inputParameter);
  Pair<Boolean, Object> cookiesValid(InputParameter inputParameter);
  Pair<Boolean, Object> payloadValid(InputParameter inputParameter);
  Map<String, Object> responseBuilder(InputParameter finalParameter);
}
