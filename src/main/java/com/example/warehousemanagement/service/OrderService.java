package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.OrderItem;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.repository.OrderRepository;
import com.example.warehousemanagement.repository.OrderItemRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * 创建订单
     * @param order 订单信息
     * @return 创建的订单
     */
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_CREATE')")
    public Order createOrder(Order order) {
        // 设置初始状态
        order.setStatus(Order.OrderStatus.PENDING);
        // 计算订单总金额
        order.calculateTotalAmount();
        return orderRepository.save(order);
    }

    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param status 新状态
     * @return 更新后的订单
     */
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_UPDATE')")
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + orderId));

        // 验证状态流转的合法性
        validateStatusTransition(order.getStatus(), status);

        order.setStatus(status);
        return orderRepository.save(order);
    }

    /**
     * 添加订单项
     * @param orderId 订单ID
     * @param orderItem 订单项
     * @return 更新后的订单
     */
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_ITEM_CREATE')")
    public Order addOrderItem(Long orderId, OrderItem orderItem) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + orderId));

        // 验证订单状态
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalStateException("只能在待处理状态添加订单项");
        }

        orderItem.setOrder(order);
        order.getOrderItems().add(orderItem);
        order.calculateTotalAmount();

        return orderRepository.save(order);
    }

    /**
     * 根据用户查询订单
     * @param user 用户
     * @return 订单列表
     */
    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_VIEW')")
    public List<Order> findOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    /**
     * 根据状态查询订单
     * @param status 订单状态
     * @return 订单列表
     */
    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_VIEW')")
    public List<Order> findOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * 根据订单号查询订单
     * @param orderNo 订单号
     * @return 订单信息
     */
    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_VIEW')")
    public Optional<Order> findByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }

    /**
     * 确认订单
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_CONFIRM')")
    public Order confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + orderId));
        order.confirm();
        return orderRepository.save(order);
    }

    /**
     * 发货
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_SHIP')")
    public Order shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + orderId));
        order.ship();
        return orderRepository.save(order);
    }

    /**
     * 完成订单
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    @PreAuthorize("@customSecurityExpression.hasPermission('ORDER_COMPLETE')")
    public Order completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + orderId));
        order.complete();
        return orderRepository.save(order);
    }

    /**
     * 验证订单状态流转的合法性
     */
    private void validateStatusTransition(Order.OrderStatus currentStatus, Order.OrderStatus newStatus) {
        switch (currentStatus) {
            case PENDING:
                if (newStatus != Order.OrderStatus.PROCESSING && newStatus != Order.OrderStatus.CANCELLED) {
                    throw new IllegalStateException("待处理订单只能转为处理中或取消状态");
                }
                break;
            case PROCESSING:
                if (newStatus != Order.OrderStatus.SHIPPED && newStatus != Order.OrderStatus.CANCELLED) {
                    throw new IllegalStateException("处理中订单只能转为已发货或取消状态");
                }
                break;
            case SHIPPED:
                if (newStatus != Order.OrderStatus.DELIVERED) {
                    throw new IllegalStateException("已发货订单只能转为已送达状态");
                }
                break;
            case DELIVERED:
            case CANCELLED:
                throw new IllegalStateException("当前状态不能更改");
            default:
                throw new IllegalStateException("未知的订单状态");
        }
    }
}