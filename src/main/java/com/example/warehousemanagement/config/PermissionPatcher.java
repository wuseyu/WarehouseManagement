package com.example.warehousemanagement.config;

import com.example.warehousemanagement.entity.Permission;
import com.example.warehousemanagement.entity.Role;
import com.example.warehousemanagement.entity.RolePermission;
import com.example.warehousemanagement.repository.PermissionRepository;
import com.example.warehousemanagement.repository.RolePermissionRepository;
import com.example.warehousemanagement.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 权限修补程序 - 即使系统已经初始化也会执行
 * 用于确保管理员账号拥有所有产品相关权限
 */
@Component
@Order(2) // 在DataInitializer之后执行
public class PermissionPatcher implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PermissionPatcher.class);

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("执行权限修补，确保管理员拥有产品管理权限...");
        
        // 查找超级管理员角色
        roleRepository.findByName("ROLE_SUPER_ADMIN").ifPresent(adminRole -> {
            
            // 确保产品相关权限存在
            ensurePermissionExists("PRODUCT_CREATE", Permission.ResourceType.PRODUCT, Permission.ActionType.CREATE, "创建产品");
            ensurePermissionExists("PRODUCT_READ", Permission.ResourceType.PRODUCT, Permission.ActionType.READ, "查看产品");
            ensurePermissionExists("PRODUCT_UPDATE", Permission.ResourceType.PRODUCT, Permission.ActionType.UPDATE, "更新产品");
            ensurePermissionExists("PRODUCT_DELETE", Permission.ResourceType.PRODUCT, Permission.ActionType.DELETE, "删除产品");
            ensurePermissionExists("PRODUCT_VIEW", Permission.ResourceType.PRODUCT, Permission.ActionType.READ, "查看产品列表");
            
            // 确保订单相关权限存在
            ensurePermissionExists("ORDER_CREATE", Permission.ResourceType.ORDER, Permission.ActionType.CREATE, "创建订单");
            ensurePermissionExists("ORDER_VIEW", Permission.ResourceType.ORDER, Permission.ActionType.READ, "查看订单");
            ensurePermissionExists("ORDER_UPDATE", Permission.ResourceType.ORDER, Permission.ActionType.UPDATE, "更新订单");
            ensurePermissionExists("ORDER_DELETE", Permission.ResourceType.ORDER, Permission.ActionType.DELETE, "删除订单");
            ensurePermissionExists("ORDER_CONFIRM", Permission.ResourceType.ORDER, Permission.ActionType.UPDATE, "确认订单");
            ensurePermissionExists("ORDER_SHIP", Permission.ResourceType.ORDER, Permission.ActionType.UPDATE, "发货订单");
            ensurePermissionExists("ORDER_COMPLETE", Permission.ResourceType.ORDER, Permission.ActionType.UPDATE, "完成订单");
            
            // 确保订单项相关权限存在
            ensurePermissionExists("ORDER_ITEM_CREATE", Permission.ResourceType.ORDER_ITEM, Permission.ActionType.CREATE, "创建订单项");
            ensurePermissionExists("ORDER_ITEM_VIEW", Permission.ResourceType.ORDER_ITEM, Permission.ActionType.READ, "查看订单项");
            ensurePermissionExists("ORDER_ITEM_UPDATE", Permission.ResourceType.ORDER_ITEM, Permission.ActionType.UPDATE, "更新订单项");
            ensurePermissionExists("ORDER_ITEM_DELETE", Permission.ResourceType.ORDER_ITEM, Permission.ActionType.DELETE, "删除订单项");
            
            // 检查并添加产品类型的权限
            checkAndAddPermission(Permission.ResourceType.PRODUCT, "READ", permissionRepository, adminRole);
            checkAndAddPermission(Permission.ResourceType.PRODUCT, "CREATE", permissionRepository, adminRole);
            checkAndAddPermission(Permission.ResourceType.PRODUCT, "UPDATE", permissionRepository, adminRole);
            checkAndAddPermission(Permission.ResourceType.PRODUCT, "DELETE", permissionRepository, adminRole);
            
            // 检查并添加订单类型的权限
            checkAndAddPermission(Permission.ResourceType.ORDER, "READ", permissionRepository, adminRole);
            checkAndAddPermission(Permission.ResourceType.ORDER, "CREATE", permissionRepository, adminRole);
            checkAndAddPermission(Permission.ResourceType.ORDER, "UPDATE", permissionRepository, adminRole);
            checkAndAddPermission(Permission.ResourceType.ORDER, "DELETE", permissionRepository, adminRole);
            
            // 检查并添加订单项类型的权限
            checkAndAddPermission(Permission.ResourceType.ORDER_ITEM, "READ", permissionRepository, adminRole);
            checkAndAddPermission(Permission.ResourceType.ORDER_ITEM, "CREATE", permissionRepository, adminRole);
            checkAndAddPermission(Permission.ResourceType.ORDER_ITEM, "UPDATE", permissionRepository, adminRole);
            checkAndAddPermission(Permission.ResourceType.ORDER_ITEM, "DELETE", permissionRepository, adminRole);
            
            // 检查并添加车辆类型的权限
            checkAndAddPermission(Permission.ResourceType.VEHICLE, "READ", permissionRepository, adminRole);
            checkAndAddPermission(Permission.ResourceType.VEHICLE, "CREATE", permissionRepository, adminRole);
            checkAndAddPermission(Permission.ResourceType.VEHICLE, "UPDATE", permissionRepository, adminRole);
            checkAndAddPermission(Permission.ResourceType.VEHICLE, "DELETE", permissionRepository, adminRole);
            
            // 为管理员角色分配产品权限
            assignPermissionIfNotExists(adminRole, "PRODUCT_CREATE");
            assignPermissionIfNotExists(adminRole, "PRODUCT_READ");
            assignPermissionIfNotExists(adminRole, "PRODUCT_UPDATE");
            assignPermissionIfNotExists(adminRole, "PRODUCT_DELETE");
            assignPermissionIfNotExists(adminRole, "PRODUCT_VIEW");
            
            // 为管理员角色分配订单权限
            assignPermissionIfNotExists(adminRole, "ORDER_CREATE");
            assignPermissionIfNotExists(adminRole, "ORDER_VIEW");
            assignPermissionIfNotExists(adminRole, "ORDER_UPDATE");
            assignPermissionIfNotExists(adminRole, "ORDER_DELETE");
            assignPermissionIfNotExists(adminRole, "ORDER_CONFIRM");
            assignPermissionIfNotExists(adminRole, "ORDER_SHIP");
            assignPermissionIfNotExists(adminRole, "ORDER_COMPLETE");
            
            // 为管理员角色分配订单项权限
            assignPermissionIfNotExists(adminRole, "ORDER_ITEM_CREATE");
            assignPermissionIfNotExists(adminRole, "ORDER_ITEM_VIEW");
            assignPermissionIfNotExists(adminRole, "ORDER_ITEM_UPDATE");
            assignPermissionIfNotExists(adminRole, "ORDER_ITEM_DELETE");
            
            logger.info("权限修补完成");
        });
    }
    
    private void ensurePermissionExists(String code, Permission.ResourceType resourceType, 
                                       Permission.ActionType actionType, String description) {
        if (!permissionRepository.existsByCode(code)) {
            logger.info("创建权限: {}", code);
            Permission permission = new Permission();
            permission.setCode(code);
            permission.setResourceType(resourceType);
            permission.setAction(actionType);
            permission.setDescription(description);
            permissionRepository.save(permission);
        }
    }
    
    private void assignPermissionIfNotExists(Role role, String permissionCode) {
        Permission permission = permissionRepository.findByCode(permissionCode).orElse(null);
        if (permission != null) {
            if (!rolePermissionRepository.existsByRoleAndPermission(role, permission)) {
                logger.info("为角色 {} 分配权限: {}", role.getName(), permissionCode);
                RolePermission rolePermission = new RolePermission(role, permission);
                rolePermissionRepository.save(rolePermission);
            }
        } else {
            logger.warn("权限不存在: {}", permissionCode);
        }
    }

    private void checkAndAddPermission(Permission.ResourceType resourceType, String action, PermissionRepository permissionRepository, Role role) {
        String permissionCode = resourceType.name() + "_" + action;
        if (!permissionRepository.existsByCode(permissionCode)) {
            logger.info("创建权限: {}", permissionCode);
            Permission permission = new Permission();
            permission.setCode(permissionCode);
            permission.setResourceType(resourceType);
            permission.setAction(Permission.ActionType.valueOf(action.toUpperCase()));
            permission.setDescription(resourceType.name() + " " + action);
            permissionRepository.save(permission);
        }
    }
} 