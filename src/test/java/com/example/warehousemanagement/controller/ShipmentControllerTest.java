package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Shipment;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.service.ShipmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShipmentController.class)
@AutoConfigureMockMvc
public class ShipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;
//Todo: 'org.springframework.boot.test.mock.mockito.MockBean' 自版本 3.4.0 起已弃用并标记为移除
    @MockBean
    private ShipmentService shipmentService;

    private Shipment testShipment;

    @BeforeEach
    void setup() {
        testShipment = new Shipment();
        testShipment.setId(1L);
        testShipment.setShipmentStatus("待发货");
        testShipment.setShippedTime(LocalDateTime.now());
    }

    @Test
    void createShipment_ShouldReturnCreated() throws Exception {
        when(shipmentService.createShipment(any(Shipment.class))).thenReturn(testShipment);

        mockMvc.perform(post("/api/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"task\":{\"id\":1},\"shipmentStatus\":\"待发货\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shipmentStatus").value("待发货"));
    }

    @Test
    void getShipmentById_ShouldReturnShipment() throws Exception {
        when(shipmentService.getShipmentById(1L)).thenReturn(testShipment);

        mockMvc.perform(get("/api/shipments/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipmentStatus").value("待发货"));
    }

    @Test
    void getShipmentById_NotFound() throws Exception {
        when(shipmentService.getShipmentById(1L)).thenThrow(new NotFoundException("Shipment not found"));

        mockMvc.perform(get("/api/shipments/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllShipments_ShouldReturnShipmentList() throws Exception {
        when(shipmentService.getAllShipments()).thenReturn(Collections.singletonList(testShipment));

        mockMvc.perform(get("/api/shipments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shipmentStatus").value("待发货"));
    }

    @Test
    void deleteShipment_ShouldReturnNoContent() throws Exception {
        doNothing().when(shipmentService).deleteShipment(1L);

        mockMvc.perform(delete("/api/shipments/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void getShipmentsByTaskId_ShouldReturnShipmentList() throws Exception {
        when(shipmentService.getShipmentsByTaskId(1L)).thenReturn(Collections.singletonList(testShipment));

        mockMvc.perform(get("/api/shipments/task/{taskId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shipmentStatus").value("待发货"));
    }

    @Test
    void getShipmentsByStatus_ShouldReturnShipmentList() throws Exception {
        when(shipmentService.getShipmentsByStatus("待发货")).thenReturn(Collections.singletonList(testShipment));

        mockMvc.perform(get("/api/shipments/status/{status}", "待发货")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shipmentStatus").value("待发货"));
    }
} 