package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.OrderItem;
import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.service.OrderItemService;
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
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderItemController.class)
class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderItemService orderItemService;

    private OrderItem testOrderItem;
    private Order testOrder;
    private Product testProduct;
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
    void shouldCreateOrderItem() throws Exception {
        when(orderItemService.createOrderItem(any(OrderItem.class))).thenReturn(testOrderItem);

        mockMvc.perform(post("/api/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrderItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testOrderItem.getId()))
                .andExpect(jsonPath("$.quantity").value(testOrderItem.getQuantity()))
                .andExpect(jsonPath("$.unitPrice").value("50.0"));
    }

    @Test
    void shouldCreateOrderItems() throws Exception {
        when(orderItemService.createOrderItems(any())).thenReturn(Arrays.asList(testOrderItem));

        mockMvc.perform(post("/api/order-items/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(testOrderItem))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testOrderItem.getId()));
    }

    @Test
    void shouldUpdateQuantity() throws Exception {
        when(orderItemService.updateQuantity(1L, 3)).thenReturn(testOrderItem);

        mockMvc.perform(put("/api/order-items/1/quantity")
                        .param("quantity", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(testOrderItem.getQuantity()));
    }

    @Test
    void shouldUpdateUnitPrice() throws Exception {
        when(orderItemService.updateUnitPrice(1L, new BigDecimal("60.00")))
                .thenReturn(testOrderItem);

        mockMvc.perform(put("/api/order-items/1/unit-price")
                        .param("unitPrice", "60.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unitPrice").value("50.0"));
    }

    @Test
    void shouldFindByOrder() throws Exception {
        when(orderItemService.findByOrder(any(Order.class)))
                .thenReturn(Arrays.asList(testOrderItem));

        mockMvc.perform(get("/api/order-items/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testOrderItem.getId()));
    }

    @Test
    void shouldFindByProduct() throws Exception {
        when(orderItemService.findByProduct(any(Product.class)))
                .thenReturn(Arrays.asList(testOrderItem));

        mockMvc.perform(get("/api/order-items/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testOrderItem.getId()));
    }

    @Test
    void shouldFindByOrderAndProduct() throws Exception {
        when(orderItemService.findByOrderAndProduct(any(Order.class), any(Product.class)))
                .thenReturn(Optional.of(testOrderItem));

        mockMvc.perform(get("/api/order-items/search")
                        .param("orderId", "1")
                        .param("productId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testOrderItem.getId()));
    }

    @Test
    void shouldReturn404WhenOrderItemNotFound() throws Exception {
        when(orderItemService.findByOrderAndProduct(any(Order.class), any(Product.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/order-items/search")
                        .param("orderId", "1")
                        .param("productId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindByBatchNo() throws Exception {
        when(orderItemService.findByBatchNo("BATCH-001"))
                .thenReturn(Arrays.asList(testOrderItem));

        mockMvc.perform(get("/api/order-items/batch/BATCH-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testOrderItem.getId()))
                .andExpect(jsonPath("$[0].batchNo").value("BATCH-001"));
    }

    @Test
    void shouldFindByWarehouseId() throws Exception {
        when(orderItemService.findByWarehouseId(1L))
                .thenReturn(Arrays.asList(testOrderItem));

        mockMvc.perform(get("/api/order-items/warehouse/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testOrderItem.getId()))
                .andExpect(jsonPath("$[0].warehouseId").value(1));
    }

    @Test
    void shouldDeleteOrderItem() throws Exception {
        mockMvc.perform(delete("/api/order-items/1"))
                .andExpect(status().isOk());

        verify(orderItemService).deleteOrderItem(1L);
    }

    @Test
    void shouldHandleIllegalArgumentException() throws Exception {
        when(orderItemService.updateQuantity(1L, 0))
                .thenThrow(new IllegalArgumentException("数量必须大于0"));

        mockMvc.perform(put("/api/order-items/1/quantity")
                        .param("quantity", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("数量必须大于0"));
    }
}