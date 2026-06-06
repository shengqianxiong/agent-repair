package com.sqx.delivery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 极简登录功能 API 集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String appToken;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = loginAndGetToken("/admin/login", "admin", "admin123");
        appToken = loginAndGetToken("/app/login", "user01", "admin123");
    }

    @Test
    void adminAccountList_shouldReturnPagedData() throws Exception {
        mockMvc.perform(get("/admin/account/list")
                        .header("token", adminToken)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void adminAccountDetail_shouldReturnAccount() throws Exception {
        mockMvc.perform(get("/admin/account/detail/1")
                        .header("token", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    void appAccountList_shouldReturnCurrentUser() throws Exception {
        mockMvc.perform(get("/app/account/list")
                        .header("token", appToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].username").value("user01"));
    }

    @Test
    void appAccountDetail_shouldReturnCurrentUser() throws Exception {
        mockMvc.perform(get("/app/account/detail")
                        .header("token", appToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.username").value("user01"));
    }

    @Test
    void adminAccountCrudFlow_shouldWork() throws Exception {
        String createBody = "{\"username\":\"testuser\",\"password\":\"123456\"}";
        mockMvc.perform(post("/admin/account/save")
                        .header("token", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        MvcResult listResult = mockMvc.perform(get("/admin/account/list")
                        .header("token", adminToken)
                        .param("keyword", "testuser"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode listNode = objectMapper.readTree(listResult.getResponse().getContentAsString());
        long newAccountId = listNode.path("data").path("list").get(0).path("id").asLong();
        assertNotNull(newAccountId);

        String updateBody = "{\"id\":" + newAccountId + ",\"status\":0}";
        mockMvc.perform(put("/admin/account/update")
                        .header("token", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(delete("/admin/account/delete/" + newAccountId)
                        .header("token", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void appLogin_shouldRejectWrongPassword() throws Exception {
        String body = "{\"username\":\"user01\",\"password\":\"wrong\"}";
        mockMvc.perform(post("/app/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("账号或密码错误"));
    }

    private String loginAndGetToken(String path, String username, String password) throws Exception {
        String body = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        MvcResult result = mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();
        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.path("data").path("token").asText();
    }
}
