package com.example.warehousemanagement.config;

import com.example.warehousemanagement.entity.Role;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.repository.RoleRepository;
import com.example.warehousemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

/**
 * 测试数据初始化类
 * 在应用启动时添加默认的测试用户和角色
 */
@Component
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 初始化角色
        initRoles();
        
        // 初始化用户
        initUsers();
    }
    
    private void initRoles() {
        // 检查角色是否已存在
        if (roleRepository.count() > 0) {
            System.out.println("角色已存在，跳过初始化");
            return;
        }
        
        // 创建角色
        for (Role.RoleType roleType : Role.RoleType.values()) {
            Role role = new Role();
            role.setType(roleType);
            role.setName(roleType.name());
            
            // 根据不同角色设置责任描述
            switch (roleType) {
                case SUPER_ADMIN:
                    role.setResponsibility("系统最高权限");
                    break;
                case CITY_OPERATOR:
                    role.setResponsibility("管理区域仓库");
                    break;
                case AGENT:
                    role.setResponsibility("负责商品调拨");
                    break;
                case SUPPLIER:
                    role.setResponsibility("商品供应");
                    break;
                case STORE:
                    role.setResponsibility("终端销售");
                    break;
            }
            
            roleRepository.save(role);
            System.out.println("已创建角色: " + roleType.name());
        }
    }
    
    private void initUsers() {
        // 检查是否已有用户
        if (userRepository.count() > 0) {
            System.out.println("用户已存在，跳过初始化");
            return;
        }
        
        // 创建测试用户
        createUser("admin", "admin123", Role.RoleType.SUPER_ADMIN);
        createUser("operator", "oper123", Role.RoleType.CITY_OPERATOR);
        createUser("agent", "agent123", Role.RoleType.AGENT);
        createUser("supplier", "supp123", Role.RoleType.SUPPLIER);
        createUser("store", "store123", Role.RoleType.STORE);
    }
    
    private void createUser(String username, String password, Role.RoleType roleType) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(username + "@example.com");
        
        // 设置创建时间和更新时间
        Timestamp now = Timestamp.from(Instant.now());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        
        // 初始化角色集合
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }
        
        // 添加对应角色
        Optional<Role> role = roleRepository.findByType(roleType);
        if (role.isPresent()) {
            user.getRoles().add(role.get());
        } else {
            System.err.println("角色不存在: " + roleType);
            return;
        }
        
        // 保存用户
        userRepository.save(user);
        System.out.println("已创建用户: " + username + " 角色: " + roleType);
    }
} 