package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.OrderItem;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private Order testOrder;
    private OrderItem testOrderItem;
    private User testUser;

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
        testOrder.setTotalAmount(new BigDecimal("100.00"));
        testOrder.setOrderNo("ORD-2025-001");

        // 初始化测试订单项
        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setQuantity(2);
        testOrderItem.setUnitPrice(new BigDecimal("50.00"));
    }

    @Test
    void shouldCreateOrder() throws Exception {
        when(orderService.createOrder(any(Order.class))).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testOrder.getId()))
                .andExpect(jsonPath("$.status").value(testOrder.getStatus().toString()))
                .andExpect(jsonPath("$.orderNo").value(testOrder.getOrderNo()));
    }

    @Test
    void shouldAddOrderItem() throws Exception {
        when(orderService.addOrderItem(any(Long.class), any(OrderItem.class))).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrderItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testOrder.getId()));
    }

    @Test
    void shouldGetOrder() throws Exception {
        when(orderService.findByOrderNo("1")).thenReturn(Optional.of(testOrder));

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testOrder.getId()))
                .andExpect(jsonPath("$.status").value(testOrder.getStatus().toString()));
    }

    @Test
    void shouldReturn404WhenOrderNotFound() throws Exception {
        when(orderService.findByOrderNo("1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetOrdersByStatus() throws Exception {
        when(orderService.findOrdersByStatus(Order.OrderStatus.PENDING))
                .thenReturn(Arrays.asList(testOrder));

        mockMvc.perform(get("/api/orders/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testOrder.getId()))
                .andExpect(jsonPath("$[0].status").value(testOrder.getStatus().toString()));
    }

    @Test
    void shouldUpdateOrderStatus() throws Exception {
        when(orderService.updateOrderStatus(1L, Order.OrderStatus.PROCESSING)).thenReturn(testOrder);

        mockMvc.perform(put("/api/orders/1/status")
                        .param("status", "PROCESSING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testOrder.getId()));
    }

    @Test
    void shouldConfirmOrder() throws Exception {
        testOrder.setStatus(Order.OrderStatus.PROCESSING);
        when(orderService.confirmOrder(1L)).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders/1/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(testOrder.getStatus().toString()));
    }

    @Test
    void shouldShipOrder() throws Exception {
        testOrder.setStatus(Order.OrderStatus.SHIPPED);
        when(orderService.shipOrder(1L)).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders/1/ship"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(testOrder.getStatus().toString()));
    }

    @Test
    void shouldCompleteOrder() throws Exception {
        testOrder.setStatus(Order.OrderStatus.DELIVERED);
        when(orderService.completeOrder(1L)).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(testOrder.getStatus().toString()));
    }

    @Test
    void shouldFindByOrderNo() throws Exception {
        when(orderService.findByOrderNo("ORD-2025-001")).thenReturn(Optional.of(testOrder));

        mockMvc.perform(get("/api/orders/search")
                        .param("orderNo", "ORD-2025-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNo").value(testOrder.getOrderNo()));
    }

    @Test
    void shouldHandleIllegalStateException() throws Exception {
        when(orderService.confirmOrder(1L))
                .thenThrow(new IllegalStateException("订单状态错误"));

        mockMvc.perform(post("/api/orders/1/confirm"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("订单状态错误"));
    }

    @Test
    void shouldHandleIllegalArgumentException() throws Exception {
        when(orderService.confirmOrder(999L))
                .thenThrow(new IllegalArgumentException("订单不存在"));

        mockMvc.perform(post("/api/orders/999/confirm"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("订单不存在"));
    }
}