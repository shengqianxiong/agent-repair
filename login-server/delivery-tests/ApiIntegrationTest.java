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
import org.springframework.test.context.jdbc.Sql;
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
 * 登录服务端 HTTP 接口集成测试
 */
@SpringBootTest(classes = SqxApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;

    @BeforeEach
    void adminLogin() throws Exception {
        MvcResult result = mockMvc.perform(post("/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        adminToken = root.path("data").path("token").asText();
        assertNotNull(adminToken);
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
    void adminAccountDetail_shouldReturnAccountInfo() throws Exception {
        mockMvc.perform(get("/admin/account/detail/1")
                        .header("token", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    void appUserLogin_shouldReturnToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/app/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user01\",\"password\":\"user123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals("user01", root.path("data").path("username").asText());
        assertNotNull(root.path("data").path("token").asText());
    }

    @Test
    void appUserInfo_shouldRequireLoginToken() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/app/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user01\",\"password\":\"user123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String appToken = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .path("data").path("token").asText();

        mockMvc.perform(get("/app/user/info")
                        .header("token", appToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.username").value("user01"));
    }

    @Test
    void adminAccountSaveUpdateDelete_shouldWork() throws Exception {
        mockMvc.perform(post("/admin/account/save")
                        .header("token", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"test123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        MvcResult listResult = mockMvc.perform(get("/admin/account/list")
                        .header("token", adminToken)
                        .param("keyword", "testuser"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode listRoot = objectMapper.readTree(listResult.getResponse().getContentAsString());
        long accountId = listRoot.path("data").path("list").get(0).path("id").asLong();

        mockMvc.perform(put("/admin/account/update")
                        .header("token", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":" + accountId + ",\"status\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(delete("/admin/account/delete/" + accountId)
                        .header("token", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}
