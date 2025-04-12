package com.example.warehousemanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(
        name = "inventory",
        indexes = {
                @Index(name = "idx_inventory_warehouse_product", columnList = "warehouse_id, product_id"),
                @Index(name = "idx_inventory_expiration", columnList = "expiration_date")
        }
)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 保留原有仓库和产品关联（可空性优化）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    @JsonIgnoreProperties({"inventories", "hibernateLazyInitializer", "handler"})
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"inventories", "orderItems", "hibernateLazyInitializer", "handler"})
    private Product product;

    // 保留数量（销售单位，必填）
    @Column(nullable = false)
    private Integer quantity;

    // 🌟 新增仓储核心字段（可空，兼容现有测试）
    @Column(name = "batch_no", length = 20)
    private String batchNo; // 批次号（如202503-001）

    @Column(name = "expiration_date")
    private LocalDate expirationDate; // 保质期（冷链/食品必填）

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'AVAILABLE'")
    private InventoryStatus status = InventoryStatus.AVAILABLE; // 状态机

    @Column(name = "locked_quantity")
    private Integer lockedQuantity = 0; // 锁定数量（如订单占用未出库）

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    // 🌟 版本号（乐观锁，防并发更新）
    @Version
    private Integer version;

    // 🌟 仓储业务方法（封装核心逻辑）
    public boolean deductQuantity(Integer amount) {
        if (status != InventoryStatus.AVAILABLE) return false; // 非可用状态不可扣减
        if (quantity < amount || lockedQuantity + amount > quantity) {
            return false; // 库存不足或超卖
        }
        lockedQuantity += amount;
        updatedAt = new Timestamp(System.currentTimeMillis());
        return true;
    }

    public boolean releaseLock(Integer amount) {
        if (lockedQuantity < amount) return false;
        lockedQuantity -= amount;
        updatedAt = new Timestamp(System.currentTimeMillis());
        return true;
    }

    public boolean confirmDelivery(Integer amount) {
        if (lockedQuantity < amount) return false;
        quantity -= amount;
        lockedQuantity -= amount;
        updatedAt = new Timestamp(System.currentTimeMillis());
        return true;
    }

    // 🌟 保留原有无参构造（兼容Hibernate）
    public Inventory() {}

    // 🌟 构造函数（兼容测试）
    public Inventory(Warehouse warehouse, Product product, Integer quantity) {
        this.warehouse = warehouse;
        this.product = product;
        this.quantity = quantity;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
        this.version = 0;
    }

    // 枚举定义（建议单独文件，此处内联）
    public enum InventoryStatus {
        AVAILABLE,  // 可用
        LOCKED,     // 已锁定（订单占用）
        FROZEN,     // 冻结（ Recall/质检）
        SCRAPPED    // 报废
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    @PrePersist
    protected void onCreate() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.createdAt = now;
        this.updatedAt = now;
    }
}