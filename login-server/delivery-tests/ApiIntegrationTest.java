package com.sqx.delivery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sqx.SqxApplication;
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
 * 登录服务接口集成测试
 */
@SpringBootTest(classes = SqxApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;

    @BeforeEach
    void loginAdmin() throws Exception {
        String body = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        MvcResult result = mockMvc.perform(post("/admin/account/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        adminToken = json.path("data").path("token").asText();
        assertNotNull(adminToken);
    }

    @Test
    void adminAccountList() throws Exception {
        mockMvc.perform(get("/admin/account/list")
                        .header("token", adminToken)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void adminAccountDetail() throws Exception {
        mockMvc.perform(get("/admin/account/detail/1")
                        .header("token", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    void appAccountLoginAndInfo() throws Exception {
        String loginBody = "{\"username\":\"user01\",\"password\":\"password\"}";
        MvcResult loginResult = mockMvc.perform(post("/app/account/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();

        String appToken = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .path("data").path("token").asText();

        mockMvc.perform(get("/app/account/info")
                        .header("token", appToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.username").value("user01"));
    }

    @Test
    void adminAccountCrudFlow() throws Exception {
        String saveBody = "{\"username\":\"test_crud\",\"password\":\"123456\"}";
        mockMvc.perform(post("/admin/account/save")
                        .header("token", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        MvcResult listResult = mockMvc.perform(get("/admin/account/list")
                        .header("token", adminToken)
                        .param("username", "test_crud"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode listJson = objectMapper.readTree(listResult.getResponse().getContentAsString());
        long accountId = listJson.path("data").path("list").get(0).path("id").asLong();

        String updateBody = "{\"id\":" + accountId + ",\"status\":0}";
        mockMvc.perform(put("/admin/account/update")
                        .header("token", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(delete("/admin/account/delete/" + accountId)
                        .header("token", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void appLoginWrongPassword() throws Exception {
        String body = "{\"username\":\"user01\",\"password\":\"wrong\"}";
        mockMvc.perform(post("/app/account/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("账号或密码错误"));
    }
}
