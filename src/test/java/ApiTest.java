import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.huasuoworld.input.ApiRequest;
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
    Function f = new Function() {
      @Override
      public Object apply(Object o) {
        return null;
      }
    };
    String requestURI = "/withInvalidComposedModel";
    Map<String, Object> headers = new HashMap<>();
    headers.put("header1", "h1");
    Map<String, Object> payload = new HashMap<>();
    payload.put("street", "nanjing east road");
    payload.put("city", "shanghai");
    payload.put("state", "china");
    payload.put("zip", "200000");
    payload.put("gps", "120,122");
    ApiRequest apiRequest = new ApiRequest();
    Map<String, Object> process = apiRequest.headers(headers).payload(payload).requestURI(requestURI).process();
    System.out.println(GsonUtil.toJson(process));
  }
}
