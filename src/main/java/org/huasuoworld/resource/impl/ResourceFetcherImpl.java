package org.huasuoworld.resource.impl;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.huasuoworld.http.OkHttpClientUtil;
import org.huasuoworld.input.OpenAPIBuilder;
import org.huasuoworld.models.Resource;
import org.huasuoworld.resource.Operations;
import org.huasuoworld.resource.ResourceFetcher;
import org.huasuoworld.resource.Schemas;
import org.huasuoworld.util.Pair;

/**
 * @author: huacailiang
 * @date: 2022/5/9
 * @description:
 **/
public class ResourceFetcherImpl implements ResourceFetcher {

  private static ResourceFetcherImpl instance;

  private ResourceFetcherImpl() {
  }

  public static ResourceFetcherImpl getInstance() {
    if(ObjectUtils.isEmpty(instance)) {
      synchronized(ResourceFetcherImpl.class) {
        if(ObjectUtils.isEmpty(instance)) {
          instance = new ResourceFetcherImpl();
        }
      }
    }
    return instance;
  }

  public Map<String, Object> resourceFetch(Resource resource) {
    String requestURI = resource.getRequestURI();
    System.out.println("resourceFetch start " + requestURI);
    OpenAPI openAPI = resource.getOpenAPI();
    //step1 get url
    Server server = openAPI.getServers().get(0);
    String url = server.getUrl();
    resource.setRequestURI(url + "/" + requestURI);
    System.out.println("/applications".equals("/" + requestURI));
    PathItem pathItem = openAPI.getPaths().get("/" + requestURI);
    switch (Operations.operation(pathItem)) {
      case POST: return post(resource, pathItem.getPost());
      default: return get(resource, pathItem.getGet());
    }
  }

  private Map<String, Object> get(Resource resource, Operation getOperation) {
    System.out.println("resourceFetch get start");
    //step1 filter parameter map
    OpenAPI openAPI = resource.getOpenAPI();
    Map<String, Object> payload = resource.getPayload();
    Optional<String> getSchemaOpt = OpenAPIBuilder.getVariables(Optional.ofNullable(openAPI), Schemas.getSchemaByOperation(Schemas.REQUEST_SCHEMA, Operations.GET.getName()));
    if(getSchemaOpt.isPresent()) {
      String getSchema = getSchemaOpt.get();
      Schema schema = getOperation.getRequestBody().getContent().get("application/json").getSchema().$ref(getSchema);
      if(!ObjectUtils.isEmpty(schema.getAllOf()) && !schema.getAllOf().isEmpty()) {
        ComposedSchema composedSchema = (ComposedSchema) schema;
        Map<String, Object> properties = composedSchema.getAllOf().get(0).getProperties();
        if(!ObjectUtils.isEmpty(properties) && !properties.isEmpty()) {
          Map<String, Object> payloadVerified = new HashMap<>();
          properties.keySet().stream().forEach(key -> {
            payloadVerified.put(key, payload.get(key));
          });
          //reload payload
          resource.setPayload(payloadVerified);
        }
//        System.out.println(properties.toString());
      } else {
        Map<String, Object> properties = schema.getProperties();
        if(!ObjectUtils.isEmpty(properties) && !properties.isEmpty()) {
          Map<String, Object> payloadVerified = new HashMap<>();
          properties.keySet().stream().forEach(key -> {
            payloadVerified.put(key, payload.get(key));
          });
          //reload payload
          resource.setPayload(payloadVerified);
        }
      }
    }

    Pair<Boolean, Map<String, Object>> booleanMapPair = OkHttpClientUtil.okHttpClient().httpGet(resource);
    if(!booleanMapPair.fst) {
      //TODO log error
      payload.put("", "");
    }
    payload.putAll(booleanMapPair.snd);
    System.out.println("resourceFetch get end");
    System.out.println("resourceFetch end");
    return payload;
  }

  private Map<String, Object> post(Resource resource, Operation postOperation) {
    System.out.println("resourceFetch post start");
    //step1 filter parameter map
    OpenAPI openAPI = resource.getOpenAPI();
    Map<String, Object> payload = resource.getPayload();
    Optional<String> getSchemaOpt = OpenAPIBuilder.getVariables(Optional.ofNullable(openAPI), Schemas.getSchemaByOperation(Schemas.REQUEST_SCHEMA, Operations.GET.getName()));
    if(getSchemaOpt.isPresent()) {
      String getSchema = getSchemaOpt.get();
      Schema schema = postOperation.getRequestBody().getContent().get("application/json").getSchema().$ref(getSchema);
      if(!ObjectUtils.isEmpty(schema.getAllOf()) && !schema.getAllOf().isEmpty()) {
        ComposedSchema composedSchema = (ComposedSchema) schema;
        Map<String, Object> properties = composedSchema.getAllOf().get(0).getProperties();
        if(!ObjectUtils.isEmpty(properties) && !properties.isEmpty()) {
          Map<String, Object> payloadVerified = new HashMap<>();
          properties.keySet().stream().forEach(key -> {
            payloadVerified.put(key, payload.get(key));
          });
          //reload payload
          resource.setPayload(payloadVerified);
        }
//        System.out.println(properties.toString());
      } else {
        Map<String, Object> properties = schema.getProperties();
        if(!ObjectUtils.isEmpty(properties) && !properties.isEmpty()) {
          Map<String, Object> payloadVerified = new HashMap<>();
          properties.keySet().stream().forEach(key -> {
            payloadVerified.put(key, payload.get(key));
          });
          //reload payload
          resource.setPayload(payloadVerified);
        }
      }
    }
    Pair<Boolean, Map<String, Object>> booleanMapPair = OkHttpClientUtil.okHttpClient().httpPost(resource);
    if(!booleanMapPair.fst) {
      //TODO log error
      payload.put("", "");
    }
    System.out.println("resourceFetch post end");
    System.out.println("resourceFetch end");
    payload.putAll(booleanMapPair.snd);
    return payload;
  }

  private Map<String, Object> put(Resource resource) {
    return null;
  }

  private Pair<Boolean, Object> delete(Resource resource) {
    return null;
  }

  private Map<String, Object> update(Resource resource) {
    return null;
  }
}
