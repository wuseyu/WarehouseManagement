package com.example.warehousemanagement.security;

import com.example.warehousemanagement.entity.Permission;
import com.example.warehousemanagement.entity.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("customSecurityExpression")
public class CustomSecurityExpression {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomSecurityExpression.class);

    public boolean hasPermission(String permissionCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || authentication.getAuthorities() == null) {
            logger.warn("无法检查权限: 认证信息为空");
            return false;
        }
        
        logger.debug("检查权限: {} 对用户: {}", permissionCode, authentication.getName());
        
        // 记录当前用户的所有权限
        authentication.getAuthorities().forEach(auth -> 
            logger.debug("用户拥有权限: {}", auth.getAuthority()));
        
        for (org.springframework.security.core.GrantedAuthority authority : authentication.getAuthorities()) {
            String authorityName = authority.getAuthority();
            logger.debug("检查权限: {} 对权限: {}", permissionCode, authorityName);
            
            if (authorityName.startsWith("ROLE_")) {
                String roleName = authorityName.substring(5);
                
                // 超级管理员始终拥有所有权限
                if (roleName.equals("SUPER_ADMIN")) {
                    logger.info("超级管理员拥有所有权限: {}", permissionCode);
                    return true;
                }
                
                try {
                    Role.RoleType roleType = Role.RoleType.valueOf(roleName);
                    if (roleType != null) {
                        // 检查角色是否有对应权限
                        if (roleType == Role.RoleType.SUPER_ADMIN || 
                            (roleType.hasPermission(permissionCode))) {
                            return true;
                        }
                    }
                } catch (IllegalArgumentException e) {
                    logger.warn("无效的角色名称: {}", roleName, e);
                }
            }
        }
        
        logger.warn("权限检查失败: {} 对用户: {}", permissionCode, authentication.getName());
        return false;
    }

    public boolean hasPermission(Permission.ActionType action, Permission.ResourceType resource) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || authentication.getAuthorities() == null) {
            logger.warn("无法检查权限: 认证信息为空");
            return false;
        }
        
        logger.debug("检查权限: {}/{} 对用户: {}", action, resource, authentication.getName());
        
        for (org.springframework.security.core.GrantedAuthority authority : authentication.getAuthorities()) {
            String authorityName = authority.getAuthority();
            
            if (authorityName.startsWith("ROLE_")) {
                String roleName = authorityName.substring(5);
                
                // 超级管理员始终拥有所有权限
                if (roleName.equals("SUPER_ADMIN")) {
                    logger.info("超级管理员拥有所有权限: {}/{}", action, resource);
                    return true;
                }
                
                try {
                    Role.RoleType roleType = Role.RoleType.valueOf(roleName);
                    if (roleType != null) {
                        // 检查角色是否有对应权限
                        if (roleType == Role.RoleType.SUPER_ADMIN || 
                            (roleType.hasPermission(action, resource))) {
                            return true;
                        }
                    }
                } catch (IllegalArgumentException e) {
                    logger.warn("无效的角色名称: {}", roleName, e);
                }
            }
        }
        
        logger.warn("权限检查失败: {}/{} 对用户: {}", action, resource, authentication.getName());
        return false;
    }
}