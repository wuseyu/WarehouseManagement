package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean  // 使用 @MockBean 替代自定义的 TestConfig
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;  // 用于转换对象到 JSON

    // 初始化模拟对象
    @BeforeEach
    void setup() {
        Mockito.reset(userService); // 每次测试前重置 mock

        // 保持原有的 testUser 初始化逻辑
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
    }

    private final User testUser = new User();

    {
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
    }

    @Test
    void createUser_ShouldReturnCreated() throws Exception {
        when(userService.saveUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testUser\",\"password\":\"password123\",\"email\":\"test@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    void findByUsername_ShouldReturnUser() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(testUser);

        mockMvc.perform(get("/api/users/testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void findById_ShouldReturnUser() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/users/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findAllUsers_ShouldReturnUserList() throws Exception {
        when(userService.findAllUsers()).thenReturn(Collections.singletonList(testUser));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testUser"));
    }

    @Test
    void findUsersByRoleName_ShouldReturnFilteredList() throws Exception {
        when(userService.findUsersByRoleName("ADMIN")).thenReturn(Collections.singletonList(testUser));

        mockMvc.perform(get("/api/users/role/ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roles[0].name").doesNotExist()); // 根据实际角色结构调整
    }
}