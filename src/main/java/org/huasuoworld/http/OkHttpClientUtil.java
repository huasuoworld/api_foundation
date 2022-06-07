package org.huasuoworld.http;

import com.sun.tools.javac.util.Pair;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.ObjectUtils;
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

  public Pair<Boolean, Map<String, Object>> httpGet(String url) {
    Request request = new Request.Builder()
        .url(url)
        .build();

    try (Response response = okHttpClient.newCall(request).execute()) {
      if(response.isSuccessful()) {
        return Pair.of(Boolean.TRUE, GsonUtil.parseMaps(response.body().toString()));
      } else {
        return Pair.of(Boolean.FALSE, GsonUtil.parseMaps(GsonUtil.toJson(response)));
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Pair.of(Boolean.FALSE, new HashMap<>());
    }
  }

  public Pair<Boolean, Map<String, Object>> httpPost(String url, Map<String, Object> parameterMap) {
    RequestBody body = RequestBody.create(GsonUtil.toJson(parameterMap), JSON);
    Request request = new Request.Builder()
        .url(url)
        .post(body)
        .build();
    try (Response response = okHttpClient.newCall(request).execute()) {
      if(response.isSuccessful()) {
        return Pair.of(Boolean.TRUE, GsonUtil.parseMaps(response.body().toString()));
      } else {
        return Pair.of(Boolean.FALSE, GsonUtil.parseMaps(GsonUtil.toJson(response)));
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Pair.of(Boolean.FALSE, new HashMap<>());
    }
  }
}