package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.OrderItem;
import com.example.warehousemanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // 根据订单查询订单项
    List<OrderItem> findByOrder(Order order);

    // 根据商品查询订单项
    List<OrderItem> findByProduct(Product product);

    // 根据单价范围查询
    List<OrderItem> findByUnitPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // 根据数量范围查询
    List<OrderItem> findByQuantityBetween(Integer minQuantity, Integer maxQuantity);

    // 根据订单和商品查询
    Optional<OrderItem> findByOrderAndProduct(Order order, Product product);

    // 根据批次号查询
    List<OrderItem> findByBatchNo(String batchNo);

    // 根据仓库ID查询
    List<OrderItem> findByWarehouseId(Long warehouseId);
}