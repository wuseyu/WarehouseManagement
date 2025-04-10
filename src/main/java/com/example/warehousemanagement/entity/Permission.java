package com.example.warehousemanagement.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"code", "resource_type"}))
@Getter @Setter
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🌟 权限唯一标识（业务主键）
    @Column(nullable = false, length = 50)
    private String code; // 格式：{资源类型}_{操作}_{范围}，如WAREHOUSE_IN_ENTRY_001（001仓库入库）

    // 🌟 资源类型（仓储核心分类）
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ResourceType resourceType; // 取值：
    // WAREHOUSE（仓库）、INVENTORY（库存）、ORDER（订单）、VEHICLE（车辆）

    // 🌟 操作类型（原子动作）
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ActionType action; // 取值：
    // CREATE（创建）、UPDATE（修改）、DELETE（删除）、VIEW（查看）、EXPORT（导出）

    // 🌟 资源范围（可选，精确到仓库/区域）
    @Column(length = 100)
    private String resourceScope; // 如"WAREHOUSE:101"（101号仓库）、"REGION:上海市"

    // 业务元数据
    @Column(nullable = false, length = 100)
    private String description; // 如"修改杭州1号仓的库存数量"

    // 关联：角色-权限（N:N，通过RolePermission中间表）
    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RolePermission> rolePermissions = new ArrayList<>();


    // 枚举定义（仓储专用）
    public enum ResourceType {
        // 补充完整资源类型
        WAREHOUSE, INVENTORY, ORDER, PRODUCT, 
        VEHICLE, TASK, USER, ROLE, REPORT
    }

    public enum ActionType {
        CREATE, READ, UPDATE, DELETE, 
        APPROVE, TRANSFER, EXPORT
    }

    // 业务方法：校验是否匹配资源
    public boolean matchesResource(String resourceId) {
        return resourceScope == null || resourceScope.equals(resourceId);
    }
}
