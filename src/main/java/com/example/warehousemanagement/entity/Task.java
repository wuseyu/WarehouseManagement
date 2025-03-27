package com.example.warehousemanagement.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String description; // 任务描述

    @Enumerated(EnumType.STRING)
    private TaskStatus status; // 任务状态

    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser; // 分配的用户

    @ManyToOne
    @JoinColumn(name = "assigned_vehicle_id")
    private Vehicle vehicle; // 分配的车辆

    @Column(name = "destination", nullable = false)
    private String destination; // 目的地

    @Column(name = "scheduled_time", nullable = false)
    private Timestamp scheduledTime; // 计划执行时间

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt; // 创建时间

    public enum TaskStatus {
        PENDING, IN_PROGRESS, COMPLETED // 任务状态
    }
}