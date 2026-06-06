package com.sqx.delivery;

import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
                .andExpect(status().isOk());

        mockMvc.perform(get("/app/product/list")
                        .header("token", "uid:1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("已实现 Admin 商品接口 - 未登录可访问")
    void adminProductEndpoints_withoutAuth_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/admin/product/list"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/admin/product/category/list"))
                .andExpect(status().isOk());
    }

    private List<ApiCase> loadApiCases() throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("api-cases.json")) {
            if (inputStream == null) {
                throw new IllegalStateException("api-cases.json not found on classpath");
            }
            return objectMapper.readValue(inputStream, new TypeReference<List<ApiCase>>() {});
        }
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
