package com.example.warehousemanagement.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 角色工具类，用于角色名称和显示名称之间的转换
 */
public class RoleUtils {
    
    private static final Map<String, String> ROLE_DISPLAY_NAMES = new HashMap<>();
    private static final Map<String, String> ROLE_DESCRIPTIONS = new HashMap<>();
    
    static {
        // 初始化角色显示名称映射
        ROLE_DISPLAY_NAMES.put("ROLE_SUPER_ADMIN", "超级管理员");
        ROLE_DISPLAY_NAMES.put("ROLE_ADMIN", "管理员");
        ROLE_DISPLAY_NAMES.put("ROLE_CITY_OPERATOR", "城市运营商");
        ROLE_DISPLAY_NAMES.put("ROLE_AGENT", "代理商");
        ROLE_DISPLAY_NAMES.put("ROLE_SUPPLIER", "供应商");
        ROLE_DISPLAY_NAMES.put("ROLE_STORE", "门店");
        
        // 初始化角色描述映射
        ROLE_DESCRIPTIONS.put("ROLE_SUPER_ADMIN", "系统最高权限");
        ROLE_DESCRIPTIONS.put("ROLE_ADMIN", "系统管理员");
        ROLE_DESCRIPTIONS.put("ROLE_CITY_OPERATOR", "管理区域仓库");
        ROLE_DESCRIPTIONS.put("ROLE_AGENT", "负责商品调拨");
        ROLE_DESCRIPTIONS.put("ROLE_SUPPLIER", "商品供应");
        ROLE_DESCRIPTIONS.put("ROLE_STORE", "终端销售");
    }
    
    /**
     * 获取角色的显示名称
     * @param roleName 角色名称（如ROLE_SUPER_ADMIN）
     * @return 显示名称（如"超级管理员"）
     */
    public static String getDisplayName(String roleName) {
        return ROLE_DISPLAY_NAMES.getOrDefault(roleName, roleName);
    }
    
    /**
     * 获取角色的描述
     * @param roleName 角色名称（如ROLE_SUPER_ADMIN）
     * @return 角色描述
     */
    public static String getDescription(String roleName) {
        return ROLE_DESCRIPTIONS.getOrDefault(roleName, "");
    }
    
    /**
     * 获取标准角色名称（确保以ROLE_前缀开头）
     * @param name 可能的角色名称
     * @return 标准角色名称
     */
    public static String getStandardRoleName(String name) {
        if (name == null) {
            return null;
        }
        
        name = name.toUpperCase();
        if (!name.startsWith("ROLE_")) {
            name = "ROLE_" + name;
        }
        
        return name;
    }
} 