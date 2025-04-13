package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Warehouse;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_VIEW')")
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_VIEW')")
    public Warehouse getWarehouse(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Warehouse not found"));
    }

    @Transactional
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_CREATE')")
    public Warehouse createWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }
    
    @Transactional
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_MANAGE')")
    public Warehouse updateWarehouse(Warehouse warehouse) {
        // 检查仓库是否存在
        if (!warehouseRepository.existsById(warehouse.getId())) {
            throw new NotFoundException("Warehouse not found with id: " + warehouse.getId());
        }
        return warehouseRepository.save(warehouse);
    }
    
    @Transactional
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_MANAGE')")
    public void deleteWarehouse(Long id) {
        // 检查仓库是否存在
        if (!warehouseRepository.existsById(id)) {
            throw new NotFoundException("Warehouse not found with id: " + id);
        }
        warehouseRepository.deleteById(id);
    }
}
