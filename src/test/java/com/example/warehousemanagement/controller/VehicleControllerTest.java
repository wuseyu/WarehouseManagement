package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Vehicle;
import com.example.warehousemanagement.entity.Task;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleControllerTest {

    @Mock
    private VehicleService vehicleService;
    
    @InjectMocks
    private VehicleController vehicleController;

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
    @WithMockUser(authorities = "VEHICLE_CREATE")
    void createVehicle_ShouldReturnCreated() {
        when(vehicleService.createVehicle(any(Vehicle.class))).thenReturn(testVehicle);

        ResponseEntity<Vehicle> response = vehicleController.createVehicle(new Vehicle());
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("京A12345", response.getBody().getPlateNumber());
    }

    @Test
    @WithMockUser(authorities = "VEHICLE_VIEW")
    void getVehicleById_ShouldReturnVehicle() {
        when(vehicleService.getVehicleById(1L)).thenReturn(testVehicle);

        ResponseEntity<Vehicle> response = vehicleController.getVehicleById(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("京A12345", response.getBody().getPlateNumber());
    }

    @Test
    @WithMockUser(authorities = "VEHICLE_VIEW")
    void getVehicleById_NotFound() {
        when(vehicleService.getVehicleById(1L)).thenThrow(new NotFoundException("Vehicle not found"));

        assertThrows(NotFoundException.class, () -> 
            vehicleController.getVehicleById(1L)
        );
    }

    @Test
    @WithMockUser(authorities = "VEHICLE_VIEW")
    void getAllVehicles_ShouldReturnList() {
        when(vehicleService.getAllVehicles()).thenReturn(Collections.singletonList(testVehicle));

        ResponseEntity<List<Vehicle>> response = vehicleController.getAllVehicles();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("京A12345", response.getBody().get(0).getPlateNumber());
    }

    @Test
    @WithMockUser(authorities = "VEHICLE_UPDATE")
    void updateVehicle_ShouldReturnUpdated() {
        when(vehicleService.updateVehicle(eq(1L), any(Vehicle.class))).thenReturn(testVehicle);

        ResponseEntity<Vehicle> response = vehicleController.updateVehicle(1L, new Vehicle());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("京A12345", response.getBody().getPlateNumber());
    }

    @Test
    @WithMockUser(authorities = "VEHICLE_DELETE")
    void deleteVehicle_ShouldReturnNoContent() {
        doNothing().when(vehicleService).deleteVehicle(1L);

        ResponseEntity<Void> response = vehicleController.deleteVehicle(1L);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(vehicleService).deleteVehicle(1L);
    }

    @Test
    @WithMockUser(authorities = "VEHICLE_VIEW")
    void getVehiclesByStatus_ShouldReturnList() {
        when(vehicleService.getVehiclesByStatus(Vehicle.VehicleStatus.AVAILABLE))
                .thenReturn(Collections.singletonList(testVehicle));

        ResponseEntity<List<Vehicle>> response = vehicleController.getVehiclesByStatus(Vehicle.VehicleStatus.AVAILABLE);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("京A12345", response.getBody().get(0).getPlateNumber());
    }

    @Test
    @WithMockUser(authorities = "VEHICLE_UPDATE")
    void assignTask_ShouldReturnUpdated() {
        when(vehicleService.assignTask(eq(1L), any(Task.class))).thenReturn(testVehicle);

        ResponseEntity<Vehicle> response = vehicleController.assignTask(1L, testTask);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("京A12345", response.getBody().getPlateNumber());
    }

    @Test
    @WithMockUser(authorities = "VEHICLE_UPDATE")
    void setMaintenance_ShouldReturnUpdated() {
        when(vehicleService.setMaintenance(1L)).thenReturn(testVehicle);

        ResponseEntity<Vehicle> response = vehicleController.setMaintenance(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("京A12345", response.getBody().getPlateNumber());
    }

    @Test
    @WithMockUser(authorities = "VEHICLE_UPDATE")
    void setAvailable_ShouldReturnUpdated() {
        when(vehicleService.setAvailable(1L)).thenReturn(testVehicle);

        ResponseEntity<Vehicle> response = vehicleController.setAvailable(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("京A12345", response.getBody().getPlateNumber());
    }

    @Test
    void handleNotFoundException_ShouldReturnNotFound() {
        NotFoundException ex = new NotFoundException("Vehicle not found");
        
        ResponseEntity<String> response = vehicleController.handleNotFoundException(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Vehicle not found", response.getBody());
    }
    
    @Test
    void handleIllegalStateException_ShouldReturnBadRequest() {
        IllegalStateException ex = new IllegalStateException("Vehicle already in use");
        
        ResponseEntity<String> response = vehicleController.handleIllegalStateException(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Vehicle already in use", response.getBody());
    }
}