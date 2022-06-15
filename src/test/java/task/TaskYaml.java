package task;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import org.junit.jupiter.api.Test;

/**
 * @author: huacailiang
 * @date: 2022/5/27
 * @description:
 **/
public class TaskYaml {

  @Test
  public void validResource() {
    ParseOptions options = new ParseOptions();
    options.setResolveFully(true);
    options.setResolveCombinators(true);

    // Testing components/schemas
    OpenAPI openAPI = new OpenAPIV3Parser().readLocation("src/test/resources/resource/HelloTask.yaml", null, options).getOpenAPI();
    System.out.println(openAPI);

  }
}
