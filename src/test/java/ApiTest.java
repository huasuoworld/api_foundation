import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.huasuoworld.input.ApiRequest;
import org.huasuoworld.models.InputParameter;
import org.huasuoworld.util.GsonUtil;
import org.junit.jupiter.api.Test;

/**
 * @author: huacailiang
 * @date: 2022/6/7
 * @description:
 **/
public class ApiTest {

  @Test
  public void apiRequest() throws Exception {
    String validationPath = "src/test/resources/validation/withInvalidComposedModel.yaml";
    List<String> validationPaths = new ArrayList<>();
    validationPaths.add(validationPath);
    String requestURI = "/withInvalidComposedModel";
    Map<String, Object> headers = new HashMap<>();
    headers.put("token", "h1");
    Map<String, Object> payload = new HashMap<>();
    payload.put("street", "nanjing east road");
    payload.put("city", "shanghai");
    payload.put("state", "china");
    payload.put("zip", "200000");
    payload.put("gps", "120,122");
    ApiRequest apiRequest = new ApiRequest(validationPaths);
    InputParameter inputParameter = new InputParameter();
    inputParameter.setHeaders(headers);
    inputParameter.setPayload(payload);
    inputParameter.setRequestURI(requestURI);
    Map<String, Object> process = apiRequest.process(inputParameter);
    System.out.println(GsonUtil.toJson(process));
  }
}
