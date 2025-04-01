package com.example.warehousemanagement.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关联的任务
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    // 配送状态（如：待发货、运输中、已完成等）
    @Column(nullable = false)
    private String shipmentStatus;

    // 实际发货时间
    @Column(name = "shipped_time")
    private LocalDateTime shippedTime;

    // 实际送达时间
    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;

    // 备注信息
    @Column(length = 255)
    private String notes;

    // 新增业务方法：标记为已发货
    public void markAsShipped() {
        this.shipmentStatus = "运输中";
        this.shippedTime = LocalDateTime.now();
    }

    // 新增业务方法：标记为已送达
    public void markAsDelivered() {
        if (!"运输中".equals(this.shipmentStatus)) {
            throw new IllegalStateException("只有运输中的货物才能标记为已送达");
        }
        this.shipmentStatus = "已完成";
        this.deliveryTime = LocalDateTime.now();
    }
}