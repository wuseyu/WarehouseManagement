package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.dto.RoleDTO;
import com.example.warehousemanagement.entity.Role;
import com.example.warehousemanagement.repository.RoleRepository;
import com.example.warehousemanagement.util.RoleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleRepository roleRepository;

    /**
     * 获取所有角色
     */
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        logger.info("【角色控制器】获取所有角色");
        List<Role> roles = roleRepository.findAll();
        
        // 转换为DTO格式
        List<RoleDTO> roleDTOs = roles.stream()
            .map(RoleDTO::fromEntity)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(roleDTOs);
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
        logger.info("【角色控制器】获取角色详情，ID: {}", id);
        
        return roleRepository.findById(id)
            .map(role -> ResponseEntity.ok(RoleDTO.fromEntity(role)))
            .orElseGet(() -> {
                logger.warn("【角色控制器】未找到ID为{}的角色", id);
                return ResponseEntity.notFound().build();
            });
    }
    
    /**
     * 获取角色名称和显示名称的映射
     */
    @GetMapping("/mapping")
    public ResponseEntity<Map<String, String>> getRoleMapping() {
        logger.info("【角色控制器】获取角色名称映射");
        
        // 创建一个映射，包含所有角色的标准名称和显示名称
        Map<String, String> roleMapping = new HashMap<>();
        
        // 添加预定义角色
        roleMapping.put("ROLE_SUPER_ADMIN", RoleUtils.getDisplayName("ROLE_SUPER_ADMIN"));
        roleMapping.put("ROLE_ADMIN", RoleUtils.getDisplayName("ROLE_ADMIN"));
        roleMapping.put("ROLE_CITY_OPERATOR", RoleUtils.getDisplayName("ROLE_CITY_OPERATOR"));
        roleMapping.put("ROLE_AGENT", RoleUtils.getDisplayName("ROLE_AGENT"));
        roleMapping.put("ROLE_SUPPLIER", RoleUtils.getDisplayName("ROLE_SUPPLIER"));
        roleMapping.put("ROLE_STORE", RoleUtils.getDisplayName("ROLE_STORE"));
        
        // 添加数据库中的额外角色
        roleRepository.findAll().forEach(role -> {
            roleMapping.putIfAbsent(role.getName(), 
                RoleUtils.getDisplayName(role.getName()));
        });
        
        return ResponseEntity.ok(roleMapping);
    }
} 