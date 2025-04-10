package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.dto.JwtResponse;
import com.example.warehousemanagement.dto.LoginRequest;
import com.example.warehousemanagement.entity.Role;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.repository.RoleRepository;
import com.example.warehousemanagement.repository.UserRepository;
import com.example.warehousemanagement.security.JwtUtils;
import com.example.warehousemanagement.security.UserDetailsEntity;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost"}, 
             allowedHeaders = "*", 
             exposedHeaders = {"Authorization", "Content-Type"},
             allowCredentials = "true",
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @PostMapping({"/login", "/signin"})
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("收到登录请求: {}", loginRequest.getUsername());
        
        try {
            // 验证用户是否存在
            if (!userRepository.existsByUsername(loginRequest.getUsername())) {
                logger.warn("登录失败: 用户 {} 不存在", loginRequest.getUsername());
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "用户名不存在",
                    "timestamp", new Date()
                ));
            }
            
            // 执行认证过程
            Authentication authentication = null;
            try {
                authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));
                
                logger.info("认证成功: {}", loginRequest.getUsername());
            } catch (DisabledException e) {
                logger.error("用户账号已禁用: {}", loginRequest.getUsername(), e);
                return ResponseEntity.status(403).body(Map.of(
                    "message", "用户账号已禁用",
                    "timestamp", new Date()
                ));
            } catch (BadCredentialsException e) {
                logger.error("用户密码不正确: {}", loginRequest.getUsername(), e);
                return ResponseEntity.status(401).body(Map.of(
                    "message", "密码不正确",
                    "timestamp", new Date()
                ));
            } catch (Exception e) {
                logger.error("认证过程中发生错误: {}", loginRequest.getUsername(), e);
                return ResponseEntity.status(500).body(Map.of(
                    "message", "认证过程中发生错误: " + e.getMessage(),
                    "timestamp", new Date()
                ));
            }

            // 设置安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 生成JWT令牌
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            // 获取用户详情和角色
            UserDetailsEntity userDetails = (UserDetailsEntity) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());
            
            logger.info("用户 {} 登录成功, 角色: {}", loginRequest.getUsername(), roles);

            // 返回JWT和用户信息
            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getUserId(),
                    userDetails.getUsername(),
                    roles));
        } catch (Exception e) {
            logger.error("登录过程中发生未知错误", e);
            return ResponseEntity.status(500).body(Map.of(
                "message", "系统错误: " + e.getMessage(),
                "timestamp", new Date()
            ));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User signUpRequest) {
        // 检查用户名是否已经存在
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        // 检查电子邮件是否已经存在
        if (signUpRequest.getEmail() != null && userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        // 创建新用户对象
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setEmail(signUpRequest.getEmail());
        user.setPhone(signUpRequest.getPhone());
        
        // 设置创建时间和更新时间
        Timestamp now = new Timestamp(System.currentTimeMillis());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // 默认分配STORE角色
        Optional<Role> storeRole = roleRepository.findByType(Role.RoleType.STORE);
        if (storeRole.isPresent()) {
            user.getRoles().add(storeRole.get());
        } else {
            // 如果没有找到STORE角色，则返回错误
            return ResponseEntity
                    .badRequest()
                    .body("Error: Default role not found.");
        }

        // 保存用户
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    // 添加一个测试接口来检查用户是否存在
    @GetMapping("/check-user/{username}")
    public ResponseEntity<?> checkUserExists(@PathVariable String username) {
        logger.info("检查用户是否存在: {}", username);
        boolean exists = userRepository.existsByUsername(username);
        logger.info("用户 {} {}", username, exists ? "存在" : "不存在");
        
        if (exists) {
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                User userEntity = user.get();
                List<String> roleNames = userEntity.getRoles().stream()
                    .map(role -> role.getType().name())
                    .collect(Collectors.toList());
                
                Map<String, Object> response = new HashMap<>();
                response.put("exists", true);
                response.put("username", username);
                response.put("roles", roleNames);
                response.put("email", userEntity.getEmail());
                response.put("createdAt", userEntity.getCreatedAt());
                // 不返回密码等敏感信息
                
                return ResponseEntity.ok(response);
            }
        }
        
        return ResponseEntity.ok(Map.of(
            "exists", exists,
            "username", username
        ));
    }
    
    // 添加一个初始化默认用户的接口
    @PostMapping("/init-default-users")
    public ResponseEntity<?> initDefaultUsers() {
        logger.info("初始化默认用户");
        
        // 检查超级管理员是否已存在
        if (userRepository.existsByUsername("admin")) {
            logger.info("管理员用户已存在，跳过创建");
        } else {
            // 创建超级管理员
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(encoder.encode("admin123"));
            adminUser.setEmail("admin@example.com");
            adminUser.setPhone("12345678901");
            
            // 设置时间戳
            Timestamp now = new Timestamp(System.currentTimeMillis());
            adminUser.setCreatedAt(now);
            adminUser.setUpdatedAt(now);
            
            // 查找SUPER_ADMIN角色
            Optional<Role> adminRole = roleRepository.findByType(Role.RoleType.SUPER_ADMIN);
            if (adminRole.isPresent()) {
                adminUser.getRoles().add(adminRole.get());
                userRepository.save(adminUser);
                logger.info("成功创建管理员用户");
            } else {
                logger.error("找不到SUPER_ADMIN角色，无法创建管理员用户");
                return ResponseEntity.status(500).body("找不到SUPER_ADMIN角色");
            }
        }
        
        // 检查其他默认用户是否存在
        List<Map<String, Object>> createdUsers = new ArrayList<>();
        Map<String, Role.RoleType> defaultUsers = Map.of(
            "operator", Role.RoleType.CITY_OPERATOR,
            "agent", Role.RoleType.AGENT,
            "supplier", Role.RoleType.SUPPLIER,
            "store", Role.RoleType.STORE
        );
        
        for (Map.Entry<String, Role.RoleType> entry : defaultUsers.entrySet()) {
            String username = entry.getKey();
            Role.RoleType roleType = entry.getValue();
            
            if (userRepository.existsByUsername(username)) {
                logger.info("用户 {} 已存在，跳过创建", username);
                continue;
            }
            
            // 创建用户
            User user = new User();
            user.setUsername(username);
            user.setPassword(encoder.encode(username + "123")); // 如：operator123
            user.setEmail(username + "@example.com");
            
            // 设置时间戳
            Timestamp now = new Timestamp(System.currentTimeMillis());
            user.setCreatedAt(now);
            user.setUpdatedAt(now);
            
            // 查找对应角色
            Optional<Role> role = roleRepository.findByType(roleType);
            if (role.isPresent()) {
                user.getRoles().add(role.get());
                userRepository.save(user);
                logger.info("成功创建用户 {}", username);
                
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("username", username);
                userInfo.put("role", roleType.name());
                createdUsers.add(userInfo);
            } else {
                logger.error("找不到角色 {}，无法创建用户 {}", roleType, username);
            }
        }
        
        return ResponseEntity.ok(Map.of(
            "message", "已初始化默认用户",
            "createdUsers", createdUsers
        ));
    }

    // 添加一个公开的测试API端点
    @GetMapping("/test")
    public ResponseEntity<?> testApi() {
        logger.info("收到测试API请求");
        return ResponseEntity.ok(Map.of(
            "message", "API测试成功",
            "timestamp", new Date(),
            "status", "ok"
        ));
    }
} 