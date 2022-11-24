package org.huasuoworld.foundation.function.validation;

import org.huasuoworld.foundation.models.InputParameter;
import org.huasuoworld.foundation.util.Pair;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.Optional;

/**
 * @author: huacailiang
 * @date: 2022/11/21
 * @description:
 **/
public class CookiesValidationFunction implements java.util.function.Function<InputParameter, Pair<Boolean, Object>> {

  @Override
  public Pair<Boolean, Object> apply(InputParameter inputParameter) {
    //step1 read cookies from headers
    //step2 find file by openapiName
    Optional<OpenAPI> openAPIOpt = Optional.ofNullable(inputParameter.getOpenAPI());
    //step3 validation cookies

    return Pair.of(Boolean.TRUE, inputParameter.getHeaders());
  }
}
