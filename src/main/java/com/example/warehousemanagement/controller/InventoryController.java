package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Inventory;
import com.example.warehousemanagement.exception.ConcurrentInventoryException;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private InventoryService inventoryService;

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
        return status != null ?
                inventoryService.listInventoryByStatus(status, pageable) :
                inventoryService.listInventoryByStatus(null, pageable);
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

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // DTO定义
    public record AdjustmentRequest(Integer delta, Integer version) {}
    public record BulkStatusUpdateRequest(
            Inventory.InventoryStatus status,
            List<Long> ids
    ) {}
}