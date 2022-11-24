package org.huasuoworld.foundation.resource;

public enum Schemas {
  REQUEST_SCHEMA("/applications/%s/requestBody/schemas"),
  ;

  private String path;
  Schemas(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }

  public static String getSchemaByOperation(Schemas schemas, String operation) {
    return String.format(schemas.getPath(), operation);
  }
}
