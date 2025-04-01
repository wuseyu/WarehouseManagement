package com.example.warehousemanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "roles")
public class Role {

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
        rolePermissions.add(rp);
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

        RoleType(String post, String Duty) {
            this.cnName = post;
            this.description = Duty;
        }
    }
}
