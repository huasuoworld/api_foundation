package org.huasuoworld.foundation.input;

import io.swagger.v3.oas.models.OpenAPI;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
import org.huasuoworld.foundation.security.SignatureProvider;
import org.huasuoworld.foundation.util.GsonUtil;

/**
 * @author: huacailiang
 * @date: 2022/5/30
 * @description:
 **/
public class ParameterBuilder {

  private static ParameterBuilder parameterBuilder;
  private static Map<String, Object> parameterMap;

  /**
   * 防止多线程数据污染，采用多例模式
   */
  static {
    parameterBuilder = new ParameterBuilder();
    parameterMap = new HashMap<>();
  }

  public static ParameterBuilder getParameterBuilder() {
    return parameterBuilder;
  }

  /**
   * 根据openapi 构建请求参数
   * @return
   */
  public ParameterBuilder parameter(Map<String, Object> inputMap, OpenAPI openAPI) {
    if(ObjectUtils.isEmpty(openAPI)) {
      //TODO

    } else {
      //如果openAPI 为空，透传请求参数，否则根据openapi过滤后再请求
      parameterMap.putAll(inputMap);
    }
    return parameterBuilder;
  }

  /**
   * 根据需要生成签名
   * @return
   */
  public ParameterBuilder generateSign(SignatureProvider signatureProvider) {
    if(!ObjectUtils.isEmpty(signatureProvider)) {
      //TODO

    }
    return parameterBuilder;
  }

  /**
   * 转译成请求体
   * @return
   */
  public String toRequestBody() {
    return GsonUtil.toJson(parameterMap);
  }
}
