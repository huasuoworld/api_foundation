package org.huasuoworld.resource;

import com.sun.tools.javac.util.Pair;
import java.util.Map;
import org.huasuoworld.models.Function;
import org.huasuoworld.models.Resource;

public interface ResourceFetcher {
  Map<String, Object> get(Map<String, Object> parameter, Resource resource);
  Map<String, Object> post(Map<String, Object> parameter, Resource resource);
  Map<String, Object> put(Map<String, Object> parameter, Resource resource);
  Pair<Boolean, Object> delete(Map<String, Object> parameter, Resource resource);
  Map<String, Object> update(Map<String, Object> parameter, Resource resource);
}
