package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.dto.CreateUserDTO;
import com.example.warehousemanagement.dto.RoleDTO;
import com.example.warehousemanagement.dto.UpdateUserDTO;
import com.example.warehousemanagement.dto.UserDTO;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.entity.Role;
import com.example.warehousemanagement.repository.UserRepository;
import com.example.warehousemanagement.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 获取所有用户
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        logger.info("【用户控制器】获取所有用户");
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream()
            .map(UserDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }
    
    /**
     * 获取单个用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        logger.info("【用户控制器】获取用户详情，ID: {}", id);
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(UserDTO.fromEntity(user)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserDTO createUserDTO) {
        logger.info("【用户控制器】创建新用户: {}", createUserDTO.getUsername());
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            logger.warn("【用户控制器】用户名已存在: {}", createUserDTO.getUsername());
            return ResponseEntity.badRequest().body("用户名已存在");
        }
        
        // 创建新用户实体
        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        user.setEmail(createUserDTO.getEmail());
        user.setPhone(createUserDTO.getPhone());
        
        // 设置角色（如果提供了）
        if (createUserDTO.getRoleIds() != null && !createUserDTO.getRoleIds().isEmpty()) {
            List<Role> roles = roleRepository.findAllById(createUserDTO.getRoleIds());
            if (roles.isEmpty()) {
                return ResponseEntity.badRequest().body("未找到指定的角色");
            }
            user.setRoles(roles);
        }
        
        // 设置创建时间和更新时间
        Timestamp now = new Timestamp(System.currentTimeMillis());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        
        User savedUser = userRepository.save(user);
        logger.info("【用户控制器】用户创建成功，ID: {}", savedUser.getId());
        return ResponseEntity.ok(UserDTO.fromEntity(savedUser));
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO updateUserDTO) {
        logger.info("【用户控制器】更新用户，ID: {}", id);
        
        return userRepository.findById(id)
                .map(existingUser -> {
                    // 更新基本信息
                    if (updateUserDTO.getUsername() != null && !updateUserDTO.getUsername().equals(existingUser.getUsername())) {
                        // 检查新用户名是否已存在
                        if (userRepository.existsByUsername(updateUserDTO.getUsername())) {
                            logger.warn("【用户控制器】用户名已存在: {}", updateUserDTO.getUsername());
                            return ResponseEntity.badRequest().body("用户名已存在");
                        }
                        existingUser.setUsername(updateUserDTO.getUsername());
                    }
                    
                    if (updateUserDTO.getEmail() != null) {
                        existingUser.setEmail(updateUserDTO.getEmail());
                    }
                    
                    if (updateUserDTO.getPhone() != null) {
                        existingUser.setPhone(updateUserDTO.getPhone());
                    }
                    
                    // 更新密码（如果提供了新密码）
                    if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));
                    }
                    
                    // 更新角色（如果提供了）
                    if (updateUserDTO.getRoleIds() != null) {
                        List<Role> roles = roleRepository.findAllById(updateUserDTO.getRoleIds());
                        if (roles.isEmpty() && !updateUserDTO.getRoleIds().isEmpty()) {
                            return ResponseEntity.badRequest().body("未找到指定的角色");
                        }
                        existingUser.setRoles(roles);
                    }
                    
                    // 更新时间
                    existingUser.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    
                    User updatedUser = userRepository.save(existingUser);
                    logger.info("【用户控制器】用户更新成功，ID: {}", updatedUser.getId());
                    return ResponseEntity.ok(UserDTO.fromEntity(updatedUser));
                })
                .orElseGet(() -> {
                    logger.warn("【用户控制器】未找到要更新的用户，ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("【用户控制器】删除用户，ID: {}", id);
        
        if (!userRepository.existsById(id)) {
            logger.warn("【用户控制器】未找到要删除的用户，ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        
        userRepository.deleteById(id);
        logger.info("【用户控制器】用户删除成功，ID: {}", id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 获取所有角色
     */
    @GetMapping("/roles")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        logger.info("【用户控制器】获取所有角色");
        List<Role> roles = roleRepository.findAll();
        List<RoleDTO> roleDTOs = roles.stream()
            .map(RoleDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(roleDTOs);
    }
    
    /**
     * 为用户分配角色
     */
    @PutMapping("/{id}/roles")
    public ResponseEntity<?> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        logger.info("【用户控制器】为用户分配角色，用户ID: {}, 角色IDs: {}", id, roleIds);
        
        return userRepository.findById(id)
                .map(user -> {
                    // 获取角色
                    List<Role> roles = roleRepository.findAllById(roleIds);
                    if (roles.isEmpty() && !roleIds.isEmpty()) {
                        return ResponseEntity.badRequest().body("未找到指定的角色");
                    }
                    
                    // 设置角色
                    user.setRoles(roles);
                    
                    // 更新时间
                    user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    
                    User updatedUser = userRepository.save(user);
                    logger.info("【用户控制器】用户角色更新成功，ID: {}", updatedUser.getId());
                    return ResponseEntity.ok(UserDTO.fromEntity(updatedUser));
                })
                .orElseGet(() -> {
                    logger.warn("【用户控制器】未找到用户，ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }
}