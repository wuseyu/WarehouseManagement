package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Inventory;
import com.example.warehousemanagement.exception.ConcurrentInventoryException;
import com.example.warehousemanagement.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private Inventory testInventory;

    @BeforeEach
    void setup() {
        // 准备测试数据
        testInventory = new Inventory();
        testInventory.setId(1L);
        testInventory.setQuantity(100);
        testInventory.setVersion(0);
        
        // 设置service
        inventoryController.setInventoryService(inventoryService);
    }

    @Test
    @WithMockUser(authorities = "INVENTORY_VIEW")
    void getInventory_ShouldReturn200() {
        // 配置mock行为
        given(inventoryService.getInventory(anyLong())).willReturn(testInventory);

        // 直接调用控制器方法
        var response = inventoryController.getInventory(1L);
        
        // 验证结果
        assertEquals(testInventory, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @WithMockUser(authorities = "INVENTORY_CREATE")
    void createInventory_ShouldReturn201() {
        // 配置mock行为
        given(inventoryService.createInventory(any(Inventory.class))).willReturn(testInventory);

        // 直接调用控制器方法
        var result = inventoryController.createInventory(new Inventory());
        
        // 验证结果
        assertEquals(testInventory, result);
        assertEquals(1L, result.getId());
        assertEquals(100, result.getQuantity());
    }

    @Test
    @WithMockUser(authorities = "INVENTORY_UPDATE")
    void adjustInventory_ShouldHandleConflict() {
        // 配置mock抛出并发异常
        doThrow(new ConcurrentInventoryException("Version conflict"))
                .when(inventoryService)
                .adjustInventoryQuantity(anyLong(), anyInt(), anyInt());

        // 创建调整请求
        InventoryController.AdjustmentRequest request = new InventoryController.AdjustmentRequest(10, 0);
        
        // 验证异常被抛出并正确处理
        assertThrows(ConcurrentInventoryException.class, () -> 
            inventoryController.adjustQuantity(1L, request)
        );
    }

    @Test
    @WithMockUser(authorities = "INVENTORY_VIEW")
    void listInventories_ShouldReturnPage() {
        // 准备分页结果
        List<Inventory> inventories = new ArrayList<>();
        Page<Inventory> page = new PageImpl<>(inventories);
        
        // 配置mock行为
        given(inventoryService.listInventoryByStatus(any(), any(Pageable.class))).willReturn(page);
        
        // 直接调用控制器方法
        var result = inventoryController.listInventories(null, Pageable.unpaged());
        
        // 验证结果
        assertEquals(page, result);
    }

    @Test
    @WithMockUser(authorities = "INVENTORY_UPDATE")
    void bulkUpdateStatus_ShouldCallService() {
        // 创建批量更新请求
        InventoryController.BulkStatusUpdateRequest request = 
            new InventoryController.BulkStatusUpdateRequest(
                Inventory.InventoryStatus.AVAILABLE,
                Arrays.asList(1L)
            );
        
        // 直接调用控制器方法
        inventoryController.bulkUpdateStatus(request);
        
        // 验证服务方法被调用
        verify(inventoryService).bulkUpdateStatus(
            Inventory.InventoryStatus.AVAILABLE, 
            Arrays.asList(1L)
        );
    }
}