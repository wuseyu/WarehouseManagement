package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static com.example.warehousemanagement.entity.Vehicle.VehicleStatus.AVAILABLE;
import static com.example.warehousemanagement.entity.Vehicle.VehicleStatus.IN_USE;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @BeforeEach
    public void setUp() {
        vehicleRepository.deleteAll(); // 清空数据库
    }

    private Vehicle createVehicle(Vehicle.VehicleStatus status, String plateNumber, Integer capacity) {
        Vehicle vehicle = new Vehicle();
        vehicle.setStatus(status);
        vehicle.setCapacity(capacity);
        vehicle.setPlateNumber(plateNumber);
        return vehicleRepository.save(vehicle);
    }

    @Test
    public void testFindByStatus() {
        createVehicle(AVAILABLE,"123test",100);
        createVehicle(IN_USE, "456test",50);
        createVehicle(AVAILABLE, "789test",70);

        List<Vehicle> availableVehicles = vehicleRepository.findByStatus(AVAILABLE);

        assertThat(availableVehicles).hasSize(2);
        assertThat(availableVehicles.get(0).getStatus()).isEqualTo(AVAILABLE);
        assertThat(availableVehicles.get(1).getStatus()).isEqualTo(AVAILABLE);
    }

    @Test
    public void testFindByType() {
        createVehicle(AVAILABLE, "123test",100);
        createVehicle(IN_USE, "456test",50);
        createVehicle(AVAILABLE, "789test",100);

        List<Vehicle> trucks = vehicleRepository.findByPlateNumber("456test");

        assertThat(trucks).hasSize(1);
        assertThat(trucks.get(0).getPlateNumber()).isEqualTo("456test");

    }
} 