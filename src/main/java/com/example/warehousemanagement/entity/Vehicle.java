package com.example.warehousemanagement.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 车牌号，唯一且不能为空
    @Column(name = "plate_number", nullable = false, unique = true, length = 20)
    private String plateNumber;

    // 司机姓名（拼音）
    @Column(name = "driver_name", length = 100)
    private String driverName;

    // 车辆容量
    @Column(nullable = false)
    private Integer capacity;

    // 车辆状态
    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    // 当前位置
    @Column(name = "current_location", length = 100)
    private String currentLocation;

    // 保险详情
    @Column(name = "insurance_details", length = 255)
    private String insuranceDetails;

    // 与Task的一对多关系
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"vehicle", "assignedUser", "order", "hibernateLazyInitializer"})
    private List<Task> tasks = new ArrayList<>();

    // 车牌号生成相关的静态变量和常量
    private static final Map<String, Integer> PLATE_NUMBER_COUNTER = new ConcurrentHashMap<>();
    private static final String[] REGION_PREFIXES = {"BJ", "SH", "GZ", "SZ", "CD"};
    private static final String[] REGION_NAMES = {"北京", "上海", "广州", "深圳", "成都"};

    public enum VehicleStatus {
        AVAILABLE, IN_USE, MAINTENANCE, PENDING
    }

    // 生成车牌号的方法
    @PrePersist
    protected void onCreate() {
        // 确保车牌号不为空
        if (plateNumber == null || plateNumber.isEmpty()) {
            // 随机选择一个地区
            int regionIndex = (int) (Math.random() * REGION_PREFIXES.length);
            String regionPrefix = REGION_PREFIXES[regionIndex];
            String regionName = REGION_NAMES[regionIndex];
            
            // 生成唯一的时间戳编号
            long timestamp = System.currentTimeMillis();
            int random = (int)(Math.random() * 1000);
            
            // 生成车牌号格式：地区名-序号（例如：北京-001）
            this.plateNumber = regionName + "-" + timestamp + "-" + random;
        }
    }
}