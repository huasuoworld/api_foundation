package org.huasuoworld.resource;

import java.util.Map;

public interface ResourceFetcher {
  void get(Map<String, Object> parameter);
  void post(Map<String, Object> parameter);
  void put(Map<String, Object> parameter);
  void delete(Map<String, Object> parameter);
  void update(Map<String, Object> parameter);
}
