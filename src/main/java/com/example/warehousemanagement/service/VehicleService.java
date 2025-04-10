package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Vehicle;
import com.example.warehousemanagement.entity.Task;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    // 创建新车辆
    @Transactional
    @PreAuthorize("@customSecurityExpression.hasPermission('VEHICLE_CREATE')")
    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    // 通过 ID 获取车辆
    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('VEHICLE_VIEW')")
    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));
    }

    // 获取所有车辆
    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('VEHICLE_VIEW')")
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    // 更新车辆信息
    @Transactional
    @PreAuthorize("@customSecurityExpression.hasPermission('VEHICLE_UPDATE')")
    public Vehicle updateVehicle(Long id, Vehicle vehicleDetails) {
        Vehicle vehicle = getVehicleById(id);
        vehicle.setPlateNumber(vehicleDetails.getPlateNumber());
        vehicle.setDriverName(vehicleDetails.getDriverName());
        vehicle.setCapacity(vehicleDetails.getCapacity());
        vehicle.setStatus(vehicleDetails.getStatus());
        vehicle.setCurrentLocation(vehicleDetails.getCurrentLocation());
        vehicle.setInsuranceDetails(vehicleDetails.getInsuranceDetails());
        return vehicleRepository.save(vehicle);
    }

    // 删除车辆
    @Transactional
    @PreAuthorize("@customSecurityExpression.hasPermission('VEHICLE_DELETE')")
    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

    // 根据状态查找车辆
    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('VEHICLE_VIEW')")
    public List<Vehicle> getVehiclesByStatus(Vehicle.VehicleStatus status) {
        return vehicleRepository.findByStatus(status);
    }

    // 分配任务给车辆
    @Transactional
    @PreAuthorize("@customSecurityExpression.hasPermission('VEHICLE_UPDATE')")
    public Vehicle assignTask(Long vehicleId, Task task) {
        Vehicle vehicle = getVehicleById(vehicleId);
        vehicle.assignTask(task);
        return vehicleRepository.save(vehicle);
    }

    // 完成车辆任务
    @Transactional
    @PreAuthorize("@customSecurityExpression.hasPermission('VEHICLE_UPDATE')")
    public Vehicle completeTask(Long vehicleId, Task task) {
        Vehicle vehicle = getVehicleById(vehicleId);
        vehicle.completeTask(task);
        return vehicleRepository.save(vehicle);
    }

    // 将车辆设置为维护状态
    @Transactional
    @PreAuthorize("@customSecurityExpression.hasPermission('VEHICLE_UPDATE')")
    public Vehicle setMaintenance(Long vehicleId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        if (!vehicle.getAssignedTasks().isEmpty()) {
            throw new IllegalStateException("有正在进行的任务，无法设置为维护状态");
        }
        vehicle.setStatus(Vehicle.VehicleStatus.MAINTENANCE);
        return vehicleRepository.save(vehicle);
    }

    // 将车辆设置为可用状态
    @Transactional
    @PreAuthorize("@customSecurityExpression.hasPermission('VEHICLE_UPDATE')")
    public Vehicle setAvailable(Long vehicleId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        if (vehicle.getStatus() != Vehicle.VehicleStatus.MAINTENANCE) {
            throw new IllegalStateException("只有维护状态的车辆才能设置为可用状态");
        }
        vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
        return vehicleRepository.save(vehicle);
    }
}