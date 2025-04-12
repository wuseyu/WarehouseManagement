package com.example.warehousemanagement.security;

import com.example.warehousemanagement.entity.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

@Component("customSecurityExpression")
public class CustomSecurityExpression {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomSecurityExpression.class);
    
    // 超级管理员角色常量
    private static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    
    // 各角色拥有的权限映射（硬编码方式，实际项目中可以从配置文件或数据库加载）
    private static final Set<String> SUPER_ADMIN_PERMISSIONS = new HashSet<>();
    
    static {
        // 初始化超级管理员权限集合
        SUPER_ADMIN_PERMISSIONS.add("PRODUCT_CREATE");
        SUPER_ADMIN_PERMISSIONS.add("PRODUCT_READ");
        SUPER_ADMIN_PERMISSIONS.add("PRODUCT_UPDATE");
        SUPER_ADMIN_PERMISSIONS.add("PRODUCT_DELETE");
        SUPER_ADMIN_PERMISSIONS.add("PRODUCT_VIEW");
        
        SUPER_ADMIN_PERMISSIONS.add("USER_CREATE");
        SUPER_ADMIN_PERMISSIONS.add("USER_VIEW");
        SUPER_ADMIN_PERMISSIONS.add("USER_UPDATE");
        SUPER_ADMIN_PERMISSIONS.add("USER_DELETE");
        
        SUPER_ADMIN_PERMISSIONS.add("ORDER_CREATE");
        SUPER_ADMIN_PERMISSIONS.add("ORDER_VIEW");
        SUPER_ADMIN_PERMISSIONS.add("ORDER_UPDATE");
        SUPER_ADMIN_PERMISSIONS.add("ORDER_CONFIRM");
        SUPER_ADMIN_PERMISSIONS.add("ORDER_SHIP");
        SUPER_ADMIN_PERMISSIONS.add("ORDER_COMPLETE");
        SUPER_ADMIN_PERMISSIONS.add("ORDER_ITEM_CREATE");
        
        SUPER_ADMIN_PERMISSIONS.add("INVENTORY_CREATE");
        SUPER_ADMIN_PERMISSIONS.add("INVENTORY_VIEW");
        SUPER_ADMIN_PERMISSIONS.add("INVENTORY_UPDATE");
        
        SUPER_ADMIN_PERMISSIONS.add("VEHICLE_CREATE");
        SUPER_ADMIN_PERMISSIONS.add("VEHICLE_VIEW");
        SUPER_ADMIN_PERMISSIONS.add("VEHICLE_UPDATE");
        SUPER_ADMIN_PERMISSIONS.add("VEHICLE_DELETE");
        
        SUPER_ADMIN_PERMISSIONS.add("SHIPMENT_CREATE");
        SUPER_ADMIN_PERMISSIONS.add("SHIPMENT_VIEW");
        SUPER_ADMIN_PERMISSIONS.add("SHIPMENT_DELETE");
        
        SUPER_ADMIN_PERMISSIONS.add("TASK_CREATE");
        SUPER_ADMIN_PERMISSIONS.add("TASK_VIEW");
        SUPER_ADMIN_PERMISSIONS.add("TASK_DELETE");
    }

    /**
     * 判断当前用户是否拥有指定权限
     */
    public boolean hasPermission(String permissionCode) {
        logger.info("【权限检查】检查权限代码: {}", permissionCode);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            logger.warn("【权限检查】获取不到Authentication对象");
            return false;
        }
        
        // 获取当前用户角色
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            logger.info("【权限检查】用户角色: {}, 请求权限: {}", role, permissionCode);
            
            // 如果是超级管理员角色
            if (ROLE_SUPER_ADMIN.equals(role)) {
                // 超级管理员拥有所有权限
                logger.info("【权限检查】超级管理员角色，授予所有权限");
                return true;
            }
            
            // 其他角色基于权限映射判断
            if (role.equals("ROLE_STORE") && SUPER_ADMIN_PERMISSIONS.contains(permissionCode)) {
                logger.info("【权限检查】门店角色，授予权限: {}", permissionCode);
                return true;
            }
        }
        
        logger.warn("【权限检查】用户无权限: {}", permissionCode);
        return false;
    }

    /**
     * 判断当前用户是否拥有指定的资源和操作权限
     */
    public boolean hasPermission(Permission.ActionType action, Permission.ResourceType resource) {
        logger.info("【权限检查】检查操作权限: {}, 资源类型: {}", action, resource);
        
        // 将资源和操作类型转换为权限码格式
        String permissionCode = resource.name() + "_" + action.name();
        return hasPermission(permissionCode);
    }
}