package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testCreateUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("JohnDoe");
        user.setPassword("password123");
        user.setEmail("johndoe@example.com");
        user.setPhone("1234567890");
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content("{\"username\":\"JohnDoe\",\"password\":\"password123\",\"email\":\"johndoe@example.com\",\"phone\":\"1234567890\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("JohnDoe"))
                .andExpect(jsonPath("$.email").value("johndoe@example.com"))
                .andExpect(jsonPath("$.phone").value("1234567890"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    public void testGetUserById() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("JohnDoe");
        user.setPassword("password123");
        user.setEmail("johndoe@example.com");
        user.setPhone("1234567890");
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("JohnDoe"))
                .andExpect(jsonPath("$.email").value("johndoe@example.com"))
                .andExpect(jsonPath("$.phone").value("1234567890"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    public void testGetUserByUsername() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("JohnDoe");
        user.setPassword("password123");
        user.setEmail("johndoe@example.com");
        user.setPhone("1234567890");
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        when(userService.getUserByUsername("JohnDoe")).thenReturn(user);

        mockMvc.perform(get("/api/users/username/JohnDoe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("JohnDoe"))
                .andExpect(jsonPath("$.email").value("johndoe@example.com"))
                .andExpect(jsonPath("$.phone").value("1234567890"));

        verify(userService, times(1)).getUserByUsername("JohnDoe");
    }

    @Test
    public void testGetAllUsers() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("JohnDoe");
        user1.setPassword("password123");
        user1.setEmail("johndoe@example.com");
        user1.setPhone("1234567890");
        user1.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user1.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("JaneDoe");
        user2.setPassword("password456");
        user2.setEmail("janedoe@example.com");
        user2.setPhone("0987654321");
        user2.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user2.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("JohnDoe"))
                .andExpect(jsonPath("$[1].username").value("JaneDoe"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    public void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent()); // 修改为期望 204 状态码

        verify(userService, times(1)).deleteUser(1L);
    }
}
