package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // 根据仓库 ID 查找库存
    List<Inventory> findByWarehouseId(Long warehouseId);

    // 根据产品 ID 查找库存
    List<Inventory> findByProductId(Long productId);
}