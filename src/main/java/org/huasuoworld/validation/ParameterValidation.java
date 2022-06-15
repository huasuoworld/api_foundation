package org.huasuoworld.validation;

import org.huasuoworld.util.Pair;
import org.huasuoworld.models.InputParameter;

public interface ParameterValidation {
  Pair<Boolean, Object> headersValid(InputParameter inputParameter);
  Pair<Boolean, Object> cookiesValid(InputParameter inputParameter);
  Pair<Boolean, Object> payloadValid(InputParameter inputParameter);
}
