package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();

        order = new Order();
        order.setId(1L);
        order.setUser(new User());
        order.setTotalPrice(new BigDecimal("200.50"));
        order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    void testGetAllOrders() throws Exception {
        List<Order> orders = Arrays.asList(order, new Order());
        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void testGetOrderById_Success() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    void testGetOrderById_NotFound() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    void testCreateOrder() throws Exception {
        when(orderService.createOrder(any(Order.class))).thenReturn(order);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    @Test
    void testUpdateOrder() throws Exception {
        when(orderService.updateOrder(eq(1L), any(Order.class))).thenReturn(order);

        mockMvc.perform(put("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(orderService, times(1)).updateOrder(eq(1L), any(Order.class));
    }

    @Test
    void testDeleteOrder() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(1L);
    }

    @Test
    void testGetOrdersByUserId() throws Exception {
        List<Order> orders = Arrays.asList(order);
        when(orderService.getOrdersByUserId(100L)).thenReturn(orders);

        mockMvc.perform(get("/api/orders/user/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(orderService, times(1)).getOrdersByUserId(100L);
    }

    @Test
    void testGetOrdersByStatus() throws Exception {
        Order.OrderStatus status = Order.OrderStatus.PENDING;
        order.setStatus(status);
        List<Order> orders = Arrays.asList(order);

        when(orderService.getOrdersByStatus(status)).thenReturn(orders);

        mockMvc.perform(get("/api/orders/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(orderService, times(1)).getOrdersByStatus(status);
    }

    @Test
    void testGetOrdersByCreatedAtRange() throws Exception {
        Timestamp startDate = Timestamp.valueOf("2024-01-01 00:00:00");
        Timestamp endDate = Timestamp.valueOf("2024-12-31 23:59:59");

        List<Order> orders = Arrays.asList(order);
        when(orderService.getOrdersByCreatedAtRange(startDate, endDate)).thenReturn(orders);

        mockMvc.perform(get("/api/orders/created")
                        .param("startDate", "2024-01-01 00:00:00")
                        .param("endDate", "2024-12-31 23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(orderService, times(1)).getOrdersByCreatedAtRange(startDate, endDate);
    }

    @Test
    void testGetOrdersByTotalPriceRange() throws Exception {
        BigDecimal minPrice = new BigDecimal("100.00");
        BigDecimal maxPrice = new BigDecimal("500.00");

        List<Order> orders = Arrays.asList(order);
        when(orderService.getOrdersByTotalPriceRange(minPrice, maxPrice)).thenReturn(orders);

        mockMvc.perform(get("/api/orders/totalPrice")
                        .param("minPrice", "100.00")
                        .param("maxPrice", "500.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(orderService, times(1)).getOrdersByTotalPriceRange(minPrice, maxPrice);
    }
}
