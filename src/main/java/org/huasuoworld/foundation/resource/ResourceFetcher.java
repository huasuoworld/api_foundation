package org.huasuoworld.foundation.resource;

import org.huasuoworld.foundation.http.OkHttpClientUtil;
import org.huasuoworld.foundation.input.OpenAPIBuilder;
import org.huasuoworld.foundation.models.Resource;
import org.huasuoworld.foundation.util.Pair;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.servers.Server;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @author: huacailiang
 * @date: 2022/5/9
 * @description:
 **/
public class ResourceFetcher implements java.util.function.Function<Map<String, Object>, Map<String, Object>> {

  private String resourceName;
  private String requestURI;
  private Optional<OpenAPI> openAPIOpt;

  public ResourceFetcher(String resourceName, String requestURI, Optional<OpenAPI> openAPIOpt) {
    this.resourceName = resourceName;
    this.requestURI = requestURI;
    this.openAPIOpt = openAPIOpt;
  }
  @Override
  public Map<String, Object> apply(Map<String, Object> resourceMap) {
    Resource resource = Resource.getResource(resourceMap);
    System.out.println("resourceFetch start " + requestURI);
    OpenAPI openAPI = openAPIOpt.get();
    Optional<String> securityOpt = OpenAPIBuilder.getVariables(Optional.ofNullable(openAPI), "security");
    if(securityOpt.isPresent()) {
      String[] security = securityOpt.get().split(",");
      resource.setUsername(security[0]);
      resource.setPassword(security[1]);
    }
    //step1 get url
    Server server = openAPI.getServers().get(0);
    String url = server.getUrl();
    String serverRequestURI = url + requestURI;
    PathItem pathItem = openAPI.getPaths().get(requestURI);
    Operations operation = Operations.operation(pathItem);
    List<String> keys = OpenAPIBuilder.fetchNameFromParameters(openAPI, requestURI);
    resourceMap.putAll(options(resource, operation, keys, serverRequestURI));
    return resourceMap;
  }

  private Map<String, Object> options(Resource resource, Operations operations, List<String> keys, String requestURI) {
    System.out.println("resourceFetch get start");
    //step1 filter parameter map
    Map<String, Object> resourceMap = new HashMap<>();
    Map<String, Object> payloadVerified = new HashMap<>();
    if(!ObjectUtils.isEmpty(keys) && !keys.isEmpty()) {
      keys.stream().forEach(key -> payloadVerified.put(key, resource.getPayload().get(key)));
    }
    //reload payload
    resource.setPayload(payloadVerified);
    Pair<Boolean, Map<String, Object>> booleanMapPair = OkHttpClientUtil.okHttpClient().httpOption(operations, resource, requestURI);
    if(!booleanMapPair.fst) {
      //TODO log error
      resourceMap.put("", "");
    }
    resourceMap.putAll(booleanMapPair.snd);
    System.out.println("resourceFetch get end");
    System.out.println("resourceFetch end");
    return resourceMap;
  }
}
