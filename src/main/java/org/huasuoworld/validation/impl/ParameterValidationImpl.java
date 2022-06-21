package org.huasuoworld.validation.impl;

import io.swagger.v3.oas.models.PathItem;
import java.util.Map;
import org.huasuoworld.input.OpenAPIBuilder;
import org.huasuoworld.resource.Operations;
import org.huasuoworld.task.TaskRunner;
import org.huasuoworld.task.TaskType;
import org.huasuoworld.task.impl.TaskRunnerImpl;
import org.huasuoworld.util.GsonUtil;
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
    //step1 run security task
    TaskRunner taskRunner = TaskRunnerImpl.getInstance();
    Map<String, Object> headersValidMap = taskRunner.run(inputParameter, TaskType.SECURITY);
    //step2 validation headers
    inputParameter.getHeaders().putAll(headersValidMap);
    return Pair.of(Boolean.TRUE, headersValidMap);
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
    ObjectSchema schema = OpenAPIBuilder.fetchSchema(openAPI, inputParameter.getRequestURI());
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
          return Pair.of(Boolean.FALSE, "parameter required validation fail, " + GsonUtil.toJson(requiredList) + " not be empty");
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
        return Pair.of(Boolean.FALSE, "parameter type validation fail, " + GsonUtil.toJson(payloadList) + " type not equal");
      }
    }
    return Pair.of(Boolean.TRUE, inputParameter.getPayload());
  }
}
