package com.example.warehousemanagement.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Getter
@Setter
@Table(
        name = "role_permission",
        indexes = {
                @Index(name = "idx_role_id", columnList = "role_id"),
                @Index(name = "idx_permission_id", columnList = "permission_id")
        }
)
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关联角色
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // 关联权限
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    // 扩展字段：数据范围（如仓库ID）
    @Column(name = "data_scope", length = 100)
    private String dataScope;

    // 构造器
    public RolePermission() {}

    public RolePermission(Role role, Permission permission) {
        this.role = role;
        this.permission = permission;
    }
}