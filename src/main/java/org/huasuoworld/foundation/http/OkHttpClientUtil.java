package org.huasuoworld.foundation.http;

import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;
import org.apache.commons.lang3.StringUtils;
import org.huasuoworld.foundation.output.Constant;
import org.huasuoworld.foundation.resource.Operations;
import org.huasuoworld.foundation.util.Pair;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.ObjectUtils;
import org.huasuoworld.foundation.models.Resource;
import org.huasuoworld.foundation.util.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: huacailiang
 * @date: 2022/5/30
 * @description:
 **/
public class OkHttpClientUtil {

  private static Logger log = LoggerFactory.getLogger(OkHttpClientUtil.class);

  private static OkHttpClient okHttpClient = new OkHttpClient();
  private static OkHttpClientUtil okHttpClientUtil;
  public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
  private OkHttpClientUtil() {}

  public static OkHttpClientUtil okHttpClient() {
    if(ObjectUtils.isEmpty(okHttpClientUtil)) {
      synchronized(OkHttpClientUtil.class) {
        if(ObjectUtils.isEmpty(okHttpClientUtil)) {
          okHttpClientUtil = new OkHttpClientUtil();
        }
      }
    }
    return okHttpClientUtil;
  }

  public Pair<Boolean, Map<String, Object>> httpOption(Operations operations, Resource resource, String requestURI) {
    switch (operations) {
      case POST: return  httpPost(resource, requestURI);
      case PUT: return httpPut(resource, requestURI);
      case PATCH: return httpPatch(resource, requestURI);
      case DELETE: return httpDelete(resource, requestURI);
      default: return httpGet(resource, requestURI);
    }
  }

  public Pair<Boolean, Map<String, Object>> httpGet(Resource resource, String requestURI) {
    Map<String, Object> responseMap = new HashMap<>();
    if(StringUtils.isEmpty(requestURI)) {
      return  Pair.of(Boolean.TRUE, responseMap);
    }
    Builder requestURLBuilder = HttpUrl.parse(requestURI).newBuilder();
    Headers.Builder headerBuilder = new Headers.Builder();
    if(!ObjectUtils.isEmpty(resource.getHeaders()) && !resource.getHeaders().isEmpty()) {
      resource.getHeaders().keySet().forEach(headerKey -> {
        if(!ObjectUtils.isEmpty(resource.getHeaders().get(headerKey))) {
          headerBuilder.add(headerKey, resource.getHeaders().get(headerKey).toString());
        }
      });
    }
    if(!ObjectUtils.isEmpty(resource.getPayload()) && !resource.getPayload().isEmpty()) {
      resource.getPayload().keySet().forEach(payloadKey -> {
        if(!ObjectUtils.isEmpty(resource.getPayload().get(payloadKey))) {
          requestURLBuilder.addQueryParameter(payloadKey, resource.getPayload().get(payloadKey).toString());
        }
      });
    }
    //Authorization from yaml config
    if(StringUtils.isNotEmpty(resource.getUsername()) && !StringUtils.isNotEmpty(resource.getPassword())) {
      String credential = Credentials.basic(resource.getUsername(), resource.getPassword());
      headerBuilder.add("Authorization", credential);
    }

    System.out.println(requestURLBuilder.build());
    Request request = new Request.Builder()
        .url(requestURLBuilder.build())
        .headers(headerBuilder.build())
        .build();
    try (Response response = okHttpClient.newCall(request).execute()) {
      String responseBody = new String(response.body().bytes(), "UTF-8");
      System.out.println("httpGet " + responseBody);
      Map<String, Object> httpResponseMap = GsonUtil.parseMaps(responseBody);
      if(!ObjectUtils.isEmpty(httpResponseMap)) {
        responseMap.putAll(httpResponseMap);
      }
      responseMap.put(Constant.HTTP_STATUS_CODE, response.code());
      if(response.isSuccessful()) {
        return Pair.of(Boolean.TRUE, responseMap);
      } else {
        return Pair.of(Boolean.FALSE, responseMap);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      e.printStackTrace();
      return Pair.of(Boolean.FALSE, new HashMap<>());
    }
  }

  public Pair<Boolean, Map<String, Object>> httpPost(Resource resource, String requestURI) {
    Map<String, Object> responseMap = new HashMap<>();
    Headers.Builder headerBuilder = new Headers.Builder();
    if(!ObjectUtils.isEmpty(resource.getHeaders()) && !resource.getHeaders().isEmpty()) {
      resource.getHeaders().keySet().forEach(headerKey -> {
        if(!ObjectUtils.isEmpty(resource.getHeaders().get(headerKey))) {
          headerBuilder.add(headerKey, resource.getHeaders().get(headerKey).toString());
        }
      });
    }
    //Authorization from yaml config
    if(StringUtils.isNotEmpty(resource.getUsername()) && !StringUtils.isNotEmpty(resource.getPassword())) {
      String credential = Credentials.basic(resource.getUsername(), resource.getPassword());
      headerBuilder.add("Authorization", credential);
    }
    RequestBody body = RequestBody.create(GsonUtil.toJson(resource.getPayload()), JSON);
    Request request = new Request.Builder()
        .url(requestURI)
        .headers(headerBuilder.build())
        .post(body)
        .build();
    try (Response response = okHttpClient.newCall(request).execute()) {
      String responseBody = new String(response.body().bytes(), "UTF-8");
      System.out.println("httpPost..." + responseBody);
      Map<String, Object> httpResponseMap = GsonUtil.parseMaps(responseBody);
      if(!ObjectUtils.isEmpty(httpResponseMap)) {
        responseMap.putAll(httpResponseMap);
      }
      responseMap.put(Constant.HTTP_STATUS_CODE, response.code());
      if(response.isSuccessful()) {
        return Pair.of(Boolean.TRUE, responseMap);
      } else {
        return Pair.of(Boolean.FALSE, responseMap);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Pair.of(Boolean.FALSE, new HashMap<>());
    }
  }

  public Pair<Boolean, Map<String, Object>> httpPut(Resource resource, String requestURI) {
    Map<String, Object> responseMap = new HashMap<>();
    Headers.Builder headerBuilder = new Headers.Builder();
    if(!ObjectUtils.isEmpty(resource.getHeaders()) && !resource.getHeaders().isEmpty()) {
      resource.getHeaders().keySet().forEach(headerKey -> {
        if(!ObjectUtils.isEmpty(resource.getHeaders().get(headerKey))) {
          headerBuilder.add(headerKey, resource.getHeaders().get(headerKey).toString());
        }
      });
    }
    //Authorization from yaml config
    if(StringUtils.isNotEmpty(resource.getUsername()) && !StringUtils.isNotEmpty(resource.getPassword())) {
      String credential = Credentials.basic(resource.getUsername(), resource.getPassword());
      headerBuilder.add("Authorization", credential);
    }
    RequestBody body = RequestBody.create(GsonUtil.toJson(resource.getPayload()), JSON);
    Request request = new Request.Builder()
        .url(requestURI)
        .headers(headerBuilder.build())
        .put(body)
        .build();
    try (Response response = okHttpClient.newCall(request).execute()) {
      String responseBody = new String(response.body().bytes(), "UTF-8");
      System.out.println("httpPut..." + responseBody);
      Map<String, Object> httpResponseMap = GsonUtil.parseMaps(responseBody);
      if(!ObjectUtils.isEmpty(httpResponseMap)) {
        responseMap.putAll(httpResponseMap);
      }
      responseMap.put(Constant.HTTP_STATUS_CODE, response.code());
      if(response.isSuccessful()) {
        return Pair.of(Boolean.TRUE, responseMap);
      } else {
        return Pair.of(Boolean.FALSE, httpResponseMap);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Pair.of(Boolean.FALSE, new HashMap<>());
    }
  }

  public Pair<Boolean, Map<String, Object>> httpPatch(Resource resource, String requestURI) {
    Map<String, Object> responseMap = new HashMap<>();
    Headers.Builder headerBuilder = new Headers.Builder();
    if(!ObjectUtils.isEmpty(resource.getHeaders()) && !resource.getHeaders().isEmpty()) {
      resource.getHeaders().keySet().forEach(headerKey -> {
        if(!ObjectUtils.isEmpty(resource.getHeaders().get(headerKey))) {
          headerBuilder.add(headerKey, resource.getHeaders().get(headerKey).toString());
        }
      });
    }
    //Authorization from yaml config
    if(StringUtils.isNotEmpty(resource.getUsername()) && !StringUtils.isNotEmpty(resource.getPassword())) {
      String credential = Credentials.basic(resource.getUsername(), resource.getPassword());
      headerBuilder.add("Authorization", credential);
    }
    RequestBody body = RequestBody.create(GsonUtil.toJson(resource.getPayload()), JSON);
    Request request = new Request.Builder()
        .url(requestURI)
        .headers(headerBuilder.build())
        .patch(body)
        .build();
    try (Response response = okHttpClient.newCall(request).execute()) {
      String responseBody = new String(response.body().bytes(), "UTF-8");
      System.out.println("httpPatch..." + responseBody);
      Map<String, Object> httpResponseMap = GsonUtil.parseMaps(responseBody);
      if(!ObjectUtils.isEmpty(httpResponseMap)) {
        responseMap.putAll(httpResponseMap);
      }
      responseMap.put(Constant.HTTP_STATUS_CODE, response.code());
      if(response.isSuccessful()) {
        return Pair.of(Boolean.TRUE, responseMap);
      } else {
        return Pair.of(Boolean.FALSE, responseMap);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Pair.of(Boolean.FALSE, new HashMap<>());
    }
  }

  public Pair<Boolean, Map<String, Object>> httpDelete(Resource resource, String requestURI) {
    Map<String, Object> responseMap = new HashMap<>();
    Headers.Builder headerBuilder = new Headers.Builder();
    if(!ObjectUtils.isEmpty(resource.getHeaders()) && !resource.getHeaders().isEmpty()) {
      resource.getHeaders().keySet().forEach(headerKey -> {
        if(!ObjectUtils.isEmpty(resource.getHeaders().get(headerKey))) {
          headerBuilder.add(headerKey, resource.getHeaders().get(headerKey).toString());
        }
      });
    }
    //Authorization from yaml config
    if(StringUtils.isNotEmpty(resource.getUsername()) && !StringUtils.isNotEmpty(resource.getPassword())) {
      String credential = Credentials.basic(resource.getUsername(), resource.getPassword());
      headerBuilder.add("Authorization", credential);
    }
    RequestBody body = RequestBody.create(GsonUtil.toJson(resource.getPayload()), JSON);
    Request request = new Request.Builder()
        .url(requestURI)
        .headers(headerBuilder.build())
        .delete(body)
        .build();
    try (Response response = okHttpClient.newCall(request).execute()) {
      String responseBody = new String(response.body().bytes(), "UTF-8");
      System.out.println("httpDelete..." + responseBody);
      Map<String, Object> httpResponseMap = GsonUtil.parseMaps(responseBody);
      if(!ObjectUtils.isEmpty(httpResponseMap)) {
        responseMap.putAll(httpResponseMap);
      }
      responseMap.put(Constant.HTTP_STATUS_CODE, response.code());
      if(response.isSuccessful()) {
        return Pair.of(Boolean.TRUE, responseMap);
      } else {
        return Pair.of(Boolean.FALSE, responseMap);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Pair.of(Boolean.FALSE, new HashMap<>());
    }
  }

  public static Map<String, String> fetchConfigMap(String url, String area) {
    Builder requestURLBuilder = HttpUrl.parse(url).newBuilder();
    requestURLBuilder.addQueryParameter("application", area);
    Headers.Builder headerBuilder = new Headers.Builder();
    headerBuilder.add("area", area);
    Request request = new Request.Builder().url(requestURLBuilder.build()).headers(headerBuilder.build()).build();
    try (Response response = okHttpClient.newCall(request).execute()) {
      String responseBody = new String(response.body().bytes(), "UTF-8");
      System.out.println("httpGet " + responseBody);
      Map<String, String> httpResponseMap = GsonUtil.parseMaps(responseBody);
      return httpResponseMap;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      e.printStackTrace();
      return new HashMap<>();
    }
  }
}
