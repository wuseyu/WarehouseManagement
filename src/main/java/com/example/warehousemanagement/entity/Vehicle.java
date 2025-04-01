package com.example.warehousemanagement.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 车牌号，唯一且不能为空
    @Column(nullable = false, unique = true, length = 20)
    private String plateNumber;

    // 司机姓名
    @Column(length = 100)
    private String driverName;

    // 车辆容量
    @Column(nullable = false)
    private Integer capacity;

    // 车辆状态
    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    // 当前位置
    @Column(length = 100)
    private String currentLocation;

    // 分配的任务列表
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> assignedTasks;

    // 保险详情
    @Column(length = 255)
    private String insuranceDetails;

    public enum VehicleStatus {
        AVAILABLE, IN_USE, MAINTENANCE
    }

    // 新增业务方法：分配任务
    public void assignTask(Task task) {
        if (this.status != VehicleStatus.AVAILABLE) {
            throw new IllegalStateException("只有可用的车辆才能分配任务");
        }
        this.assignedTasks.add(task);
        this.status = VehicleStatus.IN_USE;
    }

    // 新增业务方法：完成任务
    public void completeTask(Task task) {
        if (this.status != VehicleStatus.IN_USE) {
            throw new IllegalStateException("只有正在使用的车辆才能完成任务");
        }
        this.assignedTasks.remove(task);
        if (this.assignedTasks.isEmpty()) {
            this.status = VehicleStatus.AVAILABLE;
        }
    }
}