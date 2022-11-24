import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.huasuoworld.foundation.input.ApiRequest;
import org.huasuoworld.foundation.util.GsonUtil;
import org.junit.jupiter.api.Test;

/**
 * @author: huacailiang
 * @date: 2022/6/7
 * @description:
 **/
public class ApiPostTest {

  @Test
  public void apiRequest() throws Exception {
    String validationPath = "src/test/resources/validation/withInvalidComposedModel.yaml";
    List<String> validationPaths = new ArrayList<>();
    validationPaths.add(validationPath);
    String requestURI = "/applications";
    Map<String, Object> headers = new HashMap<>();
    headers.put("token", "h1");
    Map<String, Object> payload = new HashMap<>();
    payload.put("appName", "nanjing east road");
    payload.put("appProfile", "shanghai");
    payload.put("appLabel", "china");
    Date date = new Date();
    payload.put("appCreated", date.getTime());
    payload.put("appModified", date.getTime());
    System.out.println(GsonUtil.toJson(payload));
    ApiRequest apiRequest = new ApiRequest(validationPaths);
//    Map<String, Object> process = apiRequest.headers(headers).payload(payload).requestURI(requestURI).process();
//    System.out.println(GsonUtil.toJson(process));
  }
}
