package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.OrderItem;
import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.service.OrderService;
import com.example.warehousemanagement.service.ProductService;
import com.example.warehousemanagement.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;

    public OrderController(OrderService orderService, UserService userService, ProductService productService) {
        this.orderService = orderService;
        this.userService = userService;
        this.productService = productService;
    }

    /**
     * 创建订单 - 使用Map接收数据，避免Jackson序列化/反序列化问题
     */
    @PostMapping
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_CREATE')")
    public ResponseEntity<Order> createOrder(@RequestBody Map<String, Object> orderData) {
        // 1. 从请求数据中提取信息
        Long userId = Long.valueOf(orderData.get("userId").toString());
        String deliveryAddress = (String) orderData.get("deliveryAddress");
        List<Map<String, Object>> orderItemsData = (List<Map<String, Object>>) orderData.get("orderItems");
        
        // 2. 获取用户信息
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
        
        // 3. 创建订单对象
        Order order = new Order(user, deliveryAddress);
        
        // 4. 创建订单项
        List<OrderItem> orderItems = new ArrayList<>();
        for (Map<String, Object> itemData : orderItemsData) {
            Long productId = Long.valueOf(itemData.get("productId").toString());
            Integer quantity = Integer.valueOf(itemData.get("quantity").toString());
            
            Product product = productService.getProduct(productId)
                    .orElseThrow(() -> new IllegalArgumentException("产品不存在: " + productId));
            
            OrderItem orderItem = new OrderItem(product, quantity);
            
            // 设置可选字段
            if (itemData.containsKey("warehouseId") && itemData.get("warehouseId") != null) {
                orderItem.setWarehouseId(Long.valueOf(itemData.get("warehouseId").toString()));
            }
            
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }
        
        order.setOrderItems(orderItems);
        
        // 5. 保存订单
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    /**
     * 添加订单项
     */
    @PostMapping("/{orderId}/items")
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_ITEM_CREATE')")
    public ResponseEntity<Order> addOrderItem(
            @PathVariable Long orderId,
            @RequestBody @Valid OrderItem orderItem) {
        return ResponseEntity.ok(orderService.addOrderItem(orderId, orderItem));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_VIEW')")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        Order order = orderService.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + orderId));
        return ResponseEntity.ok(order);
    }

    /**
     * 获取用户的所有订单
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_VIEW')")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        // 先获取用户信息
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
        return ResponseEntity.ok(orderService.findOrdersByUser(user));
    }

    /**
     * 获取指定状态的订单
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_VIEW')")
    public ResponseEntity<List<Order>> getOrdersByStatus(
            @PathVariable Order.OrderStatus status) {
        return ResponseEntity.ok(orderService.findOrdersByStatus(status));
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/{orderId}/status")
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_UPDATE')")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam Order.OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    /**
     * 确认订单
     */
    @PostMapping("/{orderId}/confirm")
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_CONFIRM')")
    public ResponseEntity<Order> confirmOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.confirmOrder(orderId));
    }

    /**
     * 发货
     */
    @PostMapping("/{orderId}/ship")
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_SHIP')")
    public ResponseEntity<Order> shipOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.shipOrder(orderId));
    }

    /**
     * 完成订单
     */
    @PostMapping("/{orderId}/complete")
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_COMPLETE')")
    public ResponseEntity<Order> completeOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.completeOrder(orderId));
    }

    /**
     * 根据订单号查询
     */
    @GetMapping("/search")
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_VIEW')")
    public ResponseEntity<Order> findByOrderNo(@RequestParam String orderNo) {
        return orderService.findByOrderNo(orderNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取所有订单
     */
    @GetMapping
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_VIEW')")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
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