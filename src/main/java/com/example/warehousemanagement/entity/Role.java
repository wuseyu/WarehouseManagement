package com.example.warehousemanagement.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

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

    // 核心字段：角色类型（枚举+唯一约束）
    @Column(nullable = false, unique = true, length = 50)
    @Enumerated(EnumType.STRING)
    private RoleType type;

    // 业务字段：中文名称+职责描述
    @Column(nullable = false, length = 100)
    private String name; // 如："华北区城市运营商"
    @Column(columnDefinition = "TEXT") // 📌 支持长文本
    private String responsibility; // 详细职责说明（区别于枚举描述）


    // 双向关联 RolePermission
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RolePermission> rolePermissions = new ArrayList<>();

    // 快捷方法：添加权限
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
        // 构造方法、getter省略
    }
}
