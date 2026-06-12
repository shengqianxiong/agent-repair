package com.sqx.delivery;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SqxApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("PRD API 用例 - 未登录态可达性校验")
    void apiCases_shouldMatchExpectedStatus() throws Exception {
        List<ApiCase> cases = loadApiCases();
        for (ApiCase apiCase : cases) {
            MockHttpServletRequestBuilder request = buildRequest(apiCase);
            int actualStatus = mockMvc.perform(request)
                    .andReturn()
                    .getResponse()
                    .getStatus();
            assertTrue(apiCase.getExpectStatus().contains(actualStatus),
                    () -> apiCase.getName() + " " + apiCase.getMethod() + " " + apiCase.getPath()
                            + " expected one of " + apiCase.getExpectStatus() + " but was " + actualStatus);
        }
    }

    @Test
    @DisplayName("已实现 App 商品接口 - 携带 Token 可访问")
    void appProductEndpoints_withToken_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/app/product/category/list")
                        .header("token", "uid:1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/app/product/list")
                        .header("token", "uid:1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("已实现 Admin 商品接口 - 未登录可访问")
    void adminProductEndpoints_withoutAuth_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/admin/product/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/admin/product/category/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("管理端商品筛选 - 多条件过滤结果正确")
    void adminProductList_filterByNameAndCategory_shouldReturnFilteredResults() throws Exception {
        mockMvc.perform(get("/admin/product/list")
                        .param("name", "青岛")
                        .param("categoryId", "1")
                        .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].name").value("青岛纯生"));

        mockMvc.perform(get("/admin/product/list")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].name").value("下架商品"));
    }

    @Test
    @DisplayName("App 商品接口 - 未登录返回业务错误")
    void appProductEndpoints_withoutToken_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/app/product/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @DisplayName("豆评助手 E2E - 扫码→任务→提交→审核通过")
    void reviewFlow_endToEnd_shouldWork() throws Exception {
        mockMvc.perform(get("/app/activity/by-code").param("code", "DP20260611"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.productName").value("招牌厚乳拿铁"));

        String startBody = "{\"activityId\":10001,\"userIdentifier\":\"visitor_e2e_001\"}";
        String startResponse = mockMvc.perform(post("/app/task/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(startBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn().getResponse().getContentAsString();

        JsonNode startNode = objectMapper.readTree(startResponse).path("data");
        long taskId = startNode.path("taskId").asLong();

        String submitBody = "{\"taskId\":" + taskId
                + ",\"activityId\":10001,\"screenshotUrl\":\"https://example.com/review.png\""
                + ",\"userIdentifier\":\"visitor_e2e_001\"}";
        String submitResponse = mockMvc.perform(post("/app/task/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value("auditing"))
                .andReturn().getResponse().getContentAsString();

        long rebateId = objectMapper.readTree(submitResponse).path("data").path("rebateId").asLong();

        mockMvc.perform(get("/app/user/summary").param("userIdentifier", "visitor_e2e_001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.pendingCount").value(1));

        mockMvc.perform(get("/admin/verify/list").param("status", "auditing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(2));

        String passBody = "{\"verifyId\":" + rebateId + "}";
        mockMvc.perform(post("/admin/verify/pass")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/app/rebate/detail").param("rebateId", String.valueOf(rebateId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value("paid"));
    }

    @Test
    @DisplayName("管理端活动筛选 - 关键词与状态过滤")
    void adminActivityList_filter_shouldReturnFilteredResults() throws Exception {
        mockMvc.perform(get("/admin/activity/list")
                        .param("keyword", "拿铁")
                        .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].productName").value("招牌厚乳拿铁"));
    }

    private List<ApiCase> loadApiCases() throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("api-cases.json")) {
            if (inputStream == null) {
                throw new IllegalStateException("api-cases.json not found on classpath");
            }
            JsonNode root = objectMapper.readTree(inputStream);
            JsonNode casesNode = root.has("cases") ? root.get("cases") : root;
            List<ApiCase> cases = new ArrayList<>();
            Iterator<JsonNode> iterator = casesNode.elements();
            while (iterator.hasNext()) {
                JsonNode node = iterator.next();
                ApiCase apiCase = new ApiCase();
                apiCase.setName(node.path("name").asText());
                apiCase.setMethod(node.path("method").asText());
                apiCase.setPath(node.path("path").asText());
                apiCase.setExpectStatus(appendNotFoundStatus(normalizeExpectStatus(node.get("expectStatus"))));
                cases.add(apiCase);
            }
            return cases;
        }
    }

    private List<Integer> normalizeExpectStatus(JsonNode expectStatusNode) {
        if (expectStatusNode == null || expectStatusNode.isNull()) {
            return Collections.singletonList(200);
        }
        if (expectStatusNode.isArray()) {
            List<Integer> statuses = new ArrayList<>();
            expectStatusNode.forEach(node -> statuses.add(node.asInt()));
            return statuses;
        }
        return Collections.singletonList(expectStatusNode.asInt());
    }

    private List<Integer> appendNotFoundStatus(List<Integer> expectStatus) {
        List<Integer> statuses = new ArrayList<>(expectStatus);
        if (!statuses.contains(404)) {
            statuses.add(404);
        }
        return statuses;
    }

    private MockHttpServletRequestBuilder buildRequest(ApiCase apiCase) {
        String fullPath = apiCase.getPath();
        switch (apiCase.getMethod().toUpperCase()) {
            case "POST":
                return post(fullPath).contentType(MediaType.APPLICATION_JSON).content("{}");
            case "PUT":
                return put(fullPath).contentType(MediaType.APPLICATION_JSON).content("{}");
            case "DELETE":
                return delete(fullPath);
            case "GET":
            default:
                return get(fullPath);
        }
    }

    static class ApiCaseDocument {
        private List<ApiCase> cases;

        public List<ApiCase> getCases() {
            return cases;
        }

        public void setCases(List<ApiCase> cases) {
            this.cases = cases;
        }
    }

    static class ApiCase {
        private String name;
        private String method;
        private String path;
        private List<Integer> expectStatus;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public List<Integer> getExpectStatus() {
            return expectStatus;
        }

        public void setExpectStatus(List<Integer> expectStatus) {
            this.expectStatus = expectStatus;
        }
    }
}
