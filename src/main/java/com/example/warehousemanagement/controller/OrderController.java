package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 获取所有订单
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // 根据 ID 获取订单
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    // 创建新订单
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    // 更新订单
    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order order) {
        return orderService.updateOrder(id, order);
    }

    // 删除订单
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    // 根据用户 ID 查找订单
    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUserId(@PathVariable Long userId) {
        return orderService.getOrdersByUserId(userId);
    }

    // 根据订单状态查找订单
    @GetMapping("/status/{status}")
    public List<Order> getOrdersByStatus(@PathVariable Order.OrderStatus status) {
        return orderService.getOrdersByStatus(status);
    }

    // 根据创建时间范围查找订单
    @GetMapping("/created")
    public List<Order> getOrdersByCreatedAtRange(@RequestParam Timestamp startDate, @RequestParam Timestamp endDate) {
        return orderService.getOrdersByCreatedAtRange(startDate, endDate);
    }

    // 根据总金额范围查找订单
    @GetMapping("/totalPrice")
    public List<Order> getOrdersByTotalPriceRange(@RequestParam BigDecimal minPrice, @RequestParam BigDecimal maxPrice) {
        return orderService.getOrdersByTotalPriceRange(minPrice, maxPrice);
    }
}