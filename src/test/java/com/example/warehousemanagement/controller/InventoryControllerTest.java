package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Inventory;
import com.example.warehousemanagement.exception.ConcurrentInventoryException;
import com.example.warehousemanagement.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private InventoryService inventoryService;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext) {
        InventoryController controller = webApplicationContext.getBean(InventoryController.class);
        // 确保控制器已初始化完成
        controller.setInventoryService(inventoryService);
        // 验证注入结果
        assertThat(controller).extracting("inventoryService").isSameAs(inventoryService);
    }

    @Test
    void getInventory_ShouldReturn200() throws Exception {
        // 准备测试数据
        Inventory mockInventory = new Inventory();
        mockInventory.setId(1L);
        mockInventory.setQuantity(100);
        mockInventory.setVersion(0);

        // 配置mock行为
        given(inventoryService.getInventory(anyLong())).willReturn(mockInventory);

        // 执行请求并验证
        mockMvc.perform(get("/api/inventories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.quantity").value(100));
    }

    @Test
    void adjustInventory_ShouldHandleConflict() throws Exception {
        // 配置mock抛出并发异常
        Mockito.doThrow(new ConcurrentInventoryException("Version conflict"))
                .when(inventoryService)
                .adjustInventoryQuantity(anyLong(), anyInt(), anyInt());

        // 执行请求并验证
        mockMvc.perform(put("/api/inventories/{id}/adjust", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"delta\":10,\"version\":0}"))
                .andExpect(status().isConflict());
    }
}