package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Shipment;
import com.example.warehousemanagement.entity.Task;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ShipmentServiceTest {

    @InjectMocks
    private ShipmentService shipmentService;

    @Mock
    private ShipmentRepository shipmentRepository;

    private Task task;
    private Shipment shipment;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        task = new Task();
        task.setId(1L);
        task.setDescription("Test Task");
        shipment = new Shipment();
        shipment.setId(1L);
        shipment.setTask(task);
        shipment.setShipmentStatus("待发货");
        shipment.setShippedTime(LocalDateTime.now());
    }

    @Test
    public void testCreateShipment() {
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

        Shipment createdShipment = shipmentService.createShipment(shipment);

        assertThat(createdShipment).isNotNull();
        assertThat(createdShipment.getShipmentStatus()).isEqualTo("待发货");
        verify(shipmentRepository, times(1)).save(shipment);
    }

    @Test
    public void testGetShipmentById() {
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));

        Shipment foundShipment = shipmentService.getShipmentById(1L);

        assertThat(foundShipment).isNotNull();
        assertThat(foundShipment.getId()).isEqualTo(1L);
        verify(shipmentRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetShipmentByIdNotFound() {
        when(shipmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shipmentService.getShipmentById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Shipment not found");
    }

    @Test
    public void testGetAllShipments() {
        when(shipmentRepository.findAll()).thenReturn(Arrays.asList(shipment));

        List<Shipment> shipments = shipmentService.getAllShipments();

        assertThat(shipments).isNotEmpty();
        assertThat(shipments.size()).isEqualTo(1);
        verify(shipmentRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteShipment() {
        doNothing().when(shipmentRepository).deleteById(1L);

        shipmentService.deleteShipment(1L);

        verify(shipmentRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testGetShipmentsByTaskId() {
        when(shipmentRepository.findByTaskId(1L)).thenReturn(Arrays.asList(shipment));

        List<Shipment> shipments = shipmentService.getShipmentsByTaskId(1L);

        assertThat(shipments).isNotEmpty();
        assertThat(shipments.size()).isEqualTo(1);
        verify(shipmentRepository, times(1)).findByTaskId(1L);
    }

    @Test
    public void testGetShipmentsByStatus() {
        when(shipmentRepository.findByShipmentStatus("待发货")).thenReturn(Arrays.asList(shipment));

        List<Shipment> shipments = shipmentService.getShipmentsByStatus("待发货");

        assertThat(shipments).isNotEmpty();
        assertThat(shipments.size()).isEqualTo(1);
        verify(shipmentRepository, times(1)).findByShipmentStatus("待发货");
    }
} 