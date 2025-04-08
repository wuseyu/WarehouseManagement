package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.OrderItem;
import com.example.warehousemanagement.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody @Valid Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    /**
     * 添加订单项
     */
    @PostMapping("/{orderId}/items")
    public ResponseEntity<Order> addOrderItem(
            @PathVariable Long orderId,
            @RequestBody @Valid OrderItem orderItem) {
        return ResponseEntity.ok(orderService.addOrderItem(orderId, orderItem));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        return orderService.findByOrderNo(orderId.toString())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取用户的所有订单
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        // TODO: 需要先获取用户信息
        return ResponseEntity.ok(orderService.findOrdersByUser(null));
    }

    /**
     * 获取指定状态的订单
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(
            @PathVariable Order.OrderStatus status) {
        return ResponseEntity.ok(orderService.findOrdersByStatus(status));
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam Order.OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    /**
     * 确认订单
     */
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<Order> confirmOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.confirmOrder(orderId));
    }

    /**
     * 发货
     */
    @PostMapping("/{orderId}/ship")
    public ResponseEntity<Order> shipOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.shipOrder(orderId));
    }

    /**
     * 完成订单
     */
    @PostMapping("/{orderId}/complete")
    public ResponseEntity<Order> completeOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.completeOrder(orderId));
    }

    /**
     * 根据订单号查询
     */
    @GetMapping("/search")
    public ResponseEntity<Order> findByOrderNo(@RequestParam String orderNo) {
        return orderService.findByOrderNo(orderNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 处理订单相关的异常
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}