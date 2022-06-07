package org.huasuoworld.validation.impl;

import com.sun.tools.javac.util.Pair;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ObjectSchema;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.huasuoworld.input.OpenAPIBuilder;
import org.huasuoworld.input.URLS;
import org.huasuoworld.output.Constant;
import org.huasuoworld.validation.ParameterValidation;

/**
 * @author: huacailiang
 * @date: 2022/5/9
 * @description:
 **/
public class ParameterValidationImpl implements ParameterValidation {

  private static ParameterValidationImpl instance;

  private ParameterValidationImpl() {
  }

  public static ParameterValidationImpl getInstance() {
    if(ObjectUtils.isEmpty(instance)) {
      synchronized(ParameterValidationImpl.class) {
        if(ObjectUtils.isEmpty(instance)) {
          instance = new ParameterValidationImpl();
        }
      }
    }
    return instance;
  }

  @Override
  public Pair<Boolean, Object> headersValid(Map<String, Object> headers, String requestURI) {
    //step1 find file by openapiName
    Optional<OpenAPI> openAPIOpt = OpenAPIBuilder.getOpenAPIBuilder().openAPI(requestURI, URLS.VALIDATION);
    //step2 validation headers
    return Pair.of(Boolean.TRUE, headers);
  }

  @Override
  public Pair<Boolean, Object> cookiesValid(Map<String, Object> headers, String requestURI) {
    //step1 read cookies from headers
    //step2 find file by openapiName
    Optional<OpenAPI> openAPIOpt = OpenAPIBuilder.getOpenAPIBuilder().openAPI(requestURI, URLS.VALIDATION);
    //step3 validation cookies

    return Pair.of(Boolean.TRUE, headers);
  }

  @Override
  public Pair<Boolean, Object> payloadValid(Map<String, Object> payload, String requestURI) {
    //step1 find file by openapiName
    Optional<OpenAPI> openAPIOpt = OpenAPIBuilder.getOpenAPIBuilder().openAPI(requestURI, URLS.VALIDATION);
    if(!openAPIOpt.isPresent()) {
      return Pair.of(Boolean.FALSE, Constant.FAIL);
    }
    OpenAPI openAPI = openAPIOpt.get();
    //step2 validation payload
    ObjectSchema schema = (ObjectSchema) openAPI.getPaths().get(requestURI).getPost().getRequestBody().getContent().get("application/json").getSchema();
    if(!ObjectUtils.isEmpty(payload) && !payload.isEmpty()) {
      //validation required
      if(!ObjectUtils.isEmpty(schema.getRequired())) {
        List<String> requiredList = schema.getRequired().stream().filter(key -> {
          if(ObjectUtils.isEmpty(schema.getProperties().get(key))) {
            return true;
          } else {
            return false;
          }
        }).collect(Collectors.toList());
        if(!ObjectUtils.isEmpty(requiredList) && requiredList.size() > 0) {
          //TODO return error message
          return Pair.of(Boolean.FALSE, Constant.FAIL);
        }
      }
      //validation nullable
      List<String> payloadList = payload.keySet().stream().filter(key -> {
        if(ObjectUtils.isEmpty(schema.getProperties().get(key))) {
          return false;
        }
        String type = schema.getProperties().get(key).getType();
        return !type.equals(payload.get(key).getClass().getTypeName());
      }).collect(Collectors.toList());
      if(!ObjectUtils.isEmpty(payloadList) && payloadList.size() > 0) {
        //TODO return error message
        return Pair.of(Boolean.FALSE, Constant.FAIL);
      }
    }
    return Pair.of(Boolean.TRUE, payload);
  }
}
