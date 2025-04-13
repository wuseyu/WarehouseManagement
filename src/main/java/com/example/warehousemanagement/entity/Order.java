package com.example.warehousemanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_order_user_status", columnList = "user_id, status"),
                @Index(name = "idx_order_order_no", columnList = "order_no", unique = true)
        }
)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 仓储配送核心字段
    @Column(name = "order_no", nullable = false, length = 50)
    private String orderNo; // 唯一订单编号（格式：ORD-YYYYMMDD-XXXX）

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"orders", "hibernateLazyInitializer", "roles"})
    private User user;

    @Column(name = "delivery_address", length = 255, nullable = false)
    private String deliveryAddress; // 配送地址（对接配送任务）

    // 业务状态
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING; // 默认初始状态

    // 金额与时间
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    // 关联关系
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "task_id") // 新增 JoinColumn 来指定外键列
    @JsonIgnoreProperties({"order", "assignedUser", "vehicle", "hibernateLazyInitializer"})
    private Task task;

    // 🌟 业务方法：自动计算总金额
// 在 Order.java 中
    public void calculateTotalAmount() {
        BigDecimal amount = orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalAmount = amount;
    }

    // 状态流转方法
    public void confirm() {
        if (status != OrderStatus.PENDING) { // 检查是否为待处理
            throw new IllegalStateException("仅待处理订单可确认");
        }
        this.status = OrderStatus.PROCESSING; // 转为处理中
    }

    public void ship() {
        if (status != OrderStatus.PROCESSING) { // 检查是否为处理中
            throw new IllegalStateException("仅处理中订单可发货");
        }
        this.status = OrderStatus.SHIPPED; // 转为已发货
    }

    // 新增完成方法（可选）
    public void complete() {
        if (status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("仅发货订单可完成");
        }
        this.status = OrderStatus.DELIVERED;
    }

    // 🌟 JPA回调自动填充时间
    @PrePersist
    protected void onCreate() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    // 🌟 优化后的构造器（职责分离）
    public Order() {}

    public Order(User user, String deliveryAddress) {
        this.user = user;
        this.deliveryAddress = deliveryAddress;
        this.orderNo = generateOrderNo(); // 自动生成订单号
    }

    // 🌟 私有工具方法：生成订单号
    private String generateOrderNo() {
        return String.format("ORD-%s-%d",
                Timestamp.valueOf("2025-03-28 00:00:00").toString().replace(" ", "-").substring(0, 10),
                System.currentTimeMillis() % 10000
        ); // 示例格式：ORD-2025-03-28-1234
    }

    // 保留原有枚举，可扩展
    public enum OrderStatus {
        PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }
}