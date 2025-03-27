package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Inventory;
import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.entity.Warehouse;
import com.example.warehousemanagement.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InventoryRepositoryTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory inventory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        inventory = new Inventory();
        inventory.setId(1L);

        // 创建 Warehouse 和 Product 对象
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L); // 假设有一个 ID

        Product product = new Product();
        product.setId(1L); // 假设有一个 ID

        inventory.setWarehouse(warehouse);
        inventory.setProduct(product);
        inventory.setQuantity(100);
        inventory.setUpdatedAt(new Timestamp(System.currentTimeMillis())); // 设置更新时间
    }

    @Test
    public void testCreateInventory() {
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        Inventory createdInventory = inventoryService.createInventory(inventory);
        assertNotNull(createdInventory);
        assertEquals(1L, createdInventory.getId());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    public void testGetInventoryById() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        Inventory foundInventory = inventoryService.getInventoryById(1L);
        assertNotNull(foundInventory);
        assertEquals(1L, foundInventory.getId());
        verify(inventoryRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetAllInventories() {
        List<Inventory> inventories = Arrays.asList(inventory, new Inventory());
        when(inventoryRepository.findAll()).thenReturn(inventories);

        List<Inventory> result = inventoryService.getAllInventories();
        assertEquals(2, result.size());
        verify(inventoryRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteInventory() {
        doNothing().when(inventoryRepository).deleteById(1L);

        inventoryService.deleteInventory(1L);
        verify(inventoryRepository, times(1)).deleteById(1L);
    }
}