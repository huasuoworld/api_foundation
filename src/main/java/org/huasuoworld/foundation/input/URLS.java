package org.huasuoworld.foundation.input;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public enum URLS {
  VALIDATION("src/main/resources/validation/%s.yaml"),
  FUNCTION("src/main/resources/function/%s.yaml"),
  RESOURCE("src/main/resources/resource/%s.yaml"),
  TASK("src/main/resources/task/%s.yaml"),
  ;
  public static final String OPENAPI = "openapi.";
  public static final String URL = ".url";
  private String url;
  URLS(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public static Optional getUrlByFilename(URLS urls, String filename) {
    String openApiValidation = System.getProperty(OPENAPI + URLS.VALIDATION.name().toLowerCase() + URL);
    String openApiFunction = System.getProperty(OPENAPI + URLS.FUNCTION.name().toLowerCase() + URL);
    String openApiResource = System.getProperty(OPENAPI + URLS.RESOURCE.name().toLowerCase() + URL);
    String openApiTask = System.getProperty(OPENAPI + URLS.TASK.name().toLowerCase() + URL);
    switch (urls) {
      case VALIDATION:
        return validationPath(filename, openApiValidation, URLS.VALIDATION);
      case FUNCTION:
        return validationPath(filename, openApiFunction, URLS.FUNCTION);
      case RESOURCE:
        return validationPath(filename, openApiResource, URLS.RESOURCE);
      case TASK:
        return validationPath(filename, openApiTask, URLS.TASK);
      default: return Optional.empty();
    }
  }

  private static Optional validationPath(String filename, String urlConfigPath, URLS validation) {
    if (!StringUtils.isEmpty(urlConfigPath)) {
      return Optional.ofNullable(String.format(urlConfigPath, filename));
    } else {
      return Optional.ofNullable(String.format(validation.getUrl(), filename));
    }
  }
}
