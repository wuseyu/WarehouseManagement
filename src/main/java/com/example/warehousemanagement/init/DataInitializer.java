package com.example.warehousemanagement.init;

import com.example.warehousemanagement.entity.Role;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.repository.RoleRepository;
import com.example.warehousemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;

/**
 * 系统初始化数据创建器
 * 用于创建初始的角色和测试用户
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    private DataSource dataSource;

    @Override
    @Transactional
    public void run(String... args) {
        // 初始化角色
        initRoles();
        
        // 初始化默认用户
        initUsers();
        
        log.info("数据初始化完成");
    }
    
    private void initRoles() {
        if (roleRepository.count() == 0) {
            log.info("开始初始化角色...");
            
            // 创建超级管理员角色
            Role superAdminRole = new Role();
            superAdminRole.setType(Role.RoleType.SUPER_ADMIN);
            superAdminRole.setName("超级管理员");
            superAdminRole.setResponsibility("系统最高权限");
            
            // 创建城市运营商角色
            Role cityOperatorRole = new Role();
            cityOperatorRole.setType(Role.RoleType.CITY_OPERATOR);
            cityOperatorRole.setName("城市运营商");
            cityOperatorRole.setResponsibility("管理区域仓库");
            
            // 创建代理商角色
            Role agentRole = new Role();
            agentRole.setType(Role.RoleType.AGENT);
            agentRole.setName("代理商");
            agentRole.setResponsibility("负责商品调拨");
            
            // 创建供应商角色
            Role supplierRole = new Role();
            supplierRole.setType(Role.RoleType.SUPPLIER);
            supplierRole.setName("供应商");
            supplierRole.setResponsibility("商品供应");
            
            // 创建门店角色
            Role storeRole = new Role();
            storeRole.setType(Role.RoleType.STORE);
            storeRole.setName("门店");
            storeRole.setResponsibility("终端销售");
            
            roleRepository.saveAll(Arrays.asList(
                superAdminRole, 
                cityOperatorRole, 
                agentRole, 
                supplierRole, 
                storeRole
            ));
            
            log.info("角色初始化完成");
        }
    }
    
    private void initUsers() {
        if (userRepository.count() == 0) {
            log.info("开始初始化用户...");
            
            // 获取所有角色
            Role superAdminRole = roleRepository.findByType(Role.RoleType.SUPER_ADMIN)
                    .orElseThrow(() -> new RuntimeException("未找到超级管理员角色"));
                    
            Role cityOperatorRole = roleRepository.findByType(Role.RoleType.CITY_OPERATOR)
                    .orElseThrow(() -> new RuntimeException("未找到城市运营商角色"));
                    
            Role agentRole = roleRepository.findByType(Role.RoleType.AGENT)
                    .orElseThrow(() -> new RuntimeException("未找到代理商角色"));
                    
            Role supplierRole = roleRepository.findByType(Role.RoleType.SUPPLIER)
                    .orElseThrow(() -> new RuntimeException("未找到供应商角色"));
                    
            Role storeRole = roleRepository.findByType(Role.RoleType.STORE)
                    .orElseThrow(() -> new RuntimeException("未找到门店角色"));
            
            Timestamp now = Timestamp.from(Instant.now());
            
            // 使用原生SQL执行插入，避开JPA/Hibernate的自动生成
            try {
                // 创建测试用户
                // 1. 超级管理员
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                adminUser.setEmail("admin@example.com");
                adminUser.setCreatedAt(now);
                adminUser.setUpdatedAt(now);
                userRepository.save(adminUser);
                
                // 2. 城市运营商
                User operatorUser = new User();
                operatorUser.setUsername("operator");
                operatorUser.setPassword(passwordEncoder.encode("oper123"));
                operatorUser.setEmail("operator@example.com");
                operatorUser.setCreatedAt(now);
                operatorUser.setUpdatedAt(now);
                userRepository.save(operatorUser);
                
                // 3. 代理商
                User agentUser = new User();
                agentUser.setUsername("agent");
                agentUser.setPassword(passwordEncoder.encode("agent123"));
                agentUser.setEmail("agent@example.com");
                agentUser.setCreatedAt(now);
                agentUser.setUpdatedAt(now);
                userRepository.save(agentUser);
                
                // 4. 供应商
                User supplierUser = new User();
                supplierUser.setUsername("supplier");
                supplierUser.setPassword(passwordEncoder.encode("supp123"));
                supplierUser.setEmail("supplier@example.com");
                supplierUser.setCreatedAt(now);
                supplierUser.setUpdatedAt(now);
                userRepository.save(supplierUser);
                
                // 5. 门店
                User storeUser = new User();
                storeUser.setUsername("store");
                storeUser.setPassword(passwordEncoder.encode("store123"));
                storeUser.setEmail("store@example.com");
                storeUser.setCreatedAt(now);
                storeUser.setUpdatedAt(now);
                userRepository.save(storeUser);
                
                // 使用JdbcTemplate直接执行SQL插入角色关联
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                
                // 使用JDBC直接插入user_role记录
                jdbcTemplate.update(
                    "INSERT INTO user_role (user_id, role_id, assigned_by) VALUES (?, ?, ?)",
                    adminUser.getId(), superAdminRole.getId(), "SYSTEM_INIT"
                );
                
                jdbcTemplate.update(
                    "INSERT INTO user_role (user_id, role_id, assigned_by) VALUES (?, ?, ?)",
                    operatorUser.getId(), cityOperatorRole.getId(), "SYSTEM_INIT"
                );
                
                jdbcTemplate.update(
                    "INSERT INTO user_role (user_id, role_id, assigned_by) VALUES (?, ?, ?)",
                    agentUser.getId(), agentRole.getId(), "SYSTEM_INIT"
                );
                
                jdbcTemplate.update(
                    "INSERT INTO user_role (user_id, role_id, assigned_by) VALUES (?, ?, ?)",
                    supplierUser.getId(), supplierRole.getId(), "SYSTEM_INIT"
                );
                
                jdbcTemplate.update(
                    "INSERT INTO user_role (user_id, role_id, assigned_by) VALUES (?, ?, ?)",
                    storeUser.getId(), storeRole.getId(), "SYSTEM_INIT"
                );
                
                log.info("用户初始化完成");
            } catch (Exception e) {
                log.error("用户初始化失败", e);
                throw e;
            }
        }
    }
} 