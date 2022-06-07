package org.huasuoworld.resource.impl;

import com.sun.tools.javac.util.Pair;
import java.util.Map;
import org.huasuoworld.http.OkHttpClientUtil;
import org.huasuoworld.models.Resource;
import org.huasuoworld.resource.ResourceFetcher;

/**
 * @author: huacailiang
 * @date: 2022/5/9
 * @description:
 **/
public class ResourceFetcherImpl implements ResourceFetcher {

  @Override
  public Map<String, Object> get(Map<String, Object> parameter, Resource resource) {
    Pair<Boolean, Map<String, Object>> booleanMapPair = OkHttpClientUtil.okHttpClient().httpGet(null);
    if(!booleanMapPair.fst) {
      //TODO log error
      parameter.put("", "");
    }
    parameter.putAll(booleanMapPair.snd);
    return parameter;
  }

  @Override
  public Map<String, Object> post(Map<String, Object> parameter, Resource resource) {
    Pair<Boolean, Map<String, Object>> booleanMapPair = OkHttpClientUtil.okHttpClient().httpPost(null, null);
    if(!booleanMapPair.fst) {
      //TODO log error
    }
    parameter.putAll(booleanMapPair.snd);
    return parameter;
  }

  @Override
  public Map<String, Object> put(Map<String, Object> parameter, Resource resource) {
    return null;
  }

  @Override
  public Pair<Boolean, Object> delete(Map<String, Object> parameter, Resource resource) {
    return null;
  }

  @Override
  public Map<String, Object> update(Map<String, Object> parameter, Resource resource) {
    return null;
  }
}
