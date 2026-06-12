package com.sqx.delivery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sqx.SqxApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SqxApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeliveryApiIntegrationTest {

    private static String responseBody(org.springframework.mock.web.MockHttpServletResponse response) {
        return new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;

    @BeforeEach
    void loginAdmin() throws Exception {
        adminToken = objectMapper.readTree(responseBody(
                mockMvc.perform(post("/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse())).path("data").path("token").asText();
    }

    static Stream<JsonNode> apiCases() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = new ClassPathResource("delivery-tests/api-cases.json").getInputStream();
        JsonNode root = mapper.readTree(inputStream);
        List<JsonNode> cases = new ArrayList<>();
        Iterator<JsonNode> iterator = root.path("cases").elements();
        while (iterator.hasNext()) {
            cases.add(iterator.next());
        }
        return cases.stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("apiCases")
    @DisplayName("delivery-tests/api-cases.json")
    void runApiCase(JsonNode testCase) throws Exception {
        String method = testCase.path("method").asText("GET");
        String path = testCase.path("path").asText();
        int expectCode = testCase.path("expectCode").asInt(0);

        MockHttpServletRequestBuilder builder = buildRequest(method, path);
        if (testCase.has("body") && !testCase.get("body").isNull()) {
            builder.contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testCase.get("body")));
        }
        if ("admin".equals(testCase.path("auth").asText())) {
            builder.header("token", adminToken);
        }

        ResultActions actions = mockMvc.perform(builder).andExpect(status().isOk());
        JsonNode body = objectMapper.readTree(responseBody(actions.andReturn().getResponse()));
        assertEquals(expectCode, body.path("code").asInt(),
                () -> testCase.path("name").asText() + " 响应: " + body);
    }

    @Test
    @DisplayName("管理端筛选-关键词过滤仅返回匹配账号")
    void accountListFilterByKeyword() throws Exception {
        JsonNode data = objectMapper.readTree(responseBody(
                mockMvc.perform(get("/admin/account/list")
                        .header("token", adminToken)
                        .param("account", "admin"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse())).path("data");
        assertTrue(data.path("total").asInt() >= 1);
        for (JsonNode item : data.path("list")) {
            assertTrue(item.path("account").asText().contains("admin"));
        }
    }

    @Test
    @DisplayName("管理端分页-自定义 pageSize")
    void accountListPagination() throws Exception {
        JsonNode data = objectMapper.readTree(responseBody(
                mockMvc.perform(get("/admin/account/list")
                        .header("token", adminToken)
                        .param("page", "1")
                        .param("pageSize", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse())).path("data");
        assertEquals(1, data.path("pageSize").asInt());
        assertTrue(data.path("list").size() <= 1);
    }

    @Test
    @DisplayName("权限边界-不可删除当前登录管理员")
    void deleteCurrentAdminShouldFail() throws Exception {
        JsonNode loginBody = objectMapper.readTree(responseBody(
                mockMvc.perform(post("/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()));
        long adminId = loginBody.path("data").path("id").asLong();
        String token = loginBody.path("data").path("token").asText();

        JsonNode body = objectMapper.readTree(responseBody(
                mockMvc.perform(delete("/admin/account/delete/" + adminId)
                        .header("token", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()));
        assertEquals(500, body.path("code").asInt());
        assertTrue(body.path("msg").asText().contains("不可删除"),
                () -> "实际响应: " + body);
    }

    private MockHttpServletRequestBuilder buildRequest(String method, String path) {
        switch (method.toUpperCase()) {
            case "POST":
                return post(path);
            case "PUT":
                return put(path);
            case "DELETE":
                return delete(path);
            default:
                return get(path);
        }
    }
}
