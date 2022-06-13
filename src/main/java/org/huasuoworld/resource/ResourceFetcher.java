package org.huasuoworld.resource;

import java.util.Map;
import org.huasuoworld.models.Resource;

public interface ResourceFetcher {
  Map<String, Object> resourceFetch(Resource resource);
}
