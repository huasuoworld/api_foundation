package org.huasuoworld.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.huasuoworld.models.Function;

/**
 * @author: huacailiang
 * @date: 2022/6/17
 * @description:
 **/
public class JwtTokenVerify implements java.util.function.Function<Function, Map<String, Object>> {

  @Override
  public Map<String, Object> apply(Function function) {
    Map<String, Object> payload = function.getPayload();
    Map<String, Object> verifyMap = new HashMap<>();
    Optional<String> tokenOpt = ObjectUtils.isEmpty(payload.get("token")) ? Optional.empty() : Optional.ofNullable(payload.get("token").toString());
    if(!tokenOpt.isPresent()) {
      return payload;
    }
    System.out.println(tokenOpt.get());
    verifyMap.put("AppToken", "test");
    return verifyMap;
  }
}
