package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Inventory;
import com.example.warehousemanagement.entity.Inventory.InventoryStatus;
import com.example.warehousemanagement.exception.ConcurrentInventoryException;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_VIEW')")
    public Inventory getInventory(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inventory not found"));
    }

    @Transactional
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_CREATE')")
    public Inventory createInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    @Transactional
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_UPDATE')")
    public void adjustInventoryQuantity(Long id, Integer delta, Integer expectedVersion) {
        int affectedRows = inventoryRepository.adjustQuantity(id, delta, expectedVersion);
        if (affectedRows == 0) {
            throw new ConcurrentInventoryException("Inventory updated by another transaction");
        }
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_VIEW')")
    public List<Inventory> getInventoryByWarehouseAndProduct(Long warehouseId, Long productId) {
        return inventoryRepository.findByWarehouseIdAndProductId(warehouseId, productId);
    }

    @Transactional
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_UPDATE')")
    public void bulkUpdateStatus(InventoryStatus status, List<Long> inventoryIds) {
        inventoryRepository.bulkUpdateStatus(status, inventoryIds);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_VIEW')")
    public Page<Inventory> listInventoryByStatus(InventoryStatus status, Pageable pageable) {
        return inventoryRepository.findByStatus(status, pageable);
    }
}