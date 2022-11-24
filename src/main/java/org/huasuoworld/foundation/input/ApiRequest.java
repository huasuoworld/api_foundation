package org.huasuoworld.foundation.input;

import org.huasuoworld.foundation.function.validation.CookiesValidationFunction;
import org.huasuoworld.foundation.function.validation.HeaderValidationFunction;
import org.huasuoworld.foundation.function.validation.PayloadValidationFunction;
import org.huasuoworld.foundation.models.InputParameter;
import org.huasuoworld.foundation.task.TaskType;
import org.huasuoworld.foundation.task.TaskRunner;
import org.huasuoworld.foundation.util.Pair;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Schema;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @author: huacailiang
 * @date: 2022/5/11
 * @description:
 **/
public class ApiRequest extends RequestMessageTransfer {

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

  /**
   *
   * @param inputParameter 入参
   * @param requestURI 请求路径，对应OpenAPIBuilder.pathMap中一个键值
   * @return
   * @throws Exception
   */
  public Map<String, Object> process(InputParameter inputParameter, String requestURI) throws Exception {
    InputParameter verifiedParameter = new InputParameter();
    Optional<OpenAPI> openAPIOpt = OpenAPIBuilder.pathMap.get(requestURI);
    if(!openAPIOpt.isPresent()) {
      return new HashMap<>();
    }
    inputParameter.setOpenAPI(openAPIOpt.get());
    Map<String, Object> responseMap = securityValid(inputParameter, verifiedParameter, requestURI)
        .cookieValid(inputParameter, verifiedParameter)
        .payloadValid(inputParameter, verifiedParameter, requestURI)
        .runTask(verifiedParameter, requestURI);
    InputParameter finalParameter = new InputParameter();
    finalParameter.setPayload(responseMap);
    finalParameter.setOpenAPI(openAPIOpt.get());
    return responseBuilder(finalParameter, requestURI);
  }

  private ApiRequest securityValid(InputParameter inputParameter, InputParameter verifiedParameter, String requestURI) throws Exception {
    System.out.println("securityValid start");
    //TODO security
    //validation headers
    Pair<Boolean, Object> headersValidPair = new HeaderValidationFunction().apply(inputParameter, requestURI);
    if(!headersValidPair.fst.booleanValue()) {
      throw new IllegalAccessException(headersValidPair.snd.toString());
    }
    //TODO
    Map<String, Object> headersMap = (Map<String, Object>) headersValidPair.snd;
    verifiedParameter.setHeaders(headersMap);
    System.out.println("securityValid finish");
    return this;
  }

  public ApiRequest cookieValid(InputParameter inputParameter, InputParameter verifiedParameter) throws Exception {
    System.out.println("cookieValid start");
    //validation cookies
    Pair<Boolean, Object> cookiesValidPair = new CookiesValidationFunction().apply(inputParameter);
    if(!cookiesValidPair.fst.booleanValue()) {
      throw new IllegalAccessException(cookiesValidPair.snd.toString());
    }
    //TODO
    Map<String, Object> cookiesMap = (Map<String, Object>) cookiesValidPair.snd;
    verifiedParameter.setCookies(cookiesMap);
    System.out.println("cookieValid finish");
    return this;
  }

  private ApiRequest payloadValid(InputParameter inputParameter, InputParameter verifiedParameter, String requestURI) throws Exception {
    System.out.println("payloadValid start");
    //validation payload
    Pair<Boolean, Object> payloadValidPair = new PayloadValidationFunction().apply(inputParameter, requestURI);
    if(!payloadValidPair.fst.booleanValue()) {
      throw new IllegalAccessException(payloadValidPair.snd.toString());
    }
    //TODO
    Map<String, Object> payloadMap = (Map<String, Object>) payloadValidPair.snd;
    verifiedParameter.setPayload(payloadMap);
    verifiedParameter.setOpenAPI(inputParameter.getOpenAPI());
    System.out.println("payloadValid finish");
    return this;
  }

  private Map<String, Object> runTask(InputParameter verifiedParameter, String requestURI) throws Exception {
    System.out.println("runTask start");
    //TODO multi task：parallel task、sequential task， default parallel task
    TaskRunner taskRunner = new TaskRunner();
    verifiedParameter.setTaskType(TaskType.TASKS);
    System.out.println("runTask process");
    return taskRunner.apply(verifiedParameter, requestURI);
  }

  private Map<String, Object> responseBuilder(InputParameter finalParameter, String requestURI) {
    if(ObjectUtils.isEmpty(finalParameter.getOpenAPI())) {
      return finalParameter.getPayload();
    }
    Map<String, Object> responseFilterMap = new HashMap<>();
    Schema schema = OpenAPIBuilder.fetchResponseBodySchema(finalParameter.getOpenAPI(), requestURI);
    if(!ObjectUtils.isEmpty(finalParameter.getPayload()) && !finalParameter.getPayload().isEmpty()) {
      schema.getProperties().keySet().stream().forEach(key -> {
        if(finalParameter.getPayload().containsKey(key)) {
          responseFilterMap.put(key.toString(), finalParameter.getPayload().get(key));
        }
      });
    }
    if(responseFilterMap.isEmpty()) {
      return finalParameter.getPayload();
    }
    return responseFilterMap;
  }
}
