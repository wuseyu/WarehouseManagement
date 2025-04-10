package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.OrderItem;
import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.service.OrderItemService;
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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class OrderItemControllerTest {

    @Mock
    private OrderItemService orderItemService;

    private OrderItemController orderItemController;
    private ObjectMapper objectMapper;
    private OrderItem testOrderItem;
    private Order testOrder;
    private Product testProduct;
    private User testUser;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        orderItemController = new OrderItemController(orderItemService);
        
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
    @WithMockUser(authorities = "ORDER_ITEM_CREATE")
    void shouldCreateOrderItem() throws Exception {
        when(orderItemService.createOrderItem(any(OrderItem.class))).thenReturn(testOrderItem);

        ResponseEntity<OrderItem> response = orderItemController.createOrderItem(testOrderItem);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testOrderItem.getId(), response.getBody().getId());
        assertEquals(testOrderItem.getQuantity(), response.getBody().getQuantity());
        assertEquals(0, testOrderItem.getUnitPrice().compareTo(response.getBody().getUnitPrice()));
    }

    @Test
    @WithMockUser(authorities = "ORDER_ITEM_CREATE")
    void shouldCreateOrderItems() throws Exception {
        when(orderItemService.createOrderItems(any())).thenReturn(Arrays.asList(testOrderItem));

        ResponseEntity<List<OrderItem>> response = orderItemController.createOrderItems(Arrays.asList(testOrderItem));
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testOrderItem.getId(), response.getBody().get(0).getId());
    }

    @Test
    @WithMockUser(authorities = "ORDER_ITEM_UPDATE")
    void shouldUpdateQuantity() throws Exception {
        when(orderItemService.updateQuantity(1L, 3)).thenReturn(testOrderItem);

        ResponseEntity<OrderItem> response = orderItemController.updateQuantity(1L, 3);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testOrderItem.getQuantity(), response.getBody().getQuantity());
    }

    @Test
    @WithMockUser(authorities = "ORDER_ITEM_UPDATE")
    void shouldUpdateUnitPrice() throws Exception {
        when(orderItemService.updateUnitPrice(1L, new BigDecimal("60.00")))
                .thenReturn(testOrderItem);

        ResponseEntity<OrderItem> response = orderItemController.updateUnitPrice(1L, new BigDecimal("60.00"));
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0, testOrderItem.getUnitPrice().compareTo(response.getBody().getUnitPrice()));
    }

    @Test
    @WithMockUser(authorities = "ORDER_ITEM_VIEW")
    void shouldFindByOrder() throws Exception {
        when(orderItemService.findByOrder(any(Order.class)))
                .thenReturn(Arrays.asList(testOrderItem));
                
        // 假设有一个根据订单ID查找订单项的方法
        ResponseEntity<List<OrderItem>> response = orderItemController.findByOrder(1L);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testOrderItem.getId(), response.getBody().get(0).getId());
    }

    @Test
    @WithMockUser(authorities = "ORDER_ITEM_VIEW")
    void shouldFindByProduct() throws Exception {
        when(orderItemService.findByProduct(any(Product.class)))
                .thenReturn(Arrays.asList(testOrderItem));

        // 假设有一个根据产品ID查找订单项的方法
        ResponseEntity<List<OrderItem>> response = orderItemController.findByProduct(1L);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testOrderItem.getId(), response.getBody().get(0).getId());
    }

    @Test
    @WithMockUser(authorities = "ORDER_ITEM_VIEW")
    void shouldFindByOrderAndProduct() throws Exception {
        when(orderItemService.findByOrderAndProduct(any(Order.class), any(Product.class)))
                .thenReturn(Optional.of(testOrderItem));

        ResponseEntity<?> response = orderItemController.findByOrderAndProduct(1L, 1L);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testOrderItem.getId(), ((OrderItem)response.getBody()).getId());
    }

    @Test
    @WithMockUser(authorities = "ORDER_ITEM_VIEW")
    void shouldReturn404WhenOrderItemNotFound() throws Exception {
        when(orderItemService.findByOrderAndProduct(any(Order.class), any(Product.class)))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = orderItemController.findByOrderAndProduct(1L, 1L);
        
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    @WithMockUser(authorities = "ORDER_ITEM_VIEW")
    void shouldFindByBatchNo() throws Exception {
        when(orderItemService.findByBatchNo("BATCH-001"))
                .thenReturn(Arrays.asList(testOrderItem));

        ResponseEntity<List<OrderItem>> response = orderItemController.findByBatchNo("BATCH-001");
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testOrderItem.getId(), response.getBody().get(0).getId());
        assertEquals("BATCH-001", response.getBody().get(0).getBatchNo());
    }

    @Test
    @WithMockUser(authorities = "ORDER_ITEM_VIEW")
    void shouldFindByWarehouseId() throws Exception {
        when(orderItemService.findByWarehouseId(1L))
                .thenReturn(Arrays.asList(testOrderItem));

        ResponseEntity<List<OrderItem>> response = orderItemController.findByWarehouseId(1L);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testOrderItem.getId(), response.getBody().get(0).getId());
        assertEquals(1L, response.getBody().get(0).getWarehouseId());
    }

    @Test
    @WithMockUser(authorities = "ORDER_ITEM_DELETE")
    void shouldDeleteOrderItem() throws Exception {
        ResponseEntity<Void> response = orderItemController.deleteOrderItem(1L);
        
        assertEquals(200, response.getStatusCodeValue());
        verify(orderItemService).deleteOrderItem(1L);
    }

    @Test
    @WithMockUser(authorities = "ORDER_ITEM_UPDATE")
    void shouldHandleIllegalArgumentException() throws Exception {
        when(orderItemService.updateQuantity(1L, 0))
                .thenThrow(new IllegalArgumentException("数量必须大于0"));
                
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderItemController.updateQuantity(1L, 0);
        });
        
        assertEquals("数量必须大于0", exception.getMessage());
    }
}