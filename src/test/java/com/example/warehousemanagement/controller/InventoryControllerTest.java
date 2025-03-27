package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Inventory;
import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.entity.Warehouse;
import com.example.warehousemanagement.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class InventoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private Inventory inventory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(inventoryController).build();

        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setQuantity(100);
        inventory.setUpdatedAt(new Timestamp(System.currentTimeMillis())); // 设置更新时间

        // 创建 Warehouse 和 Product 对象
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L); // 假设有一个 ID
        inventory.setWarehouse(warehouse);

        Product product = new Product();
        product.setId(1L); // 假设有一个 ID
        inventory.setProduct(product);
    }

    @Test
    public void testGetAllInventories() throws Exception {
        List<Inventory> inventories = Arrays.asList(inventory, new Inventory());
        when(inventoryService.getAllInventories()).thenReturn(inventories);

        mockMvc.perform(get("/api/inventories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(inventoryService, times(1)).getAllInventories();
    }

    @Test
    public void testGetInventoryById_Success() throws Exception {
        when(inventoryService.getInventoryById(1L)).thenReturn(inventory);

        mockMvc.perform(get("/api/inventories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(inventoryService, times(1)).getInventoryById(1L);
    }

    @Test
    public void testGetInventoryById_NotFound() throws Exception {
        when(inventoryService.getInventoryById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/inventories/1"))
                .andExpect(status().isNotFound());

        verify(inventoryService, times(1)).getInventoryById(1L);
    }

    @Test
    public void testCreateInventory() throws Exception {
        when(inventoryService.createInventory(any(Inventory.class))).thenReturn(inventory);

        mockMvc.perform(post("/api/inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(inventory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(inventoryService, times(1)).createInventory(any(Inventory.class));
    }

    @Test
    public void testUpdateInventory() throws Exception {
        when(inventoryService.updateInventory(eq(1L), any(Inventory.class))).thenReturn(inventory);

        mockMvc.perform(put("/api/inventories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(inventory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(inventoryService, times(1)).updateInventory(eq(1L), any(Inventory.class));
    }

    @Test
    public void testDeleteInventory() throws Exception {
        doNothing().when(inventoryService).deleteInventory(1L);

        mockMvc.perform(delete("/api/inventories/1"))
                .andExpect(status().isNoContent());

        verify(inventoryService, times(1)).deleteInventory(1L);
    }
}