package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Shipment;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    // 创建新配送记录
    @Transactional
    @PreAuthorize("@customSecurityExpression.hasPermission('SHIPMENT_CREATE')")
    public Shipment createShipment(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }

    // 通过 ID 获取配送记录
    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('SHIPMENT_VIEW')")
    public Shipment getShipmentById(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Shipment not found"));
    }

    // 获取所有配送记录
    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('SHIPMENT_VIEW')")
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    // 删除配送记录
    @Transactional
    @PreAuthorize("@customSecurityExpression.hasPermission('SHIPMENT_DELETE')")
    public void deleteShipment(Long id) {
        shipmentRepository.deleteById(id);
    }

    // 根据任务 ID 获取配送记录
    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('SHIPMENT_VIEW')")
    public List<Shipment> getShipmentsByTaskId(Long taskId) {
        return shipmentRepository.findByTaskId(taskId);
    }

    // 根据配送状态获取配送记录
    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('SHIPMENT_VIEW')")
    public List<Shipment> getShipmentsByStatus(String status) {
        return shipmentRepository.findByShipmentStatus(status);
    }
}