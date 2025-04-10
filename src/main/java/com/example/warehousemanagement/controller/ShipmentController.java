package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Shipment;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    // 创建配送记录
    @PostMapping
    @PreAuthorize("@customSecurityExpression.hasPermission('SHIPMENT_CREATE')")
    public ResponseEntity<Shipment> createShipment(@RequestBody Shipment shipment) {
        Shipment savedShipment = shipmentService.createShipment(shipment);
        return new ResponseEntity<>(savedShipment, HttpStatus.CREATED);
    }

    // 根据 ID 查找配送记录
    @GetMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.hasPermission('SHIPMENT_VIEW')")
    public ResponseEntity<Shipment> getShipmentById(@PathVariable Long id) {
        Shipment shipment = shipmentService.getShipmentById(id);
        return new ResponseEntity<>(shipment, HttpStatus.OK);
    }

    // 获取所有配送记录
    @GetMapping
    @PreAuthorize("@customSecurityExpression.hasPermission('SHIPMENT_VIEW')")
    public ResponseEntity<List<Shipment>> getAllShipments() {
        List<Shipment> shipments = shipmentService.getAllShipments();
        return new ResponseEntity<>(shipments, HttpStatus.OK);
    }

    // 删除配送记录
    @DeleteMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.hasPermission('SHIPMENT_DELETE')")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 根据任务 ID 获取配送记录
    @GetMapping("/task/{taskId}")
    @PreAuthorize("@customSecurityExpression.hasPermission('SHIPMENT_VIEW')")
    public ResponseEntity<List<Shipment>> getShipmentsByTaskId(@PathVariable Long taskId) {
        List<Shipment> shipments = shipmentService.getShipmentsByTaskId(taskId);
        return new ResponseEntity<>(shipments, HttpStatus.OK);
    }

    // 根据配送状态获取配送记录
    @GetMapping("/status/{status}")
    @PreAuthorize("@customSecurityExpression.hasPermission('SHIPMENT_VIEW')")
    public ResponseEntity<List<Shipment>> getShipmentsByStatus(@PathVariable String status) {
        List<Shipment> shipments = shipmentService.getShipmentsByStatus(status);
        return new ResponseEntity<>(shipments, HttpStatus.OK);
    }

    // 异常处理
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}