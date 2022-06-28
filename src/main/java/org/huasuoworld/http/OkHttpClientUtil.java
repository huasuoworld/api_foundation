package org.huasuoworld.http;

import org.huasuoworld.output.Constant;
import org.huasuoworld.resource.Operations;
import org.huasuoworld.util.Pair;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.ObjectUtils;
import org.huasuoworld.models.Resource;
import org.huasuoworld.util.GsonUtil;
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

  public Pair<Boolean, Map<String, Object>> httpOption(Operations operations, Resource resource) {
    switch (operations) {
      case POST: return  httpPost(resource);
      case PUT: return httpPut(resource);
      case PATCH: return httpPatch(resource);
      case DELETE: return httpDelete(resource);
      default: return httpGet(resource);
    }
  }

  public Pair<Boolean, Map<String, Object>> httpGet(Resource resource) {
    Map<String, Object> responseMap = new HashMap<>();
    Request request = new Request.Builder()
        .url(resource.getRequestURI())
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

  public Pair<Boolean, Map<String, Object>> httpPost(Resource resource) {
    Map<String, Object> responseMap = new HashMap<>();
    RequestBody body = RequestBody.create(GsonUtil.toJson(resource.getPayload()), JSON);
    Request request = new Request.Builder()
        .url(resource.getRequestURI())
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

  public Pair<Boolean, Map<String, Object>> httpPut(Resource resource) {
    Map<String, Object> responseMap = new HashMap<>();
    RequestBody body = RequestBody.create(GsonUtil.toJson(resource.getPayload()), JSON);
    Request request = new Request.Builder()
        .url(resource.getRequestURI())
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

  public Pair<Boolean, Map<String, Object>> httpPatch(Resource resource) {
    Map<String, Object> responseMap = new HashMap<>();
    RequestBody body = RequestBody.create(GsonUtil.toJson(resource.getPayload()), JSON);
    Request request = new Request.Builder()
        .url(resource.getRequestURI())
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

  public Pair<Boolean, Map<String, Object>> httpDelete(Resource resource) {
    Map<String, Object> responseMap = new HashMap<>();
    RequestBody body = RequestBody.create(GsonUtil.toJson(resource.getPayload()), JSON);
    Request request = new Request.Builder()
        .url(resource.getRequestURI())
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
}
