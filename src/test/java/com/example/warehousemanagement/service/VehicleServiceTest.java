package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Vehicle;
import com.example.warehousemanagement.entity.Task;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VehicleServiceTest {

    @InjectMocks
    private VehicleService vehicleService;

    @Mock
    private VehicleRepository vehicleRepository;

    private Vehicle testVehicle;
    private Task testTask;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setPlateNumber("京A12345");
        testVehicle.setDriverName("测试司机");
        testVehicle.setCapacity(1000);
        testVehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
        testVehicle.setCurrentLocation("北京");
        testVehicle.setAssignedTasks(new ArrayList<>());

        testTask = new Task();
        testTask.setId(1L);
        testTask.setDescription("测试任务");
    }

    @Test
    public void testCreateVehicle() {
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        Vehicle createdVehicle = vehicleService.createVehicle(testVehicle);

        assertThat(createdVehicle).isNotNull();
        assertThat(createdVehicle.getPlateNumber()).isEqualTo("京A12345");
        verify(vehicleRepository, times(1)).save(testVehicle);
    }

    @Test
    public void testGetVehicleById() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

        Vehicle foundVehicle = vehicleService.getVehicleById(1L);

        assertThat(foundVehicle).isNotNull();
        assertThat(foundVehicle.getId()).isEqualTo(1L);
        verify(vehicleRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetVehicleByIdNotFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.getVehicleById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Vehicle not found");
    }

    @Test
    public void testGetAllVehicles() {
        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(testVehicle));

        List<Vehicle> vehicles = vehicleService.getAllVehicles();

        assertThat(vehicles).isNotEmpty();
        assertThat(vehicles.size()).isEqualTo(1);
        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateVehicle() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        Vehicle updatedVehicle = vehicleService.updateVehicle(1L, testVehicle);

        assertThat(updatedVehicle).isNotNull();
        assertThat(updatedVehicle.getPlateNumber()).isEqualTo("京A12345");
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    public void testDeleteVehicle() {
        doNothing().when(vehicleRepository).deleteById(1L);

        vehicleService.deleteVehicle(1L);

        verify(vehicleRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testGetVehiclesByStatus() {
        when(vehicleRepository.findByStatus(Vehicle.VehicleStatus.AVAILABLE))
                .thenReturn(Arrays.asList(testVehicle));

        List<Vehicle> vehicles = vehicleService.getVehiclesByStatus(Vehicle.VehicleStatus.AVAILABLE);

        assertThat(vehicles).isNotEmpty();
        assertThat(vehicles.size()).isEqualTo(1);
        verify(vehicleRepository, times(1)).findByStatus(Vehicle.VehicleStatus.AVAILABLE);
    }

    @Test
    public void testAssignTask() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        Vehicle updatedVehicle = vehicleService.assignTask(1L, testTask);

        assertThat(updatedVehicle).isNotNull();
        assertThat(updatedVehicle.getStatus()).isEqualTo(Vehicle.VehicleStatus.IN_USE);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    public void testSetMaintenance() {
        testVehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        Vehicle updatedVehicle = vehicleService.setMaintenance(1L);

        assertThat(updatedVehicle).isNotNull();
        assertThat(updatedVehicle.getStatus()).isEqualTo(Vehicle.VehicleStatus.MAINTENANCE);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    public void testSetAvailable() {
        testVehicle.setStatus(Vehicle.VehicleStatus.MAINTENANCE);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        Vehicle updatedVehicle = vehicleService.setAvailable(1L);

        assertThat(updatedVehicle).isNotNull();
        assertThat(updatedVehicle.getStatus()).isEqualTo(Vehicle.VehicleStatus.AVAILABLE);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }
} 