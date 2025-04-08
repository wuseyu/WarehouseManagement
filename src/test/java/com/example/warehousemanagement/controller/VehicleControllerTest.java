package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Vehicle;
import com.example.warehousemanagement.entity.Task;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
@AutoConfigureMockMvc
public class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleService vehicleService;

    private Vehicle testVehicle;
    private Task testTask;

    @BeforeEach
    void setup() {
        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setPlateNumber("京A12345");
        testVehicle.setDriverName("测试司机");
        testVehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);

        testTask = new Task();
        testTask.setId(1L);
        testTask.setDescription("测试任务");
    }

    @Test
    void createVehicle_ShouldReturnCreated() throws Exception {
        when(vehicleService.createVehicle(any(Vehicle.class))).thenReturn(testVehicle);

        mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"plateNumber\":\"京A12345\",\"driverName\":\"测试司机\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.plateNumber").value("京A12345"));
    }

    @Test
    void getVehicleById_ShouldReturnVehicle() throws Exception {
        when(vehicleService.getVehicleById(1L)).thenReturn(testVehicle);

        mockMvc.perform(get("/api/vehicles/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plateNumber").value("京A12345"));
    }

    @Test
    void getVehicleById_NotFound() throws Exception {
        when(vehicleService.getVehicleById(1L)).thenThrow(new NotFoundException("Vehicle not found"));

        mockMvc.perform(get("/api/vehicles/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllVehicles_ShouldReturnList() throws Exception {
        when(vehicleService.getAllVehicles()).thenReturn(Collections.singletonList(testVehicle));

        mockMvc.perform(get("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].plateNumber").value("京A12345"));
    }

    @Test
    void updateVehicle_ShouldReturnUpdated() throws Exception {
        when(vehicleService.updateVehicle(eq(1L), any(Vehicle.class))).thenReturn(testVehicle);

        mockMvc.perform(put("/api/vehicles/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"plateNumber\":\"京A12345\",\"driverName\":\"测试司机\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plateNumber").value("京A12345"));
    }

    @Test
    void deleteVehicle_ShouldReturnNoContent() throws Exception {
        doNothing().when(vehicleService).deleteVehicle(1L);

        mockMvc.perform(delete("/api/vehicles/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void getVehiclesByStatus_ShouldReturnList() throws Exception {
        when(vehicleService.getVehiclesByStatus(Vehicle.VehicleStatus.AVAILABLE))
                .thenReturn(Collections.singletonList(testVehicle));

        mockMvc.perform(get("/api/vehicles/status/{status}", "AVAILABLE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].plateNumber").value("京A12345"));
    }

    @Test
    void assignTask_ShouldReturnUpdated() throws Exception {
        when(vehicleService.assignTask(eq(1L), any(Task.class))).thenReturn(testVehicle);

        mockMvc.perform(post("/api/vehicles/{id}/tasks", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"测试任务\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plateNumber").value("京A12345"));
    }

    @Test
    void setMaintenance_ShouldReturnUpdated() throws Exception {
        when(vehicleService.setMaintenance(1L)).thenReturn(testVehicle);

        mockMvc.perform(put("/api/vehicles/{id}/maintenance", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plateNumber").value("京A12345"));
    }

    @Test
    void setAvailable_ShouldReturnUpdated() throws Exception {
        when(vehicleService.setAvailable(1L)).thenReturn(testVehicle);

        mockMvc.perform(put("/api/vehicles/{id}/available", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plateNumber").value("京A12345"));
    }
} 