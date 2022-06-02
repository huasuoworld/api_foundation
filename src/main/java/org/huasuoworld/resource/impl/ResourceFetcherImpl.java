package org.huasuoworld.resource.impl;

import com.sun.tools.javac.util.Pair;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import org.huasuoworld.http.OkHttpClientUtil;
import org.huasuoworld.resource.ResourceFetcher;

/**
 * @author: huacailiang
 * @date: 2022/5/9
 * @description:
 **/
public class ResourceFetcherImpl implements ResourceFetcher {

  @Override
  public Map<String, Object> get(Map<String, Object> parameter) {
    Pair<Boolean, Map<String, Object>> booleanMapPair = OkHttpClientUtil.okHttpClient().httpGet(null);
    if(!booleanMapPair.fst) {
      //TODO log error
      parameter.put("", "");
    }
    parameter.putAll(booleanMapPair.snd);
    return parameter;
  }

  @Override
  public Map<String, Object> post(Map<String, Object> parameter) {
    Pair<Boolean, Map<String, Object>> booleanMapPair = OkHttpClientUtil.okHttpClient().httpPost(null, null);
    if(!booleanMapPair.fst) {
      //TODO log error
    }
    parameter.putAll(booleanMapPair.snd);
    return parameter;
  }

  @Override
  public Map<String, Object> put(Map<String, Object> parameter) {
    return null;
  }

  @Override
  public Pair<Boolean, Object> delete(Map<String, Object> parameter) {
    return null;
  }

  @Override
  public Map<String, Object> update(Map<String, Object> parameter) {
    return null;
  }

  public static void main(String[] args) {
    System.out.println(new BigDecimal(10).compareTo(new BigDecimal(5)));
    System.out.println(new BigDecimal(10).compareTo(new BigDecimal(10)));
    Map<String, Object> payNoMap = new LinkedHashMap<>();
    System.out.println(payNoMap.isEmpty());
  }
}
