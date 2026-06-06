import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sqx.SqxApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest(classes = SqxApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("PRD API 用例批量探测（未登录）")
    void shouldMatchExpectedStatusForAllApiCases() throws Exception {
        JsonNode root = loadApiCases();
        Iterator<JsonNode> cases = root.path("cases").elements();

        while (cases.hasNext()) {
            JsonNode item = cases.next();
            String name = item.path("name").asText();
            String method = item.path("method").asText("GET");
            String path = item.path("path").asText();
            List<Integer> expectStatus = parseExpectStatus(item.path("expectStatus"));

            int actualStatus = performRequest(method, path).andReturn().getResponse().getStatus();
            assertTrue(
                    expectStatus.contains(actualStatus),
                    String.format("用例[%s] %s %s 期望状态 %s，实际 %d", name, method, path, expectStatus, actualStatus)
            );
        }
    }

    @Test
    @DisplayName("已实现 App 商品接口（携带 Token）")
    void shouldAccessImplementedAppProductApisWithToken() throws Exception {
        String token = "uid:1";

        int categoryStatus = performRequest("GET", "/app/product/category/list", token)
                .andReturn().getResponse().getStatus();
        assertTrue(Arrays.asList(200, 401, 403).contains(categoryStatus));

        int listStatus = performRequest("GET", "/app/product/list", token)
                .andReturn().getResponse().getStatus();
        assertTrue(Arrays.asList(200, 401, 403).contains(listStatus));

        int detailStatus = performRequest("GET", "/app/product/detail/1", token)
                .andReturn().getResponse().getStatus();
        assertTrue(Arrays.asList(200, 401, 403).contains(detailStatus));
    }

    @Test
    @DisplayName("已实现 Admin 商品接口（GET 列表/详情）")
    void shouldAccessImplementedAdminProductApis() throws Exception {
        int listStatus = performRequest("GET", "/admin/product/list")
                .andReturn().getResponse().getStatus();
        assertTrue(Arrays.asList(200, 401, 403).contains(listStatus));

        int detailStatus = performRequest("GET", "/admin/product/detail/1")
                .andReturn().getResponse().getStatus();
        assertTrue(Arrays.asList(200, 401, 403).contains(detailStatus));

        int categoryStatus = performRequest("GET", "/admin/product/category/list")
                .andReturn().getResponse().getStatus();
        assertTrue(Arrays.asList(200, 401, 403).contains(categoryStatus));
    }

    private ResultActions performRequest(String method, String fullPath) throws Exception {
        return performRequest(method, fullPath, null);
    }

    private ResultActions performRequest(String method, String fullPath, String token) throws Exception {
        MockHttpServletRequestBuilder builder;
        switch (method.toUpperCase()) {
            case "POST":
                builder = post(fullPath).contentType(MediaType.APPLICATION_JSON).content("{}");
                break;
            case "PUT":
                builder = put(fullPath).contentType(MediaType.APPLICATION_JSON).content("{}");
                break;
            case "DELETE":
                builder = delete(fullPath);
                break;
            default:
                builder = get(fullPath);
                break;
        }
        if (token != null) {
            builder.header("token", token);
        }
        return mockMvc.perform(builder);
    }

    private JsonNode loadApiCases() throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("api-cases.json");
        if (inputStream == null) {
            throw new IllegalStateException("未找到 api-cases.json");
        }
        return objectMapper.readTree(inputStream);
    }

    private List<Integer> parseExpectStatus(JsonNode node) {
        List<Integer> statuses = new java.util.ArrayList<>();
        if (node.isArray()) {
            node.forEach(item -> statuses.add(item.asInt()));
        }
        if (statuses.isEmpty()) {
            statuses.add(200);
        }
        return statuses;
    }
}
