package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Inventory;
import com.example.warehousemanagement.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    // 创建新库存
    public Inventory createInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    // 通过 ID 获取库存
    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id).orElse(null);
    }

    // 获取所有库存
    public List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }

    // 更新库存
    public Inventory updateInventory(Long id, Inventory inventory) {
        inventory.setId(id); // 设置库存 ID
        return inventoryRepository.save(inventory);
    }

    // 删除库存
    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }

    // 根据仓库 ID 查找库存
    public List<Inventory> getInventoriesByWarehouseId(Long warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId);
    }

    // 根据产品 ID 查找库存
    public List<Inventory> getInventoriesByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId);
    }
}