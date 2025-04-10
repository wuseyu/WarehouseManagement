package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Inventory;
import com.example.warehousemanagement.exception.ConcurrentInventoryException;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void adjustInventoryQuantity_Success() {
        // 准备测试数据
        Long inventoryId = 1L;
        when(inventoryRepository.adjustQuantity(inventoryId, 10, 0))
                .thenReturn(1);

        // 执行测试
        inventoryService.adjustInventoryQuantity(inventoryId, 10, 0);

        // 验证调用
        verify(inventoryRepository).adjustQuantity(inventoryId, 10, 0);
    }

    @Test
    void adjustInventoryQuantity_ConcurrentFailure() {
        Long inventoryId = 1L;
        when(inventoryRepository.adjustQuantity(inventoryId, 10, 0))
                .thenReturn(0);

        assertThatThrownBy(() -> inventoryService.adjustInventoryQuantity(inventoryId, 10, 0))
                .isInstanceOf(ConcurrentInventoryException.class)
                .hasMessageContaining("another transaction");
    }

    @Test
    void getInventory_NotFound() {
        Long inventoryId = 999L;
        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.getInventory(inventoryId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void getInventory_Success() {
        Long inventoryId = 1L;
        Inventory inventory = mock(Inventory.class);
        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));
        
        Inventory result = inventoryService.getInventory(inventoryId);
        
        assertThat(result).isEqualTo(inventory);
    }

    @Test
    void bulkUpdateStatus_Success() {
        List<Long> ids = List.of(1L, 2L);
        Inventory.InventoryStatus newStatus = Inventory.InventoryStatus.AVAILABLE;
        
        inventoryService.bulkUpdateStatus(newStatus, ids);
        
        verify(inventoryRepository).bulkUpdateStatus(newStatus, ids);
    }

    @Test
    void listInventoryByStatus_ReturnsPagedData() {
        // 准备分页数据
        PageRequest pageable = PageRequest.of(0, 10);
        Inventory inventory = mock(Inventory.class);
        when(inventoryRepository.findByStatus(any(), any()))
                .thenReturn(new PageImpl<>(List.of(inventory)));

        // 执行测试
        Page<Inventory> result = inventoryService.listInventoryByStatus(
                Inventory.InventoryStatus.AVAILABLE, 
                pageable
        );

        // 验证结果
        assertThat(result.getContent()).hasSize(1);
        verify(inventoryRepository).findByStatus(Inventory.InventoryStatus.AVAILABLE, pageable);
    }
}