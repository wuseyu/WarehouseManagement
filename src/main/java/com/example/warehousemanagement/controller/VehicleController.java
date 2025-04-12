package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Vehicle;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")  // 超级管理员和管理员都可以访问车辆相关接口
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    /**
     * 获取所有车辆
     */
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.findAll());
    }

    /**
     * 根据ID获取车辆
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return vehicleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建新车辆
     */
    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(vehicleService.save(vehicle));
    }

    /**
     * 更新车辆信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        return vehicleService.findById(id)
                .map(existingVehicle -> {
                    return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicle));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 删除车辆
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        return vehicleService.findById(id)
                .map(vehicle -> {
                    vehicleService.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据状态查询车辆
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Vehicle>> getVehiclesByStatus(@PathVariable String status) {
        try {
            System.out.println("收到状态筛选请求: " + status);
            Vehicle.VehicleStatus vehicleStatus = Vehicle.VehicleStatus.valueOf(status);
            List<Vehicle> vehicles = vehicleService.findByStatus(vehicleStatus);
            System.out.println("找到符合状态 " + status + " 的车辆数量: " + vehicles.size());
            return ResponseEntity.ok(vehicles);
        } catch (IllegalArgumentException e) {
            System.err.println("无效的车辆状态: " + status);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("处理状态筛选请求时出错: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据车牌号查询车辆
     */
    @GetMapping("/plateNumber/{plateNumber}")
    public ResponseEntity<List<Vehicle>> getVehiclesByPlateNumber(@PathVariable String plateNumber) {
        return ResponseEntity.ok(vehicleService.findByPlateNumber(plateNumber));
    }

    // 设置车辆为维护状态
    @PutMapping("/{id}/maintenance")
    public ResponseEntity<Vehicle> setMaintenance(@PathVariable Long id) {
        Vehicle updatedVehicle = vehicleService.setMaintenance(id);
        return new ResponseEntity<>(updatedVehicle, HttpStatus.OK);
    }

    // 设置车辆为可用状态
    @PutMapping("/{id}/available")
    public ResponseEntity<Vehicle> setAvailable(@PathVariable Long id) {
        Vehicle updatedVehicle = vehicleService.setAvailable(id);
        return new ResponseEntity<>(updatedVehicle, HttpStatus.OK);
    }

    // 异常处理
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}