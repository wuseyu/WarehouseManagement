package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.OrderItem;
import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class OrderItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderItemRepository orderItemRepository;

    // 辅助方法：创建测试用户
    private User createTestUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setPhone("13800138000");
        return entityManager.persist(user);
    }

    // 辅助方法：创建测试商品
    private Product createTestProduct(String name, String category) {
        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setDescription("测试商品描述");
        product.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return entityManager.persist(product);
    }

    // 辅助方法：创建测试订单
    private static int orderCounter = 0;

    private Order createTestOrder(User user) {
        orderCounter++;
        String address = String.format("测试地址-%d", orderCounter);
        Order order = new Order(user, address);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("100.00"));
        return entityManager.persist(order);
    }

    // 辅助方法：创建测试订单项
    private OrderItem createTestOrderItem(Order order, Product product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(new BigDecimal("50.00"));
        orderItem.setBatchNo("BATCH-001");
        orderItem.setWarehouseId(1L);
        return entityManager.persist(orderItem);
    }

    @Test
    void shouldSaveOrderItem() {
        // 准备测试数据
        User user = createTestUser();
        Order order = createTestOrder(user);
        Product product = createTestProduct("测试商品", "测试类别");
        OrderItem orderItem = createTestOrderItem(order, product);

        // 保存订单项
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        // 验证
        assertThat(savedOrderItem.getId()).isNotNull();
        assertThat(savedOrderItem.getOrder()).isEqualTo(order);
        assertThat(savedOrderItem.getProduct()).isEqualTo(product);
        assertThat(savedOrderItem.getQuantity()).isEqualTo(2);
        assertThat(savedOrderItem.getUnitPrice()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void shouldFindByOrder() {
        // 准备测试数据
        User user = createTestUser();
        Order order = createTestOrder(user);
        Product product1 = createTestProduct("商品1", "类别1");
        Product product2 = createTestProduct("商品2", "类别1");

        OrderItem orderItem1 = createTestOrderItem(order, product1);
        OrderItem orderItem2 = createTestOrderItem(order, product2);

        // 查询测试
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

        // 验证
        assertThat(orderItems).hasSize(2);
        assertThat(orderItems).extracting(OrderItem::getProduct)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("商品1", "商品2");
    }

    @Test
    void shouldFindByProduct() {
        // 准备测试数据
        User user = createTestUser();
        Product product = createTestProduct("测试商品", "测试类别");

        // 创建两个不同的订单
        Order order1 = createTestOrder(user);
        OrderItem orderItem1 = createTestOrderItem(order1, product);
        entityManager.persist(orderItem1);

        Order order2 = createTestOrder(user);
        OrderItem orderItem2 = createTestOrderItem(order2, product);
        entityManager.persist(orderItem2);

        // 查询测试
        List<OrderItem> orderItems = orderItemRepository.findByProduct(product);

        // 验证
        assertThat(orderItems).hasSize(2);
        assertThat(orderItems).extracting(OrderItem::getOrder)
                .extracting(Order::getId)
                .containsExactlyInAnyOrder(order1.getId(), order2.getId());
    }

    @Test
    void shouldFindByUnitPriceBetween() {
        // 准备测试数据
        User user = createTestUser();
        Order order = createTestOrder(user);
        Product product1 = createTestProduct("高价商品", "测试类别");
        Product product2 = createTestProduct("低价商品", "测试类别");

        OrderItem orderItem1 = createTestOrderItem(order, product1);
        orderItem1.setUnitPrice(new BigDecimal("100.00"));

        OrderItem orderItem2 = createTestOrderItem(order, product2);
        orderItem2.setUnitPrice(new BigDecimal("20.00"));

        entityManager.persist(orderItem1);
        entityManager.persist(orderItem2);

        // 查询测试
        List<OrderItem> orderItems = orderItemRepository.findByUnitPriceBetween(
                new BigDecimal("30.00"),
                new BigDecimal("120.00")
        );

        // 验证
        assertThat(orderItems).hasSize(1);
        assertThat(orderItems.get(0).getUnitPrice())
                .isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    void shouldFindByOrderAndProduct() {
        // 准备测试数据
        User user = createTestUser();
        Order order = createTestOrder(user);
        Product product = createTestProduct("测试商品", "测试类别");
        OrderItem orderItem = createTestOrderItem(order, product);

        // 查询测试
        Optional<OrderItem> foundOrderItem = orderItemRepository.findByOrderAndProduct(order, product);

        // 验证
        assertThat(foundOrderItem).isPresent();
        assertThat(foundOrderItem.get().getOrder()).isEqualTo(order);
        assertThat(foundOrderItem.get().getProduct()).isEqualTo(product);
    }

    @Test
    void shouldFindByBatchNo() {
        // 准备测试数据
        User user = createTestUser();
        Order order = createTestOrder(user);
        Product product = createTestProduct("测试商品", "测试类别");

        OrderItem orderItem = createTestOrderItem(order, product);
        orderItem.setBatchNo("BATCH-TEST-001");
        entityManager.persist(orderItem);

        // 查询测试
        List<OrderItem> orderItems = orderItemRepository.findByBatchNo("BATCH-TEST-001");

        // 验证
        assertThat(orderItems).hasSize(1);
        assertThat(orderItems.get(0).getBatchNo()).isEqualTo("BATCH-TEST-001");
    }

    @Test
    void shouldFindByWarehouseId() {
        // 准备测试数据
        User user = createTestUser();
        Order order = createTestOrder(user);
        Product product = createTestProduct("测试商品", "测试类别");

        OrderItem orderItem = createTestOrderItem(order, product);
        orderItem.setWarehouseId(100L);
        entityManager.persist(orderItem);

        // 查询测试
        List<OrderItem> orderItems = orderItemRepository.findByWarehouseId(100L);

        // 验证
        assertThat(orderItems).hasSize(1);
        assertThat(orderItems.get(0).getWarehouseId()).isEqualTo(100L);
    }
}