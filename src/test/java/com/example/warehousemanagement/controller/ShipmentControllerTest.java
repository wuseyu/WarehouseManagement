package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Shipment;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.service.ShipmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ShipmentControllerTest {

    @Mock
    private ShipmentService shipmentService;

    private ShipmentController shipmentController;
    private Shipment testShipment;

    @BeforeEach
    void setup() {
        shipmentController = new ShipmentController(shipmentService);
        
        testShipment = new Shipment();
        testShipment.setId(1L);
        testShipment.setShipmentStatus("待发货");
        testShipment.setShippedTime(LocalDateTime.now());
    }

    @Test
    @WithMockUser(authorities = "SHIPMENT_VIEW")
    void getShipmentById_ShouldReturnShipment() throws Exception {
        when(shipmentService.getShipmentById(1L)).thenReturn(testShipment);

        ResponseEntity<Shipment> response = shipmentController.getShipmentById(1L);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("待发货", response.getBody().getShipmentStatus());
    }

    @Test
    @WithMockUser(authorities = "SHIPMENT_VIEW")
    void getShipmentById_NotFound() throws Exception {
        when(shipmentService.getShipmentById(1L)).thenThrow(new NotFoundException("Shipment not found"));

        assertThrows(NotFoundException.class, () -> shipmentController.getShipmentById(1L));
    }

    @Test
    @WithMockUser(authorities = "SHIPMENT_VIEW")
    void getAllShipments_ShouldReturnShipmentList() throws Exception {
        when(shipmentService.getAllShipments()).thenReturn(Collections.singletonList(testShipment));

        ResponseEntity<List<Shipment>> response = shipmentController.getAllShipments();
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("待发货", response.getBody().get(0).getShipmentStatus());
    }

    @Test
    @WithMockUser(authorities = "SHIPMENT_VIEW")
    void getShipmentsByTaskId_ShouldReturnShipmentList() throws Exception {
        when(shipmentService.getShipmentsByTaskId(1L)).thenReturn(Collections.singletonList(testShipment));

        ResponseEntity<List<Shipment>> response = shipmentController.getShipmentsByTaskId(1L);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("待发货", response.getBody().get(0).getShipmentStatus());
    }

    @Test
    @WithMockUser(authorities = "SHIPMENT_VIEW")
    void getShipmentsByStatus_ShouldReturnShipmentList() throws Exception {
        when(shipmentService.getShipmentsByStatus("待发货")).thenReturn(Collections.singletonList(testShipment));

        ResponseEntity<List<Shipment>> response = shipmentController.getShipmentsByStatus("待发货");
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("待发货", response.getBody().get(0).getShipmentStatus());
    }
}