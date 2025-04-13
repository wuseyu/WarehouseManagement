package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Inventory;
import com.example.warehousemanagement.entity.Warehouse;
import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.exception.ConcurrentInventoryException;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.repository.InventoryRepository;
import com.example.warehousemanagement.repository.WarehouseRepository;
import com.example.warehousemanagement.repository.ProductRepository;
import com.example.warehousemanagement.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ProductRepository productRepository;

    // 获取库存详情
    @GetMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_VIEW')")
    public ResponseEntity<Inventory> getInventory(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventory(id));
    }

    // 创建库存记录
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_CREATE')")
    public Inventory createInventory(@RequestBody Inventory inventory) {
        return inventoryService.createInventory(inventory);
    }

    // 调整库存数量（带乐观锁）
    @PutMapping("/{id}/adjust")
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_UPDATE')")
    public void adjustQuantity(
            @PathVariable Long id,
            @RequestBody AdjustmentRequest request) {
        inventoryService.adjustInventoryQuantity(
                id,
                request.delta(),
                request.version()
        );
    }

    // 分页查询库存
    @GetMapping
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_VIEW')")
    public Page<Inventory> listInventories(
            @RequestParam(required = false) Inventory.InventoryStatus status,
            @PageableDefault(sort = "createdAt") Pageable pageable) {
        // 检查是否有数据，如果没有则添加一些临时测试数据
        long count = inventoryRepository.count();
        if (count == 0) {
            addSampleData();
        }
        return inventoryService.listInventoryByStatus(status, pageable);
    }

    // 添加示例数据（仅用于测试）
    private void addSampleData() {
        try {
            // 1. 创建仓库
            Warehouse warehouse = new Warehouse();
            warehouse.setName("测试仓库");
            warehouse.setLocation("测试地址");
            warehouse.setWarehouseType(Warehouse.WarehouseType.GENERAL);
            warehouse.setStatus(Warehouse.WarehouseStatus.ACTIVE);
            warehouse = warehouseRepository.save(warehouse);

            // 2. 创建产品
            Product product = new Product();
            product.setName("测试产品");
            product.setCategory("测试分类");
            product.setSku("TEST-SKU-001");
            product.setHasExpiration(true);
            product.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            product = productRepository.save(product);

            // 3. 创建库存
            Inventory inventory = new Inventory();
            inventory.setWarehouse(warehouse);
            inventory.setProduct(product);
            inventory.setQuantity(100);
            inventory.setBatchNo("TEST-BATCH-001");
            inventory.setExpirationDate(LocalDate.now().plusDays(30));
            inventory.setStatus(Inventory.InventoryStatus.AVAILABLE);
            inventory.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            inventory.setVersion(0);
            inventoryRepository.save(inventory);
        } catch (Exception e) {
            logger.error("添加示例数据失败", e);
        }
    }

    // 批量更新库存状态
    @PutMapping("/bulk-status")
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_UPDATE')")
    public void bulkUpdateStatus(
            @RequestBody BulkStatusUpdateRequest request) {
        inventoryService.bulkUpdateStatus(
                request.status(),
                request.ids()
        );
    }

    // 异常处理
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(NotFoundException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConcurrentInventoryException.class)
    public String handleConcurrentConflict(ConcurrentInventoryException e) {
        return e.getMessage();
    }

    // DTO定义
    public record AdjustmentRequest(Integer delta, Integer version) {}
    public record BulkStatusUpdateRequest(
            Inventory.InventoryStatus status,
            List<Long> ids
    ) {}
}