package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.OrderItem;
import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.repository.OrderRepository;
import com.example.warehousemanagement.repository.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Order testOrder;
    private Product testProduct;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        // 初始化测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        // 初始化测试订单
        testOrder = new Order(testUser, "测试地址");
        testOrder.setId(1L);
        testOrder.setStatus(Order.OrderStatus.PENDING);

        // 初始化测试商品
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("测试商品");

        // 初始化测试订单项
        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setProduct(testProduct);
        testOrderItem.setQuantity(2);
        testOrderItem.setUnitPrice(new BigDecimal("50.00"));
    }

    @Test
    void shouldCreateOrder() {
        // 准备测试数据
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // 执行测试
        Order createdOrder = orderService.createOrder(testOrder);

        // 验证结果
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldUpdateOrderStatus() {
        // 准备测试数据
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // 执行测试
        Order updatedOrder = orderService.updateOrderStatus(1L, Order.OrderStatus.PROCESSING);

        // 验证结果
        assertThat(updatedOrder.getStatus()).isEqualTo(Order.OrderStatus.PROCESSING);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenInvalidStatusTransition() {
        // 准备测试数据
        testOrder.setStatus(Order.OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // 验证非法状态转换会抛出异常
        assertThatThrownBy(() ->
                orderService.updateOrderStatus(1L, Order.OrderStatus.DELIVERED)
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("待处理订单只能转为处理中或取消状态");
    }

    @Test
    void shouldAddOrderItem() {
        // 准备测试数据
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // 执行测试
        Order updatedOrder = orderService.addOrderItem(1L, testOrderItem);

        // 验证结果
        assertThat(updatedOrder.getOrderItems()).contains(testOrderItem);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenAddingItemToNonPendingOrder() {
        // 准备测试数据
        testOrder.setStatus(Order.OrderStatus.PROCESSING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // 验证非待处理状态添加订单项会抛出异常
        assertThatThrownBy(() ->
                orderService.addOrderItem(1L, testOrderItem)
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("只能在待处理状态添加订单项");
    }

    @Test
    void shouldFindOrdersByUser() {
        // 准备测试数据
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByUser(testUser)).thenReturn(orders);

        // 执行测试
        List<Order> foundOrders = orderService.findOrdersByUser(testUser);

        // 验证结果
        assertThat(foundOrders).hasSize(1);
        assertThat(foundOrders.get(0)).isEqualTo(testOrder);
    }

    @Test
    void shouldFindOrdersByStatus() {
        // 准备测试数据
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByStatus(Order.OrderStatus.PENDING)).thenReturn(orders);

        // 执行测试
        List<Order> foundOrders = orderService.findOrdersByStatus(Order.OrderStatus.PENDING);

        // 验证结果
        assertThat(foundOrders).hasSize(1);
        assertThat(foundOrders.get(0).getStatus()).isEqualTo(Order.OrderStatus.PENDING);
    }

    @Test
    void shouldConfirmOrder() {
        // 准备测试数据
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // 执行测试
        Order confirmedOrder = orderService.confirmOrder(1L);

        // 验证结果
        assertThat(confirmedOrder.getStatus()).isEqualTo(Order.OrderStatus.PROCESSING);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldShipOrder() {
        // 准备测试数据
        testOrder.setStatus(Order.OrderStatus.PROCESSING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // 执行测试
        Order shippedOrder = orderService.shipOrder(1L);

        // 验证结果
        assertThat(shippedOrder.getStatus()).isEqualTo(Order.OrderStatus.SHIPPED);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldCompleteOrder() {
        // 准备测试数据
        testOrder.setStatus(Order.OrderStatus.SHIPPED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // 执行测试
        Order completedOrder = orderService.completeOrder(1L);

        // 验证结果
        assertThat(completedOrder.getStatus()).isEqualTo(Order.OrderStatus.DELIVERED);
        verify(orderRepository).save(any(Order.class));
    }
}