package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private User createTestUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setPhone("13800138000");
        return entityManager.persist(user);
    }

    private int orderCounter = 0;

    private Order createTestOrder(User user) {
        orderCounter++;
        String address = String.format("测试配送地址-%d", orderCounter);
        Order order = new Order(user, address);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("100.00"));
        return entityManager.persist(order);
    }

    @Test
    void shouldSaveOrder() {
        // 准备测试数据
        User user = createTestUser();
        Order order = createTestOrder(user);

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        // 验证
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getOrderNo()).isNotNull();
        assertThat(savedOrder.getUser()).isEqualTo(user);
        assertThat(savedOrder.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
    }

    @Test
    void shouldFindByUserAndStatus() {
        // 准备测试数据
        User user = createTestUser();
        Order order1 = createTestOrder(user);
        Order order2 = createTestOrder(user);
        order2.setStatus(Order.OrderStatus.PROCESSING);

        entityManager.persist(order1);
        entityManager.persist(order2);

        // 查询测试
        List<Order> pendingOrders = orderRepository.findByUserAndStatus(user, Order.OrderStatus.PENDING);
        List<Order> processingOrders = orderRepository.findByUserAndStatus(user, Order.OrderStatus.PROCESSING);

        // 验证
        assertThat(pendingOrders).hasSize(1);
        assertThat(processingOrders).hasSize(1);
        assertThat(pendingOrders.get(0).getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        assertThat(processingOrders.get(0).getStatus()).isEqualTo(Order.OrderStatus.PROCESSING);
    }

    @Test
    void shouldFindByOrderNo() {
        // 准备测试数据
        User user = createTestUser();
        Order order = createTestOrder(user);
        entityManager.persist(order);
        String orderNo = order.getOrderNo();

        // 查询测试
        Optional<Order> foundOrder = orderRepository.findByOrderNo(orderNo);

        // 验证
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getOrderNo()).isEqualTo(orderNo);
    }

    @Test
    void shouldFindByStatus() {
        // 准备测试数据
        User user = createTestUser();
        Order order1 = createTestOrder(user);
        Order order2 = createTestOrder(user);
        order2.setStatus(Order.OrderStatus.SHIPPED);

        entityManager.persist(order1);
        entityManager.persist(order2);

        // 查询测试
        List<Order> pendingOrders = orderRepository.findByStatus(Order.OrderStatus.PENDING);
        List<Order> shippedOrders = orderRepository.findByStatus(Order.OrderStatus.SHIPPED);

        // 验证
        assertThat(pendingOrders).hasSize(1);
        assertThat(shippedOrders).hasSize(1);
    }

    @Test
    void shouldFindByCreatedAtBetween() {
        // 准备测试数据
        User user = createTestUser();
        Order order = createTestOrder(user);
        entityManager.persist(order);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime tomorrow = now.plusDays(1);

        // 查询测试
        List<Order> orders = orderRepository.findByCreatedAtBetween(
                Timestamp.valueOf(yesterday),
                Timestamp.valueOf(tomorrow)
        );

        // 验证
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getId()).isEqualTo(order.getId());
    }

    @Test
    void shouldFindByUser() {
        // 准备测试数据
        User user1 = createTestUser();
        User user2 = new User();
        user2.setUsername("testUser2");
        user2.setPassword("password123");
        user2.setEmail("test2@example.com");
        user2.setPhone("13800138001");
        entityManager.persist(user2);

        Order order1 = createTestOrder(user1);
        Order order2 = createTestOrder(user2);
        entityManager.persist(order1);
        entityManager.persist(order2);

        // 查询测试
        List<Order> user1Orders = orderRepository.findByUser(user1);
        List<Order> user2Orders = orderRepository.findByUser(user2);

        // 验证
        assertThat(user1Orders).hasSize(1);
        assertThat(user2Orders).hasSize(1);
        assertThat(user1Orders.get(0).getUser()).isEqualTo(user1);
        assertThat(user2Orders.get(0).getUser()).isEqualTo(user2);
    }
}