package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.OrderItem;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    private OrderController orderController;
    private ObjectMapper objectMapper;
    private Order testOrder;
    private OrderItem testOrderItem;
    private User testUser;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        orderController = new OrderController(orderService);
        
        // 初始化测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        // 初始化测试订单
        testOrder = new Order(testUser, "测试地址");
        testOrder.setId(1L);
        testOrder.setStatus(Order.OrderStatus.PENDING);
        testOrder.setTotalAmount(new BigDecimal("100.00"));
        testOrder.setOrderNo("ORD-2025-001");

        // 初始化测试订单项
        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setQuantity(2);
        testOrderItem.setUnitPrice(new BigDecimal("50.00"));
    }

    @Test
    @WithMockUser(authorities = "ORDER_CREATE")
    void shouldCreateOrder() throws Exception {
        when(orderService.createOrder(any(Order.class))).thenReturn(testOrder);

        ResponseEntity<Order> response = orderController.createOrder(testOrder);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getUser().getId());
        assertEquals(Order.OrderStatus.PENDING, response.getBody().getStatus());
    }

    @Test
    @WithMockUser(authorities = "ORDER_ITEM_CREATE")
    void shouldAddOrderItem() throws Exception {
        when(orderService.addOrderItem(anyLong(), any(OrderItem.class))).thenReturn(testOrder);

        ResponseEntity<Order> response = orderController.addOrderItem(1L, testOrderItem);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testOrder.getId(), response.getBody().getId());
    }

    @Test
    @WithMockUser(authorities = "ORDER_VIEW")
    void shouldGetOrder() throws Exception {
        when(orderService.findByOrderNo("1")).thenReturn(Optional.of(testOrder));

        ResponseEntity<Order> response = orderController.getOrder(1L);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testOrder.getId(), response.getBody().getId());
        assertEquals(testOrder.getStatus(), response.getBody().getStatus());
    }

    @Test
    @WithMockUser(authorities = "ORDER_VIEW")
    void shouldReturn404WhenOrderNotFound() throws Exception {
        when(orderService.findByOrderNo("1")).thenReturn(Optional.empty());

        ResponseEntity<Order> response = orderController.getOrder(1L);
        
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    @WithMockUser(authorities = "ORDER_VIEW")
    void shouldGetOrdersByStatus() throws Exception {
        when(orderService.findOrdersByStatus(Order.OrderStatus.PENDING))
                .thenReturn(Arrays.asList(testOrder));

        ResponseEntity<List<Order>> response = orderController.getOrdersByStatus(Order.OrderStatus.PENDING);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(testOrder.getId(), response.getBody().get(0).getId());
        assertEquals(testOrder.getStatus(), response.getBody().get(0).getStatus());
    }

    @Test
    @WithMockUser(authorities = "ORDER_UPDATE")
    void shouldUpdateOrderStatus() throws Exception {
        // 模拟更新逻辑
        Order updatedOrder = testOrder;
        updatedOrder.setStatus(Order.OrderStatus.PROCESSING);
        when(orderService.updateOrderStatus(1L, Order.OrderStatus.PROCESSING)).thenReturn(updatedOrder);

        ResponseEntity<Order> response = orderController.updateOrderStatus(1L, Order.OrderStatus.PROCESSING);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Order.OrderStatus.PROCESSING, response.getBody().getStatus());
        assertEquals(1L, response.getBody().getUser().getId());
    }

    @Test
    @WithMockUser(authorities = "ORDER_CONFIRM")
    void shouldConfirmOrder() throws Exception {
        // A模拟确认操作后的订单
        Order confirmedOrder = testOrder;
        confirmedOrder.setStatus(Order.OrderStatus.PROCESSING);
        when(orderService.confirmOrder(1L)).thenReturn(confirmedOrder);

        ResponseEntity<Order> response = orderController.confirmOrder(1L);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Order.OrderStatus.PROCESSING, response.getBody().getStatus());
        assertEquals(1L, response.getBody().getUser().getId());
    }
    
    @Test
    @WithMockUser(authorities = "ORDER_VIEW")
    void shouldFindByOrderNo() throws Exception {
        when(orderService.findByOrderNo("ORD-2025-001")).thenReturn(Optional.of(testOrder));

        ResponseEntity<Order> response = orderController.findByOrderNo("ORD-2025-001");
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testOrder.getOrderNo(), response.getBody().getOrderNo());
    }

    @Test
    @WithMockUser(authorities = "ORDER_CONFIRM")
    void shouldHandleIllegalStateException() throws Exception {
        // 模拟订单处于非法状态（PENDING 不可确认）
        testOrder.setStatus(Order.OrderStatus.PENDING);
        when(orderService.confirmOrder(1L))
                .thenThrow(new IllegalStateException("订单状态错误：必须为 PROCESSING 状态"));

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            orderController.confirmOrder(1L);
        });
        
        assertEquals("订单状态错误：必须为 PROCESSING 状态", exception.getMessage());
    }

    @Test
    @WithMockUser(authorities = "ORDER_CONFIRM")
    void shouldHandleIllegalArgumentException() throws Exception {
        when(orderService.confirmOrder(999L))
                .thenThrow(new IllegalArgumentException("订单 ID 999 不存在"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderController.confirmOrder(999L);
        });
        
        assertEquals("订单 ID 999 不存在", exception.getMessage());
    }
}