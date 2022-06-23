package org.huasuoworld.input;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.huasuoworld.models.InputParameter;
import org.huasuoworld.task.TaskRunner;
import org.huasuoworld.task.TaskType;
import org.huasuoworld.task.impl.TaskRunnerImpl;
import org.huasuoworld.util.Pair;
import org.huasuoworld.validation.ParameterValidation;
import org.huasuoworld.validation.impl.ParameterValidationImpl;

/**
 * @author: huacailiang
 * @date: 2022/5/11
 * @description:
 **/
public class ApiRequest extends RequestMessageTransfer {

  private InputParameter verifiedParameter = new InputParameter();
  private InputParameter inputParameter = new InputParameter();

  private ApiRequest() {}

  public ApiRequest(List<String> validationPaths) {
    if(!ObjectUtils.isEmpty(validationPaths) && !validationPaths.isEmpty()) {
      validationPaths.stream().forEach(openAPIUrl -> {
        System.out.println("init .... " + openAPIUrl);
        Optional<OpenAPI> validationOpenAPIOpt = OpenAPIBuilder.getOpenAPIBuilder().openAPI(openAPIUrl);
        if(validationOpenAPIOpt.isPresent()) {
          OpenAPI validationOpenAPI = validationOpenAPIOpt.get();
          Paths paths = validationOpenAPI.getPaths();
          if(!ObjectUtils.isEmpty(paths)) {
            paths.keySet().stream().forEach(path -> OpenAPIBuilder.pathMap.put(path, validationOpenAPIOpt));
          }
        }
      });
    }
  }

  public ApiRequest headers(Map<String, Object> headers) {
    getInputParameter().setHeaders(headers);
    return this;
  }

  public ApiRequest payload(Map<String, Object> payload) {
    getInputParameter().setPayload(payload);
    return this;
  }

  public ApiRequest requestURI(String requestURI) {
    getInputParameter().setRequestURI(requestURI);
    return this;
  }

  public Map<String, Object> process() throws Exception {
    ParameterValidation instance = ParameterValidationImpl.getInstance();
    String requestURI = getInputParameter().getRequestURI();
    Optional<OpenAPI> openAPIOpt = OpenAPIBuilder.pathMap.get(requestURI);
    if(!openAPIOpt.isPresent()) {
      return new HashMap<>();
    }
    getInputParameter().setOpenAPI(openAPIOpt.get());
    return securityValid(instance, getInputParameter()).cookieValid(instance, getInputParameter()).payloadValid(instance, getInputParameter()).runTask(getVerifiedParameter());
  }

  private ApiRequest securityValid(ParameterValidation instance, InputParameter inputParameter) throws Exception {
    System.out.println("securityValid start");
    //TODO security
    //validation headers
    Pair<Boolean, Object> headersValidPair = instance.headersValid(inputParameter);
    if(!headersValidPair.fst.booleanValue()) {
      throw new IllegalAccessException(headersValidPair.snd.toString());
    }
    //TODO
    Map<String, Object> headersMap = (Map<String, Object>) headersValidPair.snd;
    getVerifiedParameter().setHeaders(headersMap);
    System.out.println("securityValid finish");
    return this;
  }

  public ApiRequest cookieValid(ParameterValidation instance, InputParameter inputParameter) throws Exception {
    System.out.println("cookieValid start");
    //validation cookies
    Pair<Boolean, Object> cookiesValidPair = instance.cookiesValid(inputParameter);
    if(!cookiesValidPair.fst.booleanValue()) {
      throw new IllegalAccessException(cookiesValidPair.snd.toString());
    }
    //TODO
    Map<String, Object> cookiesMap = (Map<String, Object>) cookiesValidPair.snd;
    getVerifiedParameter().setCookies(cookiesMap);
    System.out.println("cookieValid finish");
    return this;
  }

  private ApiRequest payloadValid(ParameterValidation instance, InputParameter inputParameter) throws Exception {
    System.out.println("payloadValid start");
    //validation payload
    Pair<Boolean, Object> payloadValidPair = instance.payloadValid(inputParameter);
    if(!payloadValidPair.fst.booleanValue()) {
      throw new IllegalAccessException(payloadValidPair.snd.toString());
    }
    //TODO
    Map<String, Object> payloadMap = (Map<String, Object>) payloadValidPair.snd;
    getVerifiedParameter().setPayload(payloadMap);
    getVerifiedParameter().setRequestURI(inputParameter.getRequestURI());
    getVerifiedParameter().setOpenAPI(inputParameter.getOpenAPI());
    System.out.println("payloadValid finish");
    return this;
  }

  private Map<String, Object> runTask(InputParameter verifiedParameter) throws Exception {
    System.out.println("runTask start");
    //TODO multi task：parallel task、sequential task， default parallel task
    TaskRunner taskRunner = TaskRunnerImpl.getInstance();
    System.out.println("runTask process");
    return taskRunner.run(verifiedParameter, TaskType.TASKS);
  }

  public InputParameter getVerifiedParameter() {
    return verifiedParameter;
  }

  public InputParameter getInputParameter() {
    return inputParameter;
  }
}
