package com.example.warehousemanagement.config;

import com.example.warehousemanagement.entity.Role;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.repository.RoleRepository;
import com.example.warehousemanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class InitialDataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitialDataLoader.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public void run(String... args) {
        logger.info("【初始化】开始初始化基础数据...");
        
        // 创建默认角色
        createRoleIfNotExists("ROLE_SUPER_ADMIN", "超级管理员");
        createRoleIfNotExists("ROLE_ADMIN", "管理员");
        createRoleIfNotExists("ROLE_CITY_OPERATOR", "城市运营商");
        createRoleIfNotExists("ROLE_AGENT", "代理商");
        createRoleIfNotExists("ROLE_SUPPLIER", "供应商");
        createRoleIfNotExists("ROLE_STORE", "门店");
        
        // 创建超级管理员
        createSuperAdminIfNotExists();
        
        logger.info("【初始化】基础数据初始化完成");
    }
    
    /**
     * 创建角色（如果不存在）
     */
    private void createRoleIfNotExists(String name, String responsibility) {
        Optional<Role> existingRole = roleRepository.findByName(name);
        if (existingRole.isEmpty()) {
            String type = name.replace("ROLE_", "");
            
            // 检查是否已存在相同type的角色
            boolean typeExists = roleRepository.findAll().stream()
                .anyMatch(r -> type.equals(r.getType()));
                
            if (!typeExists) {
                Role role = new Role(name);
                role.setType(type);
                role.setResponsibility(responsibility);
                roleRepository.save(role);
                logger.info("【初始化】创建角色: {}", name);
            } else {
                logger.warn("【初始化】类型 {} 已存在，跳过创建角色 {}", type, name);
            }
        }
    }
    
    /**
     * 创建超级管理员（如果不存在）
     */
    private void createSuperAdminIfNotExists() {
        Optional<User> existingAdmin = userRepository.findByUsername("admin");
        if (existingAdmin.isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            
            // 获取超级管理员角色
            Optional<Role> adminRole = roleRepository.findByName("ROLE_SUPER_ADMIN");
            if (adminRole.isPresent()) {
                List<Role> roles = new ArrayList<>();
                roles.add(adminRole.get());
                admin.setRoles(roles);
            }
            
            // 设置创建时间和更新时间
            Timestamp now = new Timestamp(System.currentTimeMillis());
            admin.setCreatedAt(now);
            admin.setUpdatedAt(now);
            
            userRepository.save(admin);
            logger.info("【初始化】创建超级管理员用户");
        }
    }
} 