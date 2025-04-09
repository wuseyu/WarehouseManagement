package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Vehicle;
import com.example.warehousemanagement.entity.Task;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    // 创建车辆
    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
        Vehicle savedVehicle = vehicleService.createVehicle(vehicle);
        return new ResponseEntity<>(savedVehicle, HttpStatus.CREATED);
    }

    // 根据 ID 查找车辆
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        return new ResponseEntity<>(vehicle, HttpStatus.OK);
    }

    // 获取所有车辆
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    // 更新车辆信息
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicleDetails) {
        Vehicle updatedVehicle = vehicleService.updateVehicle(id, vehicleDetails);
        return new ResponseEntity<>(updatedVehicle, HttpStatus.OK);
    }

    // 删除车辆
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 根据状态查找车辆
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Vehicle>> getVehiclesByStatus(@PathVariable Vehicle.VehicleStatus status) {
        List<Vehicle> vehicles = vehicleService.getVehiclesByStatus(status);
        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    // 分配任务给车辆
    @PostMapping("/{id}/tasks")
    public ResponseEntity<Vehicle> assignTask(@PathVariable Long id, @RequestBody Task task) {
        Vehicle updatedVehicle = vehicleService.assignTask(id, task);
        return new ResponseEntity<>(updatedVehicle, HttpStatus.OK);
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