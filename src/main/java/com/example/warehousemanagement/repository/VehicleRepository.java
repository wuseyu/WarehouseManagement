package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Vehicle;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // 根据车辆状态查找车辆
    List<Vehicle> findByStatus(Vehicle.VehicleStatus status);

    // 根据车牌号查找车辆
    List<Vehicle> findByPlateNumber(String plateNumber);
    
    // 根据驾驶员姓名查找车辆
    List<Vehicle> findByDriverName(String driverName);
    
    // 根据当前位置查找车辆
    List<Vehicle> findByCurrentLocation(String currentLocation);
} 