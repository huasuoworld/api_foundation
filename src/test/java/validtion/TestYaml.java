package validtion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import org.junit.jupiter.api.Test;

/**
 * @author: huacailiang
 * @date: 2022/5/6
 * @description:
 **/
public class TestYaml {

  @Test
  public void resolveAllOfWithoutAggregatingParameters() {
    ParseOptions options = new ParseOptions();
    options.setResolveFully(true);
    options.setResolveCombinators(true);

    // Testing components/schemas
    OpenAPI openAPI = new OpenAPIV3Parser().readLocation("src/test/resources/validation/test.yaml", null, options).getOpenAPI();

    ComposedSchema allOf = (ComposedSchema) openAPI.getComponents().getSchemas().get("ExtendedAddress");
    assertEquals(allOf.getAllOf().size(), 2);

    assertTrue(allOf.getAllOf().get(0).get$ref() != null);
    assertTrue(allOf.getAllOf().get(1).getProperties().containsKey("gps"));


    // Testing path item
    ObjectSchema schema = (ObjectSchema) openAPI.getPaths().get("/withInvalidComposedModel").getPost().getRequestBody().getContent().get("application/json").getSchema();

    assertEquals(schema.getProperties().size(), 5);
    assertTrue(schema.getProperties().containsKey("street"));
    assertTrue(schema.getProperties().containsKey("gps"));
    System.out.println(schema.getProperties().get("street").getType());

  }
}
