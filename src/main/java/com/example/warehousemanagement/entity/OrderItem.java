package com.example.warehousemanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(
        indexes = {@Index(name = "idx_order_item_product", columnList = "product_id")}
)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关联订单（多对一）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties({"orderItems", "hibernateLazyInitializer"})
    private Order order;

    // 关联商品（多对一）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"orderItems", "inventories", "hibernateLazyInitializer"})
    private Product product;

    // 订单项核心字段
    private Integer quantity; // 销售单位数量

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice; // 商品单价（销售单价）

    // 仓储扩展字段
    @Column(name = "batch_no", length = 20)
    private String batchNo; // 关联库存批次（可选）

    @Column(name = "warehouse_id")
    private Long warehouseId; // 发货仓库ID（可选）

    // 构造器
    public OrderItem() {}

    public OrderItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getSellingPrice(); // 自动获取商品单价
    }

    // 业务方法：计算订单项金额
    public BigDecimal getAmount() {
        return unitPrice.multiply(new BigDecimal(quantity));
    }
}