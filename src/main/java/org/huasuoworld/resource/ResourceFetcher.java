package org.huasuoworld.resource;

import com.sun.tools.javac.util.Pair;
import java.util.Map;

public interface ResourceFetcher {
  Map<String, Object> get(Map<String, Object> parameter);
  Map<String, Object> post(Map<String, Object> parameter);
  Map<String, Object> put(Map<String, Object> parameter);
  Pair<Boolean, Object> delete(Map<String, Object> parameter);
  Map<String, Object> update(Map<String, Object> parameter);
}
