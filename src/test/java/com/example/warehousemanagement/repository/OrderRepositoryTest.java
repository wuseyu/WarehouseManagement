package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.Order.OrderStatus;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderRepositoryTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService; // 这里可以直接使用 OrderService 进行测试

    private Order order;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        order = new Order();
        order.setId(1L);
        order.setUser(new User());
        order.setTotalPrice(BigDecimal.valueOf(99.99));
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    public void testCreateOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order createdOrder = orderService.createOrder(order);
        assertNotNull(createdOrder);
        assertEquals(OrderStatus.PENDING, createdOrder.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testGetOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order foundOrder = orderService.getOrderById(1L);
        assertNotNull(foundOrder);
        assertEquals(OrderStatus.PENDING, foundOrder.getStatus());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetAllOrders() {
        Order order2 = new Order();
        order2.setId(2L);
        order2.setTotalPrice(BigDecimal.valueOf(49.99));
        order2.setStatus(OrderStatus.PENDING);

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order, order2));

        List<Order> orders = orderService.getAllOrders();
        assertEquals(2, orders.size());
        assertEquals(OrderStatus.PENDING, orders.get(0).getStatus());
        assertEquals(OrderStatus.PENDING, orders.get(1).getStatus());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateOrder() {
        order.setStatus(OrderStatus.PENDING);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order updatedOrder = orderService.updateOrder(1L, order);
        assertNotNull(updatedOrder);
        assertEquals(OrderStatus.PENDING, updatedOrder.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testDeleteOrder() {
        doNothing().when(orderRepository).deleteById(1L);

        orderService.deleteOrder(1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testGetOrdersByUserId() {
        when(orderRepository.findByUserId(1L)).thenReturn(Arrays.asList(order));

        List<Order> foundOrders = orderService.getOrdersByUserId(1L);
        assertEquals(1, foundOrders.size());
        assertEquals(OrderStatus.PENDING, foundOrders.get(0).getStatus());
        verify(orderRepository, times(1)).findByUserId(1L);
    }

    @Test
    public void testGetOrdersByStatus() {
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(Arrays.asList(order));

        List<Order> foundOrders = orderService.getOrdersByStatus(OrderStatus.PENDING);
        assertEquals(1, foundOrders.size());
        assertEquals(OrderStatus.PENDING, foundOrders.get(0).getStatus());
        verify(orderRepository, times(1)).findByStatus(OrderStatus.PENDING);
    }
}