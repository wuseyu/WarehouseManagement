package com.example.warehousemanagement.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task; // 关联的任务

    @Column(nullable = false)
    private String shipmentStatus; // 配送状态（如：待发货、运输中、已完成等）

    @Column(name = "shipped_time")
    private Timestamp shippedTime; // 实际发货时间

    @Column(name = "delivery_time")
    private Timestamp deliveryTime; // 实际送达时间

    @Column(length = 255)
    private String notes; // 备注信息

    // 其他可能的属性和方法可以根据需求添加
}