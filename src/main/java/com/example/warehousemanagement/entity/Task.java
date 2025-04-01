package com.example.warehousemanagement.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 任务描述
    @Column(columnDefinition = "TEXT")
    private String description;

    // 任务状态
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    // 分配的用户
    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    // 分配的车辆
    @ManyToOne
    @JoinColumn(name = "assigned_vehicle_id")
    private Vehicle vehicle;

    // 目的地
    @Column(name = "destination", nullable = false)
    private String destination;

    // 计划执行时间
    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;

    // 创建时间
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum TaskStatus {
        PENDING, IN_PROGRESS, COMPLETED // 任务状态
    }

    // 新增业务方法：开始任务
    public void startTask() {
        if (this.status != TaskStatus.PENDING) {
            throw new IllegalStateException("只有待处理的任务才能开始");
        }
        this.status = TaskStatus.IN_PROGRESS;
    }

    // 新增业务方法：完成任务
    public void completeTask() {
        if (this.status != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("只有进行中的任务才能完成");
        }
        this.status = TaskStatus.COMPLETED;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}