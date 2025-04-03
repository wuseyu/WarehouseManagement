package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    // 根据任务查找配送记录
    List<Shipment> findByTaskId(Long taskId);

    // 根据配送状态查找配送记录
    List<Shipment> findByShipmentStatus(String shipmentStatus);
} 