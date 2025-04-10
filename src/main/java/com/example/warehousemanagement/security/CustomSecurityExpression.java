package com.example.warehousemanagement.security;

import com.example.warehousemanagement.entity.Permission;
import com.example.warehousemanagement.entity.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("customSecurityExpression")
public class CustomSecurityExpression {

    public boolean hasPermission(String permissionCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            for (org.springframework.security.core.GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().startsWith("ROLE_")) {
                    String roleName = authority.getAuthority().substring(5);
                    try {
                        Role.RoleType roleType = Role.RoleType.valueOf(roleName);
                        if (roleType.hasPermission(permissionCode)) {
                            return true;
                        }
                    } catch (IllegalArgumentException e) {
                        // 忽略无效的角色名称
                    }
                }
            }
        }
        return false;
    }

    public boolean hasPermission(Permission.ActionType action, Permission.ResourceType resource) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            for (org.springframework.security.core.GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().startsWith("ROLE_")) {
                    String roleName = authority.getAuthority().substring(5);
                    try {
                        Role.RoleType roleType = Role.RoleType.valueOf(roleName);
                        if (roleType.hasPermission(action, resource)) {
                            return true;
                        }
                    } catch (IllegalArgumentException e) {
                        // 忽略无效的角色名称
                    }
                }
            }
        }
        return false;
    }
}