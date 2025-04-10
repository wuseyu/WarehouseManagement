package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Role;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.dto.LoginRequest;
import com.example.warehousemanagement.dto.JwtResponse;
import com.example.warehousemanagement.repository.RoleRepository;
import com.example.warehousemanagement.repository.UserRepository;
import com.example.warehousemanagement.security.CustomSecurityExpression;
import com.example.warehousemanagement.security.JwtAuthenticationFilter;
import com.example.warehousemanagement.security.JwtUtils;
import com.example.warehousemanagement.security.UserDetailsEntity;
import com.example.warehousemanagement.security.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 测试AuthController类
 */
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtUtils jwtUtils;
    
    @Mock
    private PasswordEncoder encoder;
    
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    
    @Mock
    private CustomSecurityExpression customSecurityExpression;
    
    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    private AuthController authController;
    
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // 手动注入依赖
        authController = new AuthController(
            authenticationManager,
            userRepository,
            roleRepository,
            encoder,
            jwtUtils
        );
    }

    @Test
    @WithMockUser
    public void testLoginSuccess() throws Exception {
        // 准备数据
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        // 模拟身份验证
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        UserDetailsEntity userDetails = org.mockito.Mockito.mock(UserDetailsEntity.class);

        // 设置模拟行为
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userDetails.getUserId()).thenReturn(1L);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("fake-jwt-token");

        // 直接调用控制器方法
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);
        
        // 验证响应状态和内容
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof JwtResponse);
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals("fake-jwt-token", jwtResponse.getToken());
        assertEquals("testuser", jwtResponse.getUsername());
    }

    @Test
    @WithMockUser
    public void testRegisterSuccess() throws Exception {
        // 准备数据
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("password");
        user.setEmail("newuser@example.com");

        // 模拟用户不存在
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        
        // 模拟角色存在
        Role role = new Role();
        role.setType(Role.RoleType.STORE);
        when(roleRepository.findByType(Role.RoleType.STORE)).thenReturn(Optional.of(role));
        
        // 模拟密码编码器
        when(encoder.encode(any(String.class))).thenReturn("encoded_password");
        
        // 模拟保存用户
        when(userRepository.save(any(User.class))).thenReturn(user);

        // 直接调用控制器方法
        ResponseEntity<?> response = authController.registerUser(user);
        
        // 验证响应状态和内容
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully!", response.getBody());
    }

    @Test
    @WithMockUser
    public void testRegisterUsernameTaken() throws Exception {
        // 准备数据
        User user = new User();
        user.setUsername("existinguser");
        user.setPassword("password");
        user.setEmail("newuser@example.com");

        // 模拟用户名已存在
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // 直接调用控制器方法
        ResponseEntity<?> response = authController.registerUser(user);
        
        // 验证响应状态和内容
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Error: Username is already taken!", response.getBody());
    }
} 