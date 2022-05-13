package org.huasuoworld.validation;

import com.sun.tools.javac.util.Pair;
import java.util.Map;

public interface ParameterValidation {
  Pair<Boolean, Object> headersValid(Map<String, Object> headers, String requestURI);
  Pair<Boolean, Object> cookiesValid(Map<String, Object> headers, String requestURI);
  Pair<Boolean, Object> payloadValid(Map<String, Object> payload, String requestURI);
}
