package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // 根据仓库和产品查询库存（兼容批量查询）
    List<Inventory> findByWarehouseIdAndProductId(Long warehouseId, Long productId);

    // 按状态分页查询（支持分页）
    Page<Inventory> findByStatus(Inventory.InventoryStatus status, Pageable pageable);

    // 批量更新库存状态（原子操作）
    @Modifying
    @Transactional
    @Query("UPDATE Inventory i SET i.status = :status WHERE i.id IN :ids")
    int bulkUpdateStatus(@Param("status") Inventory.InventoryStatus status, 
                        @Param("ids") List<Long> ids);

    // 库存数量增减操作（乐观锁版本控制）
    @Modifying
    @Transactional
    @Query("UPDATE Inventory i SET i.quantity = i.quantity + :delta, i.version = i.version + 1 WHERE i.id = :id AND i.version = :version")
    int adjustQuantity(@Param("id") Long id,
                       @Param("delta") Integer delta,
                       @Param("version") Integer version);

    // 根据产品ID批量查询（带排序）
    List<Inventory> findByProductIdOrderByExpirationDateAsc(Long productId);
}