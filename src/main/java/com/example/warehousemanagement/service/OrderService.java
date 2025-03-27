package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // 创建新订单
    public Order createOrder(Order order) {
        order.setCreatedAt(new Timestamp(System.currentTimeMillis())); // 设置创建时间
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis())); // 设置更新时间
        return orderRepository.save(order);
    }

    // 通过 ID 获取订单
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    // 获取所有订单
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 更新订单
    public Order updateOrder(Long id, Order order) {
        order.setId(id); // 设置订单 ID
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis())); // 更新修改时间
        return orderRepository.save(order);
    }

    // 删除订单
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    // 根据用户 ID 查找订单
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // 根据订单状态查找订单
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    // 根据创建时间范围查找订单
    public List<Order> getOrdersByCreatedAtRange(Timestamp startDate, Timestamp endDate) {
        return orderRepository.findByCreatedAtBetween(startDate, endDate);
    }

    // 根据总金额范围查找订单
    public List<Order> getOrdersByTotalPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return orderRepository.findByTotalPriceBetween(minPrice, maxPrice);
    }
}