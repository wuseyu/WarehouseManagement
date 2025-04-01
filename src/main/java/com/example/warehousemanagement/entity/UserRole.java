package com.example.warehousemanagement.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(
        name = "user_role",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"}), // 防重复分配
        indexes = {
                @Index(name = "idx_user_role_region", columnList = "assigned_region"),
                @Index(name = "idx_user_role_warehouse", columnList = "user_id, role_id")
        }
)
public class UserRole {

    // 🌟 自增主键（简化数据库操作）
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 单向关联：用户（仓储系统操作主体）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_role_user"))
    private User user;

    // 单向关联：角色（权限模板）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_role_role"))
    private Role role;

    // 🌟 仓储独特属性：区域权限
    @Column(name = "assigned_region", length = 50)
    private String assignedRegion; // 生效区域（如"上海市/浦东新区"）

    // 🌟 仓储独特属性：仓库权限（数据范围核心）
    @ElementCollection
    @CollectionTable(
            name = "user_role_warehouse",
            joinColumns = @JoinColumn(name = "user_role_id", foreignKey = @ForeignKey(name = "fk_ur_warehouse"))
    )
    @Column(name = "warehouse_id", nullable = false)
    private List<Long> managedWarehouses = new ArrayList<>(); // 管理的仓库ID列表

    // 🌟 审计属性（仓储合规要求）
    @Column(name = "assigned_time", nullable = false, updatable = false)
    private LocalDateTime assignedTime = LocalDateTime.now(); // 自动填充分配时间

    @Column(name = "assigned_by", nullable = false, length = 50)
    private String assignedBy; // 分配人（如"admin"超级管理员）

    // 🌟 仓储业务方法：校验仓库权限
    public boolean hasWarehousePermission(Long warehouseId) {
        return managedWarehouses.isEmpty() // 无限制（超级管理员）
                || managedWarehouses.contains(warehouseId); // 匹配指定仓库
    }

    // 🌟 仓储业务方法：校验区域权限
    public boolean hasRegionPermission(String region) {
        return assignedRegion == null // 无区域限制（超级管理员）
                || assignedRegion.equals(region); // 精确匹配生效区域
    }

    // 构造方法（JPA无参+业务构造）
    public UserRole() {}

    public UserRole(User user, Role role, String assignedBy) {
        this.user = user;
        this.role = role;
        this.assignedBy = assignedBy;
    }
}