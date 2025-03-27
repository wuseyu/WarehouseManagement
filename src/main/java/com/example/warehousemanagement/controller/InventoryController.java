package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Inventory;
import com.example.warehousemanagement.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // 获取所有库存
    @GetMapping
    public List<Inventory> getAllInventories() {
        return inventoryService.getAllInventories();
    }

    // 根据 ID 获取库存
    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable Long id) {
        Inventory inventory = inventoryService.getInventoryById(id);
        return inventory != null ? ResponseEntity.ok(inventory) : ResponseEntity.notFound().build();
    }

    // 创建新库存
    @PostMapping
    public Inventory createInventory(@RequestBody Inventory inventory) {
        return inventoryService.createInventory(inventory);
    }

    // 更新库存
    @PutMapping("/{id}")
    public Inventory updateInventory(@PathVariable Long id, @RequestBody Inventory inventory) {
        return inventoryService.updateInventory(id, inventory);
    }

    // 删除库存
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }

    // 根据仓库 ID 查找库存
    @GetMapping("/warehouse/{warehouseId}")
    public List<Inventory> getInventoriesByWarehouseId(@PathVariable Long warehouseId) {
        return inventoryService.getInventoriesByWarehouseId(warehouseId);
    }

    // 根据产品 ID 查找库存
    @GetMapping("/product/{productId}")
    public List<Inventory> getInventoriesByProductId(@PathVariable Long productId) {
        return inventoryService.getInventoriesByProductId(productId);
    }
}