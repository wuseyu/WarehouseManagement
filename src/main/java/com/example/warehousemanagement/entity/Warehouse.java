package com.example.warehousemanagement.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(
        indexes = {
                @Index(name = "idx_warehouse_type_status", columnList = "warehouse_type, status"),
                @Index(name = "idx_warehouse_location", columnList = "location")
        }
)
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 保留原有核心字段（名称/位置/负责人）
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String location; // 详细地址（省市区+街道）

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager; // 负责人（保留原有关系）

    // 🌟 新增仓储核心字段（可空/默认值，兼容测试）
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'GENERAL'")
    private WarehouseType warehouseType = WarehouseType.GENERAL; // 类型（普通/冷链/保税）

    @Column(name = "total_volume", precision = 10, scale = 3)
    private BigDecimal totalVolume; // 总容积（m³）

    @Column(name = "used_volume", precision = 10, scale = 3, columnDefinition = "DECIMAL(10,3) DEFAULT 0.0")
    private BigDecimal usedVolume = BigDecimal.ZERO; // 已用容积

    @Column(name = "total_weight", precision = 10, scale = 3)
    private BigDecimal totalWeight; // 总承重（吨）

    @Column(name = "used_weight", precision = 10, scale = 3, columnDefinition = "DECIMAL(10,3) DEFAULT 0.0")
    private BigDecimal usedWeight = BigDecimal.ZERO; // 已用承重

    @Column(name = "zone", length = 50)
    private String zone; // 库区（如A1-01货架区）

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
    private WarehouseStatus status = WarehouseStatus.ACTIVE; // 状态（启用/禁用/维修）

    // 🌟 关联库存（1仓库→N库存，双向关联）
    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inventory> inventories = new ArrayList<>();

    // 🌟 业务方法：计算可用容积
    public BigDecimal getAvailableVolume() {
        return totalVolume == null ? BigDecimal.ZERO : totalVolume.subtract(usedVolume);
    }

    // 🌟 业务方法：计算可用承重
    public BigDecimal getAvailableWeight() {
        return totalWeight == null ? BigDecimal.ZERO : totalWeight.subtract(usedWeight);
    }

    // 🌟 容量更新（库存变更时自动调用）
    public void updateCapacity(BigDecimal addedVolume, BigDecimal addedWeight) {
        if (addedVolume != null) usedVolume = usedVolume.add(addedVolume);
        if (addedWeight != null) usedWeight = usedWeight.add(addedWeight);
    }

    // 保留原有构造器（兼容测试）
    public Warehouse() {}

    public Warehouse(String name, String location) {
        this.name = name;
        this.location = location;
    }

    // 枚举定义（建议单独文件，此处内联）
    public enum WarehouseType {
        GENERAL,      // 普通仓
        COLD_CHAIN,   // 冷链仓（2-8℃）
        BONDED,       // 保税仓
        HAZARDOUS     // 危险品仓
    }

    public enum WarehouseStatus {
        ACTIVE,       // 正常使用
        DISABLED,     // 禁用（盘点/维修）
        ARCHIVED      // 历史仓库（不可新增库存）
    }
}