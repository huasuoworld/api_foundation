package org.huasuoworld.validation.impl;

import org.huasuoworld.util.Pair;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ObjectSchema;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.huasuoworld.models.InputParameter;
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
  public Pair<Boolean, Object> headersValid(InputParameter inputParameter) {
    //step1 find file by openapiName
    Optional<OpenAPI> openAPIOpt = Optional.ofNullable(inputParameter.getOpenAPI());
    //step2 validation headers
    return Pair.of(Boolean.TRUE, inputParameter.getHeaders());
  }

  @Override
  public Pair<Boolean, Object> cookiesValid(InputParameter inputParameter) {
    //step1 read cookies from headers
    //step2 find file by openapiName
    Optional<OpenAPI> openAPIOpt = Optional.ofNullable(inputParameter.getOpenAPI());
    //step3 validation cookies

    return Pair.of(Boolean.TRUE, inputParameter.getHeaders());
  }

  @Override
  public Pair<Boolean, Object> payloadValid(InputParameter inputParameter) {
    //step1 find file by openapiName
    Optional<OpenAPI> openAPIOpt = Optional.ofNullable(inputParameter.getOpenAPI());
    if(!openAPIOpt.isPresent()) {
      return Pair.of(Boolean.FALSE, Constant.FAIL);
    }
    OpenAPI openAPI = openAPIOpt.get();
    //step2 validation payload
    ObjectSchema schema = (ObjectSchema) openAPI.getPaths().get(inputParameter.getRequestURI()).getPost().getRequestBody().getContent().get("application/json").getSchema();
    if(!ObjectUtils.isEmpty(inputParameter.getPayload()) && !inputParameter.getPayload().isEmpty()) {
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
      List<String> payloadList = inputParameter.getPayload().keySet().stream().filter(key -> {
        if(ObjectUtils.isEmpty(schema.getProperties().get(key))) {
          return false;
        }
        String type = schema.getProperties().get(key).getType();
        String paramType = inputParameter.getPayload().get(key).getClass().getTypeName().replace("java.lang.", "").toLowerCase();
        return !type.equals(paramType);
      }).collect(Collectors.toList());
      if(!ObjectUtils.isEmpty(payloadList) && payloadList.size() > 0) {
        //TODO return error message
        return Pair.of(Boolean.FALSE, Constant.FAIL);
      }
    }
    return Pair.of(Boolean.TRUE, inputParameter.getPayload());
  }
}
