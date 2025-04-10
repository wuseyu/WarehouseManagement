package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.OrderItem;
import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.service.OrderItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    /**
     * 创建订单项
     */
    @PostMapping
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_ITEM_CREATE')")
    public ResponseEntity<OrderItem> createOrderItem(@RequestBody @Valid OrderItem orderItem) {
        return ResponseEntity.ok(orderItemService.createOrderItem(orderItem));
    }

    /**
     * 批量创建订单项
     */
    @PostMapping("/batch")
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_ITEM_CREATE')")
    public ResponseEntity<List<OrderItem>> createOrderItems(@RequestBody @Valid List<OrderItem> orderItems) {
        return ResponseEntity.ok(orderItemService.createOrderItems(orderItems));
    }

    /**
     * 更新订单项数量
     */
    @PutMapping("/{id}/quantity")
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_ITEM_UPDATE')")
    public ResponseEntity<OrderItem> updateQuantity(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(orderItemService.updateQuantity(id, quantity));
    }

    /**
     * 更新订单项单价
     */
    @PutMapping("/{id}/unit-price")
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_ITEM_UPDATE')")
    public ResponseEntity<OrderItem> updateUnitPrice(
            @PathVariable Long id,
            @RequestParam BigDecimal unitPrice) {
        return ResponseEntity.ok(orderItemService.updateUnitPrice(id, unitPrice));
    }

    /**
     * 根据订单查询订单项
     */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_ITEM_VIEW')")
    public ResponseEntity<List<OrderItem>> findByOrder(@PathVariable Long orderId) {
        Order order = new Order();
        order.setId(orderId);
        return ResponseEntity.ok(orderItemService.findByOrder(order));
    }

    /**
     * 根据商品查询订单项
     */
    @GetMapping("/product/{productId}")
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_ITEM_VIEW')")
    public ResponseEntity<List<OrderItem>> findByProduct(@PathVariable Long productId) {
        Product product = new Product();
        product.setId(productId);
        return ResponseEntity.ok(orderItemService.findByProduct(product));
    }

    /**
     * 根据订单和商品查询订单项
     */
    @GetMapping("/search")
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_ITEM_VIEW')")
    public ResponseEntity<OrderItem> findByOrderAndProduct(
            @RequestParam Long orderId,
            @RequestParam Long productId) {
        Order order = new Order();
        order.setId(orderId);
        Product product = new Product();
        product.setId(productId);

        return orderItemService.findByOrderAndProduct(order, product)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据批次号查询订单项
     */
    @GetMapping("/batch/{batchNo}")
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_ITEM_VIEW')")
    public ResponseEntity<List<OrderItem>> findByBatchNo(@PathVariable String batchNo) {
        return ResponseEntity.ok(orderItemService.findByBatchNo(batchNo));
    }

    /**
     * 根据仓库ID查询订单项
     */
    @GetMapping("/warehouse/{warehouseId}")
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_ITEM_VIEW')")
    public ResponseEntity<List<OrderItem>> findByWarehouseId(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(orderItemService.findByWarehouseId(warehouseId));
    }

    /**
     * 删除订单项
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_ITEM_DELETE')")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}