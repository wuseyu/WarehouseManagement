package com.example.warehousemanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "roles")
public class Role {
    private static final Logger logger = LoggerFactory.getLogger(Role.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @Enumerated(EnumType.STRING)
    private RoleType type;

    @Column(nullable = false, length = 100)
    private String name; 

    @Column(columnDefinition = "TEXT") 
    private String responsibility; 

    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RolePermission> rolePermissions = new ArrayList<>();

    public void addPermission(Permission permission) {
        RolePermission rp = new RolePermission(this, permission);
        this.rolePermissions.add(rp);
        permission.getRolePermissions().add(rp);
    }

    public enum RoleType {
        SUPER_ADMIN("超级管理员", "系统最高权限"),
        CITY_OPERATOR("城市运营商", "管理区域仓库"),
        AGENT("代理商", "负责商品调拨"),
        SUPPLIER("供应商", "商品供应"),
        STORE("门店", "终端销售");

        private final String cnName;
        private final String description;
        private static final Logger logger = LoggerFactory.getLogger(RoleType.class);

        RoleType(String post, String Duty) {
            this.cnName = post;
            this.description = Duty;
        }
        
        public String getCnName() {
            return cnName;
        }
        
        public String getDescription() {
            return description;
        }

        public boolean hasPermission(String permissionCode) {
            // 超级管理员始终返回true
            if (this == SUPER_ADMIN) {
                logger.debug("超级管理员角色拥有权限: {}", permissionCode);
                return true;
            }
            
            // 注意：这里应该从数据库查询角色对应的权限
            // 目前暂时为超级管理员放行，其他角色检查具体权限
            logger.debug("检查角色 {} 的权限: {}", this.name(), permissionCode);
            
            // 为每种角色临时指定一些权限，便于测试
            // 实际应用中应该从数据库查询
            switch (this) {
                case CITY_OPERATOR:
                    return permissionCode.startsWith("INVENTORY_") || 
                           permissionCode.startsWith("PRODUCT_") ||
                           permissionCode.startsWith("WAREHOUSE_");
                case AGENT:
                    return permissionCode.startsWith("ORDER_") || 
                           permissionCode.startsWith("INVENTORY_VIEW") ||
                           permissionCode.startsWith("PRODUCT_VIEW");
                case SUPPLIER:
                    return permissionCode.startsWith("PRODUCT_") || 
                           permissionCode.equals("INVENTORY_VIEW");
                case STORE:
                    return permissionCode.startsWith("ORDER_CREATE") || 
                           permissionCode.equals("PRODUCT_VIEW") ||
                           permissionCode.equals("INVENTORY_VIEW");
                default:
                    return false;
            }
        }

        public boolean hasPermission(Permission.ActionType action, Permission.ResourceType resource) {
            // 超级管理员始终返回true
            if (this == SUPER_ADMIN) {
                return true;
            }
            
            // 使用字符串形式的权限代码检查
            String permissionCode = resource.name() + "_" + action.name();
            return hasPermission(permissionCode);
        }
    }
}
