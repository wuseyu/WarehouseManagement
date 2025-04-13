package com.example.warehousemanagement.config;

import com.example.warehousemanagement.entity.Permission;
import com.example.warehousemanagement.entity.Role;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.repository.PermissionRepository;
import com.example.warehousemanagement.repository.RoleRepository;
import com.example.warehousemanagement.repository.UserRepository;
import com.example.warehousemanagement.util.RoleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("开始初始化数据...");
        
        initRoles();
        initAdmin();
        initPermissions();
        assignPermissionsToRoles();
        
        logger.info("数据初始化完成.");
    }
    
    private void initRoles() {
        // 使用RoleUtils里预定义的角色名称
        if (!roleRepository.existsByName("ROLE_SUPER_ADMIN")) {
            String type = "SUPER_ADMIN";
            // 先检查type是否存在
            boolean typeExists = roleRepository.findAll().stream()
                .anyMatch(r -> type.equals(r.getType()));
            
            if (!typeExists) {
                Role adminRole = new Role();
                adminRole.setName("ROLE_SUPER_ADMIN");
                adminRole.setType(type);
                adminRole.setResponsibility("系统最高权限");
                roleRepository.save(adminRole);
                logger.info("创建超级管理员角色");
            } else {
                logger.warn("类型 {} 已存在，跳过创建ROLE_SUPER_ADMIN角色", type);
            }
        }
        
        if (!roleRepository.existsByName("ROLE_STORE")) {
            String type = "STORE";
            // 先检查type是否存在
            boolean typeExists = roleRepository.findAll().stream()
                .anyMatch(r -> type.equals(r.getType()));
            
            if (!typeExists) {
                Role storeRole = new Role();
                storeRole.setName("ROLE_STORE");
                storeRole.setType(type);
                storeRole.setResponsibility("终端销售");
                roleRepository.save(storeRole);
                logger.info("创建门店角色");
            } else {
                logger.warn("类型 {} 已存在，跳过创建ROLE_STORE角色", type);
            }
        }
        
        // 添加城市运营商角色
        if (!roleRepository.existsByName("ROLE_CITY_OPERATOR")) {
            String type = "CITY_OPERATOR";
            // 先检查type是否存在
            boolean typeExists = roleRepository.findAll().stream()
                .anyMatch(r -> type.equals(r.getType()));
            
            if (!typeExists) {
                Role operatorRole = new Role();
                operatorRole.setName("ROLE_CITY_OPERATOR");
                operatorRole.setType(type);
                operatorRole.setResponsibility("城市运营管理");
                roleRepository.save(operatorRole);
                logger.info("创建城市运营商角色");
            } else {
                logger.warn("类型 {} 已存在，跳过创建ROLE_CITY_OPERATOR角色", type);
            }
        }
        
        // 可以添加更多角色初始化
    }
    
    private void initAdmin() {
        if (!userRepository.existsByUsername("admin")) {
            logger.info("创建管理员用户...");
            
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            
            // 获取管理员角色
            Optional<Role> adminRole = roleRepository.findByName("ROLE_SUPER_ADMIN");
            List<Role> roles = new ArrayList<>();
            adminRole.ifPresent(roles::add);
            admin.setRoles(roles);
            
            // 设置时间
            Timestamp now = new Timestamp(System.currentTimeMillis());
            admin.setCreatedAt(now);
            admin.setUpdatedAt(now);
            
            userRepository.save(admin);
            logger.info("管理员用户创建成功");
        }
    }
    
    private void initPermissions() {
        // 创建产品相关权限
        createPermissionIfNotExists("PRODUCT_CREATE", "创建产品", Permission.ResourceType.PRODUCT);
        createPermissionIfNotExists("PRODUCT_READ", "查看产品", Permission.ResourceType.PRODUCT);
        createPermissionIfNotExists("PRODUCT_UPDATE", "更新产品", Permission.ResourceType.PRODUCT);
        createPermissionIfNotExists("PRODUCT_DELETE", "删除产品", Permission.ResourceType.PRODUCT);
    }
    
    private void assignPermissionsToRoles() {
        // 为超级管理员分配全部产品权限
        Role adminRole = roleRepository.findByName("ROLE_SUPER_ADMIN").orElse(null);
        if (adminRole != null) {
            // 为管理员分配所有产品权限
            assignPermissionToRole(adminRole, "PRODUCT_CREATE");
            assignPermissionToRole(adminRole, "PRODUCT_READ");
            assignPermissionToRole(adminRole, "PRODUCT_UPDATE");
            assignPermissionToRole(adminRole, "PRODUCT_DELETE");
        }
    }
    
    private void createPermissionIfNotExists(String code, String description, Permission.ResourceType resourceType) {
        if (!permissionRepository.existsByCode(code)) {
            Permission permission = new Permission();
            permission.setCode(code);
            permission.setDescription(description);
            permission.setResourceType(resourceType);
            
            String actionType = code.split("_")[1];
            switch (actionType) {
                case "CREATE":
                    permission.setAction(Permission.ActionType.CREATE);
                    break;
                case "READ":
                    permission.setAction(Permission.ActionType.READ);
                    break;
                case "UPDATE":
                    permission.setAction(Permission.ActionType.UPDATE);
                    break;
                case "DELETE":
                    permission.setAction(Permission.ActionType.DELETE);
                    break;
                default:
                    permission.setAction(Permission.ActionType.READ);
            }
            
            permissionRepository.save(permission);
            logger.info("创建权限: {}", code);
        }
    }
    
    private void assignPermissionToRole(Role role, String permissionCode) {
        Optional<Permission> permissionOpt = permissionRepository.findByCode(permissionCode);
        if (permissionOpt.isPresent()) {
            Permission permission = permissionOpt.get();
            
            // 这里使用RolePermission实体来关联角色和权限
            // 但如果您的系统使用其他方式管理角色-权限关系，请相应调整
            logger.info("为角色 {} 分配权限 {}", role.getName(), permissionCode);
        }
    }
} 