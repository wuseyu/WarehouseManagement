package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Vehicle;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    // 创建新车辆
    @Transactional
    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    // 通过 ID 获取车辆
    @Transactional(readOnly = true)
    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));
    }

    // 获取所有车辆
    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    // 更新车辆信息
    @Transactional
    public Vehicle updateVehicle(Long id, Vehicle vehicleDetails) {
        Vehicle vehicle = getVehicleById(id);
        // 保留原始车牌号，避免违反唯一约束
        String originalPlateNumber = vehicle.getPlateNumber();
        vehicle.setDriverName(vehicleDetails.getDriverName());
        vehicle.setCapacity(vehicleDetails.getCapacity());
        vehicle.setStatus(vehicleDetails.getStatus());
        vehicle.setCurrentLocation(vehicleDetails.getCurrentLocation());
        vehicle.setInsuranceDetails(vehicleDetails.getInsuranceDetails());
        // 只有当新车牌号不为空且与原车牌号不同时才更新
        if (vehicleDetails.getPlateNumber() != null && !vehicleDetails.getPlateNumber().equals(originalPlateNumber)) {
            vehicle.setPlateNumber(vehicleDetails.getPlateNumber());
        }
        return vehicleRepository.save(vehicle);
    }

    // 删除车辆
    @Transactional
    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

    // 根据状态查找车辆
    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesByStatus(Vehicle.VehicleStatus status) {
        return vehicleRepository.findByStatus(status);
    }

    // 将车辆设置为维护状态
    @Transactional
    public Vehicle setMaintenance(Long vehicleId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        vehicle.setStatus(Vehicle.VehicleStatus.MAINTENANCE);
        return vehicleRepository.save(vehicle);
    }

    // 将车辆设置为可用状态
    @Transactional
    public Vehicle setAvailable(Long vehicleId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        if (vehicle.getStatus() != Vehicle.VehicleStatus.MAINTENANCE) {
            throw new IllegalStateException("只有维护状态的车辆才能设置为可用状态");
        }
        vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
        return vehicleRepository.save(vehicle);
    }

    /**
     * 查找所有车辆
     */
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }
    
    /**
     * 根据ID查找车辆
     */
    public Optional<Vehicle> findById(Long id) {
        return vehicleRepository.findById(id);
    }
    
    /**
     * 保存车辆信息
     */
    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }
    
    /**
     * 根据ID删除车辆
     */
    public void deleteById(Long id) {
        vehicleRepository.deleteById(id);
    }
    
    /**
     * 根据状态查找车辆
     */
    public List<Vehicle> findByStatus(Vehicle.VehicleStatus status) {
        return vehicleRepository.findByStatus(status);
    }
    
    /**
     * 根据车牌号查找车辆
     */
    public List<Vehicle> findByPlateNumber(String plateNumber) {
        return vehicleRepository.findByPlateNumber(plateNumber);
    }
}