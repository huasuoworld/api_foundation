package org.huasuoworld.foundation.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GsonUtil {

  private static Logger log = LoggerFactory.getLogger(GsonUtil.class);

  static enum GsonSerializerFeature {
    SortField
  }

  private static final String DEFAULT_FORMAT = "yyyyMMddHHmmss";

  private static Gson gson = null;

  static {

    if (gson == null) {

      gson = new GsonBuilder().setDateFormat(DEFAULT_FORMAT).create();

    }
  }


  /**
   * 
   * @param object
   * @return
   */
  public static String toJson(Object object) {
    return gson.toJson(object);
  }

  /**
   * 指定排序规则排序
   * @param object
   * @param serializerFeature
   * @return
   */
  public static String toJson(Object object, GsonSerializerFeature serializerFeature) {

    if (serializerFeature == null) {
      return toJson(object);
    }
    return _sort(object, serializerFeature);
  }

  /**
   * 默认Key 排序
   * @param object
   * @return
   */
  public static String toJsonSort(Object object) {
    return _sort(object, GsonSerializerFeature.SortField);
  }

  /**
   * 
   * @param <T>
   * @param content
   * @param clazz
   * @return
   */
  public static <T> T parseObject(String content, Class<T> clazz) {
    try {
      return gson.fromJson(content, clazz);
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage(), e);
    }
    return null;
  }

  public static <T> T parseObject(String content, Type type) {
    try {
      return gson.fromJson(content, type);
    } catch (Exception e) {
       log.error(e.getMessage(), e);
       e.printStackTrace();
    }
    return null;
  }


  /**
   * 
   * @param <T>
   * @param content
   * @return
   */
  public static <T> List<Map<String, T>> parseListMaps(String content) {
    try {
      return gson.fromJson(content, new TypeToken<List<Map<String, T>>>() {}.getType());
    } catch (Exception e) {
    }
    return null;
  }


  /**
   * 
   * @param <T>
   * @param gsonString
   * @return
   */
  public static <T> Map<String, T> parseMaps(String gsonString) {
    Map<String, T> map = null;
    if (gson != null) {
      map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {}.getType());
    }
    return map;
  }

  /**
   * 新增
   * @param obj
   * @return
   */
  public static JsonElement toJsonElement(Object obj) {
    return gson.toJsonTree(obj);
  }

  /**
   * Key asc 比较器
   * @return
   */
  // private static Comparator<String> _keyAscComparator() {
  // Comparator<String> c = new Comparator<String>() {
  // public int compare(String o1, String o2) {
  // return o1.compareTo(o2);
  // }
  // };
  // return c;
  // }

  /**
   * 排序主方法
   * @param e
   */
  private static void _sortJsonElement(JsonElement e, GsonSerializerFeature sf) {
    if (e == null || e.isJsonNull()) {
      return;
    }
    if (e.isJsonPrimitive()) {
      return;
    }

    if (e.isJsonArray()) {
      JsonArray a = e.getAsJsonArray();
      for (Iterator<JsonElement> it = a.iterator(); it.hasNext();) {
        _sortJsonElement(it.next(), sf);
      }
      return;
    }

    if (e.isJsonObject()) {

      Map<String, JsonElement> tm = null;
      switch (sf) {
        case SortField:
        default:
          // 按照首字母升序
          tm = new TreeMap<String, JsonElement>((o1, o2) -> o1.compareTo(o2));
          break;
      }
      for (Entry<String, JsonElement> en : e.getAsJsonObject().entrySet()) {
        tm.put(en.getKey(), en.getValue());
      }

      for (Entry<String, JsonElement> en : tm.entrySet()) {
        e.getAsJsonObject().remove(en.getKey());
        e.getAsJsonObject().add(en.getKey(), en.getValue());
        _sortJsonElement(en.getValue(), sf);
      }
      return;
    }
  }

  private static String _sort(Object object, GsonSerializerFeature serializerFeature) {
    JsonElement e = gson.toJsonTree(object);
    _sortJsonElement(e, serializerFeature);
    return gson.toJson(e);
  }


  /**
   * 禁止字符转义
   * @param object
   * @return
   */
  public static String sortObjectToJson(Object object) {
    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    JsonElement e = gson.toJsonTree(object);
    _sortJsonElement(e, GsonSerializerFeature.SortField);
    return gson.toJson(e);
  }
}
