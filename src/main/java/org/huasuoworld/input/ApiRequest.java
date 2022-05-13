package org.huasuoworld.input;

import com.sun.tools.javac.util.Pair;
import java.util.Map;
import org.huasuoworld.task.TaskRunner;
import org.huasuoworld.task.impl.TaskRunnerImpl;
import org.huasuoworld.validation.ParameterValidation;
import org.huasuoworld.validation.impl.ParameterValidationImpl;

/**
 * @author: huacailiang
 * @date: 2022/5/11
 * @description:
 **/
public class ApiRequest extends RequestMessageTransfer {

  public static Map<String, Object> process(Map<String, Object> headers, Map<String, Object> payload, String requestURI) throws Exception {
    //TODO security
    //validation headers
    ParameterValidation instance = ParameterValidationImpl.getInstance();
    Pair<Boolean, Object> headersValidPair = instance.headersValid(headers, requestURI);
    if(!headersValidPair.fst.booleanValue()) {
      throw new IllegalAccessException(headersValidPair.snd.toString());
    }
    //TODO
    Map<String, Object> headersMap = (Map<String, Object>) headersValidPair.snd;
    //validation cookies
    Pair<Boolean, Object> cookiesValidPair = instance.cookiesValid(headers, requestURI);
    if(!cookiesValidPair.fst.booleanValue()) {
      throw new IllegalAccessException(cookiesValidPair.snd.toString());
    }
    //TODO
    Map<String, Object> cookiesMap = (Map<String, Object>) cookiesValidPair.snd;
    //validation payload
    Pair<Boolean, Object> payloadValidPair = instance.payloadValid(payload, requestURI);
    if(!payloadValidPair.fst.booleanValue()) {
      throw new IllegalAccessException(payloadValidPair.snd.toString());
    }
    //TODO
    Map<String, Object> payloadMap = (Map<String, Object>) payloadValidPair.snd;
    TaskRunner taskRunner = TaskRunnerImpl.getInstance();
    //TODO
    return taskRunner.run(headersMap, cookiesMap, payloadMap, null);
  }
}
