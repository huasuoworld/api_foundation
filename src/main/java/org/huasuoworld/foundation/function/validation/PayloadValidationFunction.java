package org.huasuoworld.foundation.function.validation;

import org.huasuoworld.foundation.input.OpenAPIBuilder;
import org.huasuoworld.foundation.models.InputParameter;
import org.huasuoworld.foundation.output.Constant;
import org.huasuoworld.foundation.util.GsonUtil;
import org.huasuoworld.foundation.util.Pair;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @author: huacailiang
 * @date: 2022/11/21
 * @description:
 **/
public class PayloadValidationFunction implements java.util.function.BiFunction<InputParameter, String, Pair<Boolean, Object>> {

  @Override
  public Pair<Boolean, Object> apply(InputParameter inputParameter, String requestURI) {
    //step1 find file by openapiName
    Optional<OpenAPI> openAPIOpt = Optional.ofNullable(inputParameter.getOpenAPI());
    if(!openAPIOpt.isPresent()) {
      return Pair.of(Boolean.FALSE, Constant.FAIL);
    }
    OpenAPI openAPI = openAPIOpt.get();
    //step2 validation payload
    List<String> required = OpenAPIBuilder.fetchRequired(openAPI, requestURI);
    if(!ObjectUtils.isEmpty(inputParameter.getPayload()) && !inputParameter.getPayload().isEmpty()) {
      //validation required
      if(!ObjectUtils.isEmpty(required)) {
        List<String> requiredList = required.stream().filter(key -> {
          if(ObjectUtils.isEmpty(inputParameter.getPayload().get(key))) {
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
      Map<String, String> typeMap = OpenAPIBuilder.fetchNameAndTypeFromParameters(openAPI, requestURI);
      List<String> payloadList = inputParameter.getPayload().keySet().stream().filter(key -> {
        if(!typeMap.containsKey(key)) {
          return false;
        }
        String type = typeMap.get(key);
        String paramType = inputParameter.getPayload().get(key).getClass().getTypeName().replace("java.lang.", "").replace("java.util.", "").toLowerCase();
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
