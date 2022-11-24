package org.huasuoworld.foundation.function.util;

import org.huasuoworld.foundation.models.Resource;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.apache.commons.lang3.ObjectUtils;
import org.huasuoworld.foundation.util.GsonUtil;

/**
 * @author: huacailiang
 * @date: 2022/6/13
 * @description:
 **/
public class SpringDataJpaPagingFunction implements Function<Resource, Map<String, Object>> {

  @Override
  public Map<String, Object> apply(Resource function) {
    Map<String, Object> payload = function.getPayload();
    if(ObjectUtils.isEmpty(payload) || payload.isEmpty()) {
      return payload;
    }
    Optional<Object> embeddedOpt = Optional.ofNullable(payload.get("_embedded"));
    if(!embeddedOpt.isPresent()) {
      return payload;
    }
    payload.remove("_embedded");
    if(payload.containsKey("_links")) {
      payload.remove("_links");
    }
    Map<String, Object> embeddedMap = GsonUtil.parseMaps(GsonUtil.toJson(embeddedOpt.get()));
    return embeddedMap;
  }
}
