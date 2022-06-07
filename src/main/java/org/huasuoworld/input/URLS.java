package org.huasuoworld.input;

import java.util.Optional;

public enum URLS {
  VALIDATION("src/main/resources/validation/%s.yaml"),
  FUNCTION("src/main/resources/function/%s.yaml"),
  RESOURCE("src/main/resources/resource/%s.yaml"),
  TASK("src/main/resources/task/%s.yaml"),
  ;
  private String url;
  URLS(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public static Optional getUrlByFilename(URLS urls, String filename) {
    switch (urls) {
      case VALIDATION: return Optional.ofNullable(String.format(URLS.VALIDATION.getUrl(), filename));
      case FUNCTION: return Optional.ofNullable(String.format(URLS.FUNCTION.getUrl(), filename));
      case RESOURCE: return Optional.ofNullable(String.format(URLS.RESOURCE.getUrl(), filename));
      case TASK: return Optional.ofNullable(String.format(URLS.TASK.getUrl(), filename));
      default: return Optional.empty();
    }
  }
}
