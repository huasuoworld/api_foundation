package org.huasuoworld.resource.impl;

import io.swagger.v3.oas.models.OpenAPI;
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
    Schema schema;
    Operations operation = Operations.operation(pathItem);
    switch (operation) {
      case POST: {
        Optional<String> getSchemaOpt = OpenAPIBuilder.getVariables(Optional.ofNullable(openAPI), Schemas.getSchemaByOperation(Schemas.REQUEST_SCHEMA, Operations.GET.getName()));
        if(!getSchemaOpt.isPresent()) {
          return new HashMap<>();
        }
        String getSchema = getSchemaOpt.get();
        schema = pathItem.getPost().getRequestBody().getContent().get("application/json").getSchema().$ref(getSchema);
        return options(resource, operation, schema);
      }
      case PUT: {
        Optional<String> putSchemaOpt = OpenAPIBuilder.getVariables(Optional.ofNullable(openAPI), Schemas.getSchemaByOperation(Schemas.REQUEST_SCHEMA, Operations.PUT.getName()));
        if(!putSchemaOpt.isPresent()) {
          return new HashMap<>();
        }
        String putSchema = putSchemaOpt.get();
        schema = pathItem.getPut().getRequestBody().getContent().get("application/json").getSchema().$ref(putSchema);
        return options(resource, operation, schema);
      }
      case PATCH: {
        Optional<String> patchSchemaOpt = OpenAPIBuilder.getVariables(Optional.ofNullable(openAPI), Schemas.getSchemaByOperation(Schemas.REQUEST_SCHEMA, Operations.PATCH.getName()));
        if(!patchSchemaOpt.isPresent()) {
          return new HashMap<>();
        }
        String patchSchema = patchSchemaOpt.get();
        schema = pathItem.getPatch().getRequestBody().getContent().get("application/json").getSchema().$ref(patchSchema);
        return options(resource, operation, schema);
      }
      case DELETE: {
        Optional<String> deleteSchemaOpt = OpenAPIBuilder.getVariables(Optional.ofNullable(openAPI), Schemas.getSchemaByOperation(Schemas.REQUEST_SCHEMA, Operations.DELETE.getName()));
        if(!deleteSchemaOpt.isPresent()) {
          return new HashMap<>();
        }
        String deleteSchema = deleteSchemaOpt.get();
        schema = pathItem.getDelete().getRequestBody().getContent().get("application/json").getSchema().$ref(deleteSchema);
        return options(resource, operation, schema);
      }
      default: {
        Optional<String> getSchemaOpt = OpenAPIBuilder.getVariables(Optional.ofNullable(openAPI), Schemas.getSchemaByOperation(Schemas.REQUEST_SCHEMA, Operations.GET.getName()));
        if(!getSchemaOpt.isPresent()) {
          return new HashMap<>();
        }
        String getSchema = getSchemaOpt.get();
        schema = pathItem.getGet().getRequestBody().getContent().get("application/json").getSchema().$ref(getSchema);
        return options(resource, operation, schema);
      }
    }
  }

  private Map<String, Object> options(Resource resource, Operations operations, Schema schema) {
    System.out.println("resourceFetch get start");
    //step1 filter parameter map
    Map<String, Object> payload = resource.getPayload();
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

    Pair<Boolean, Map<String, Object>> booleanMapPair = OkHttpClientUtil.okHttpClient().httpOption(operations, resource);
    if(!booleanMapPair.fst) {
      //TODO log error
      payload.put("", "");
    }
    payload.putAll(booleanMapPair.snd);
    System.out.println("resourceFetch get end");
    System.out.println("resourceFetch end");
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
