package com.example.warehousemanagement.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@Getter
@Setter
@Table(
        indexes = {@Index(name = "idx_product_sku", columnList = "sku")}
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🌟 保留原有SKU逻辑（优化索引）
    @Column(nullable = false, length = 30, unique = true)
    private String sku;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // 🌟 分类优化：保留String类型（兼容现有测试），新增冗余编码
    @Column(length = 100)
    private String category; // 分类名称（如"饮品-矿泉水"）
    @Column(name = "category_code", length = 20) // 新增：分类编码（如"DRINK-WATER"）
    private String categoryCode;

    // 🌟 新增仓储物流属性（可空，不破坏现有测试）
    @Column(precision = 10, scale = 3)
    private BigDecimal weight; // 单商品重量（kg）
    @Column(precision = 10, scale = 3)
    private BigDecimal volume; // 单商品体积（m³）
    @Column(name = "stacking_limit", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer stackingLimit = 1; // 堆码层数（默认1层）

    // 🌟 多单位管理（仓储核心，可空设计）
    @Column(name = "purchase_unit", length = 20)
    private String purchaseUnit; // 采购单位（如"箱"）
    @Column(name = "sales_unit", length = 20)
    private String salesUnit; // 销售单位（如"瓶"）
    @Column(name = "unit_ratio", precision = 5, scale = 2)
    private BigDecimal unitConversionRatio; // 转换比例（1箱=24瓶）

    // 供应链信息（新增，可空）
    @Column(name = "supplier_name", length = 100)
    private String supplierName; // 直接存储供应商名称（如"农夫山泉股份有限公司"）

    // 保留原有价格和保质期字段
    @Column(precision = 10, scale = 2)
    private BigDecimal purchasePrice;
    @Column(precision = 10, scale = 2)
    private BigDecimal sellingPrice;
    @Column(name = "has_expiration")
    private boolean hasExpiration;
    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;

    // 保留库存和订单关联
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inventory> inventories = new ArrayList<>();
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    private static final Map<String, Long> SKU_COUNTER = new ConcurrentHashMap<>();
    // 🌟 保留原有业务方法（增强注释）
    public Integer getTotalInventory() {
        return inventories.stream()
                .mapToInt(Inventory::getQuantity)
                .sum(); // 销售单位总量
    }

    public boolean isLowStock(Integer safetyStock) {
        return getTotalInventory() < safetyStock;
    }

    // 🌟 新增仓储方法（带默认值，不影响测试）
    public BigDecimal getPurchaseUnitInventory() {
        if (unitConversionRatio == null || unitConversionRatio.compareTo(BigDecimal.ZERO) == 0) {
            return new BigDecimal(getTotalInventory()); // 无单位转换时默认按销售单位
        }
        return new BigDecimal(getTotalInventory())
                .divide(unitConversionRatio, 2, BigDecimal.ROUND_HALF_UP);
    }

    // 🌟 SKU生成逻辑优化（兼容String分类）
    @PrePersist
    protected void generateSku() {
        if (sku == null && category != null) {
            categoryCode = category.contains("-")
                    ? category.split("-")[0]
                    : category.toUpperCase();

            // 线程安全的计数器（测试环境可用）
            SKU_COUNTER.compute(categoryCode, (k, v) -> (v == null) ? 1L : v + 1);
            this.sku = String.format("%s-%06d", categoryCode, SKU_COUNTER.get(categoryCode));
        }
    }

    // 🌟 新增构造器（兼容测试）
    public Product() {}

    public Product(String name, String category) {
        this.name = name;
        this.category = category;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
}