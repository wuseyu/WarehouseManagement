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

    @Column(nullable = false, unique = true, length = 20)
    private String plateNumber;

    @Column(length = 100)
    private String driverName;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    @Column(length = 100)
    private String currentLocation;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> assignedTasks;

    @Column(length = 255)
    private String insuranceDetails;

    public enum VehicleStatus {
        AVAILABLE, IN_USE, MAINTENANCE
    }
}
