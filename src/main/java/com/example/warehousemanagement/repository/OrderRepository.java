package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 根据用户 ID 查找订单
    List<Order> findByUserId(Long userId);

    // 根据订单状态查找订单
    List<Order> findByStatus(Order.OrderStatus status);

    // 根据创建时间范围查找订单
    List<Order> findByCreatedAtBetween(Timestamp startDate, Timestamp endDate);

    // 根据总金额范围查找订单
    List<Order> findByTotalPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // 根据用户 ID 和订单状态查找订单
    List<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status);

    // 根据 ID 列表查找订单
    List<Order> findByIdIn(List<Long> ids);

    // 根据产品 ID 查找订单
    @Query("SELECT o FROM Order o JOIN o.orderItems oi WHERE oi.product.id = :productId")
    List<Order> findByProductId(@Param("productId") Long productId);

}