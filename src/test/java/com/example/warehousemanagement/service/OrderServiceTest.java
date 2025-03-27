package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        order = new Order();
        order.setId(1L);
        order.setUser(new User());
        order.setTotalPrice(new BigDecimal("200.50"));
        order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    void testCreateOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order savedOrder = orderService.createOrder(order);

        assertNotNull(savedOrder);
        assertEquals(1L, savedOrder.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testGetOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order foundOrder = orderService.getOrderById(1L);

        assertNotNull(foundOrder);
        assertEquals(1L, foundOrder.getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllOrders() {
        List<Order> orders = Arrays.asList(order, new Order());
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testUpdateOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order updatedOrder = orderService.updateOrder(1L, order);

        assertNotNull(updatedOrder);
        assertEquals(1L, updatedOrder.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testDeleteOrder() {
        doNothing().when(orderRepository).deleteById(1L);

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetOrdersByUserId() {
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findByUserId(100L)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByUserId(100L);

        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findByUserId(100L);
    }

    @Test
    void testGetOrdersByStatus() {
        Order.OrderStatus status = Order.OrderStatus.PENDING;
        order.setStatus(status);

        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findByStatus(status)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByStatus(status);

        assertEquals(1, result.size());
        assertEquals(status, result.get(0).getStatus());
        verify(orderRepository, times(1)).findByStatus(status);
    }

    @Test
    void testGetOrdersByCreatedAtRange() {
        Timestamp startDate = Timestamp.valueOf("2024-01-01 00:00:00");
        Timestamp endDate = Timestamp.valueOf("2024-12-31 23:59:59");

        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findByCreatedAtBetween(startDate, endDate)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByCreatedAtRange(startDate, endDate);

        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findByCreatedAtBetween(startDate, endDate);
    }

    @Test
    void testGetOrdersByTotalPriceRange() {
        BigDecimal minPrice = new BigDecimal("100.00");
        BigDecimal maxPrice = new BigDecimal("500.00");

        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findByTotalPriceBetween(minPrice, maxPrice)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByTotalPriceRange(minPrice, maxPrice);

        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findByTotalPriceBetween(minPrice, maxPrice);
    }
}
