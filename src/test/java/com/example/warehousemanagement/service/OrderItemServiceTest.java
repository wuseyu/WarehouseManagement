package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.OrderItem;
import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.entity.User;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderItemService orderItemService;

    private Order testOrder;
    private Product testProduct;
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

        // 初始化测试商品
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("测试商品");

        // 初始化测试订单项
        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setOrder(testOrder);
        testOrderItem.setProduct(testProduct);
        testOrderItem.setQuantity(2);
        testOrderItem.setUnitPrice(new BigDecimal("50.00"));
        testOrderItem.setBatchNo("BATCH-001");
        testOrderItem.setWarehouseId(1L);
    }

    @Test
    void shouldCreateOrderItem() {
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        OrderItem createdOrderItem = orderItemService.createOrderItem(testOrderItem);

        assertThat(createdOrderItem).isNotNull();
        assertThat(createdOrderItem.getQuantity()).isEqualTo(2);
        assertThat(createdOrderItem.getUnitPrice()).isEqualByComparingTo(new BigDecimal("50.00"));
        verify(orderItemRepository).save(any(OrderItem.class));
    }

    @Test
    void shouldCreateOrderItems() {
        List<OrderItem> orderItems = Arrays.asList(testOrderItem);
        when(orderItemRepository.saveAll(anyList())).thenReturn(orderItems);

        List<OrderItem> createdOrderItems = orderItemService.createOrderItems(orderItems);

        assertThat(createdOrderItems).hasSize(1);
        verify(orderItemRepository).saveAll(anyList());
    }

    @Test
    void shouldUpdateQuantity() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        OrderItem updatedOrderItem = orderItemService.updateQuantity(1L, 3);

        assertThat(updatedOrderItem.getQuantity()).isEqualTo(3);
        verify(orderItemRepository).save(any(OrderItem.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithInvalidQuantity() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));

        assertThatThrownBy(() -> orderItemService.updateQuantity(1L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("数量必须大于0");
    }

    @Test
    void shouldUpdateUnitPrice() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        OrderItem updatedOrderItem = orderItemService.updateUnitPrice(1L, new BigDecimal("60.00"));

        assertThat(updatedOrderItem.getUnitPrice()).isEqualByComparingTo(new BigDecimal("60.00"));
        verify(orderItemRepository).save(any(OrderItem.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithInvalidPrice() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));

        assertThatThrownBy(() ->
                orderItemService.updateUnitPrice(1L, new BigDecimal("0.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("单价必须大于0");
    }

    @Test
    void shouldFindByOrder() {
        List<OrderItem> orderItems = Arrays.asList(testOrderItem);
        when(orderItemRepository.findByOrder(testOrder)).thenReturn(orderItems);

        List<OrderItem> foundOrderItems = orderItemService.findByOrder(testOrder);

        assertThat(foundOrderItems).hasSize(1);
        assertThat(foundOrderItems.get(0).getOrder()).isEqualTo(testOrder);
    }

    @Test
    void shouldFindByProduct() {
        List<OrderItem> orderItems = Arrays.asList(testOrderItem);
        when(orderItemRepository.findByProduct(testProduct)).thenReturn(orderItems);

        List<OrderItem> foundOrderItems = orderItemService.findByProduct(testProduct);

        assertThat(foundOrderItems).hasSize(1);
        assertThat(foundOrderItems.get(0).getProduct()).isEqualTo(testProduct);
    }

    @Test
    void shouldFindByOrderAndProduct() {
        when(orderItemRepository.findByOrderAndProduct(testOrder, testProduct))
                .thenReturn(Optional.of(testOrderItem));

        Optional<OrderItem> foundOrderItem = orderItemService.findByOrderAndProduct(testOrder, testProduct);

        assertThat(foundOrderItem).isPresent();
        assertThat(foundOrderItem.get().getOrder()).isEqualTo(testOrder);
        assertThat(foundOrderItem.get().getProduct()).isEqualTo(testProduct);
    }

    @Test
    void shouldFindByBatchNo() {
        List<OrderItem> orderItems = Arrays.asList(testOrderItem);
        when(orderItemRepository.findByBatchNo("BATCH-001")).thenReturn(orderItems);

        List<OrderItem> foundOrderItems = orderItemService.findByBatchNo("BATCH-001");

        assertThat(foundOrderItems).hasSize(1);
        assertThat(foundOrderItems.get(0).getBatchNo()).isEqualTo("BATCH-001");
    }

    @Test
    void shouldFindByWarehouseId() {
        List<OrderItem> orderItems = Arrays.asList(testOrderItem);
        when(orderItemRepository.findByWarehouseId(1L)).thenReturn(orderItems);

        List<OrderItem> foundOrderItems = orderItemService.findByWarehouseId(1L);

        assertThat(foundOrderItems).hasSize(1);
        assertThat(foundOrderItems.get(0).getWarehouseId()).isEqualTo(1L);
    }

    @Test
    void shouldDeleteOrderItem() {
        when(orderItemRepository.existsById(1L)).thenReturn(true);

        orderItemService.deleteOrderItem(1L);

        verify(orderItemRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingOrderItem() {
        when(orderItemRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> orderItemService.deleteOrderItem(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("订单项不存在: 1");
    }

    @Test
    void shouldThrowExceptionWhenCreatingInvalidOrderItem() {
        OrderItem invalidOrderItem = new OrderItem();
        // 没有设置必要的字段

        assertThatThrownBy(() -> orderItemService.createOrderItem(invalidOrderItem))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("订单不能为空");
    }
}