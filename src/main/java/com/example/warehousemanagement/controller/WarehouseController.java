package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Warehouse;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.service.WarehouseService;
import com.example.warehousemanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private static final Logger logger = LoggerFactory.getLogger(WarehouseController.class);

    @Autowired
    private WarehouseService warehouseService;
    
    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_VIEW')")
    public List<Warehouse> getAllWarehouses() {
        List<Warehouse> warehouses = warehouseService.getAllWarehouses();
        
        // 如果没有仓库数据，添加一个测试仓库
        if (warehouses.isEmpty()) {
            try {
                Warehouse warehouse = new Warehouse();
                warehouse.setName("测试仓库");
                warehouse.setLocation("测试地址");
                warehouse.setWarehouseType(Warehouse.WarehouseType.GENERAL);
                warehouse.setStatus(Warehouse.WarehouseStatus.ACTIVE);
                Warehouse savedWarehouse = warehouseService.createWarehouse(warehouse);
                warehouses.add(savedWarehouse);
                logger.info("成功添加测试仓库: {}", savedWarehouse.getName());
            } catch (Exception e) {
                logger.error("添加测试仓库失败", e);
            }
        }
        
        return warehouses;
    }

    @GetMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_VIEW')")
    public ResponseEntity<Warehouse> getWarehouse(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getWarehouse(id));
    }

    @PostMapping
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_MANAGE')")
    public ResponseEntity<Warehouse> createWarehouse(@RequestBody Map<String, Object> warehouseData) {
        Warehouse warehouse = new Warehouse();
        applyWarehouseData(warehouse, warehouseData);
        
        Warehouse createdWarehouse = warehouseService.createWarehouse(warehouse);
        logger.info("成功创建仓库: {}", createdWarehouse.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWarehouse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_MANAGE')")
    public ResponseEntity<Warehouse> updateWarehouse(@PathVariable Long id, @RequestBody Map<String, Object> warehouseData) {
        Warehouse warehouse = warehouseService.getWarehouse(id);
        applyWarehouseData(warehouse, warehouseData);
        
        Warehouse updatedWarehouse = warehouseService.updateWarehouse(warehouse);
        logger.info("成功更新仓库: {}", updatedWarehouse.getName());
        return ResponseEntity.ok(updatedWarehouse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.hasPermission('INVENTORY_MANAGE')")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        logger.info("成功删除仓库ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 将请求数据应用到仓库对象
     */
    private void applyWarehouseData(Warehouse warehouse, Map<String, Object> data) {
        if (data.containsKey("name")) {
            warehouse.setName((String) data.get("name"));
        }
        
        if (data.containsKey("location")) {
            warehouse.setLocation((String) data.get("location"));
        }
        
        if (data.containsKey("zone")) {
            warehouse.setZone((String) data.get("zone"));
        }
        
        if (data.containsKey("warehouseType")) {
            String warehouseType = (String) data.get("warehouseType");
            warehouse.setWarehouseType(Warehouse.WarehouseType.valueOf(warehouseType));
        }
        
        if (data.containsKey("status")) {
            String status = (String) data.get("status");
            warehouse.setStatus(Warehouse.WarehouseStatus.valueOf(status));
        }
        
        if (data.containsKey("totalVolume")) {
            try {
                Number totalVolume = (Number) data.get("totalVolume");
                warehouse.setTotalVolume(java.math.BigDecimal.valueOf(totalVolume.doubleValue()));
            } catch (Exception e) {
                logger.warn("解析totalVolume失败: {}", e.getMessage());
            }
        }
        
        if (data.containsKey("totalWeight")) {
            try {
                Number totalWeight = (Number) data.get("totalWeight");
                warehouse.setTotalWeight(java.math.BigDecimal.valueOf(totalWeight.doubleValue()));
            } catch (Exception e) {
                logger.warn("解析totalWeight失败: {}", e.getMessage());
            }
        }
        
        if (data.containsKey("managerId")) {
            try {
                Number managerId = (Number) data.get("managerId");
                if (managerId != null && managerId.longValue() > 0) {
                    Optional<User> managerOpt = userService.findById(managerId.longValue());
                    if (managerOpt.isPresent()) {
                        warehouse.setManager(managerOpt.get());
                    } else {
                        warehouse.setManager(null);
                        logger.warn("找不到ID为{}的用户", managerId.longValue());
                    }
                } else {
                    warehouse.setManager(null);
                }
            } catch (Exception e) {
                logger.warn("设置管理员失败: {}", e.getMessage());
                warehouse.setManager(null);
            }
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(NotFoundException e) {
        return e.getMessage();
    }
}
