package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // 根据用户和订单状态查询
    List<Order> findByUserAndStatus(User user, Order.OrderStatus status);

    // 根据订单号查询
    Optional<Order> findByOrderNo(String orderNo);

    // 根据状态查询
    List<Order> findByStatus(Order.OrderStatus status);

    // 根据创建时间范围查询
    List<Order> findByCreatedAtBetween(Timestamp startTime, Timestamp endTime);

    // 根据用户查询
    List<Order> findByUser(User user);
    
    // 删除所有订单项
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM order_item", nativeQuery = true)
    void deleteAllOrderItems();
    
    // 删除所有订单
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM orders", nativeQuery = true)
    void deleteAllOrders();
}