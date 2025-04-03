package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Shipment;
import com.example.warehousemanagement.entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ShipmentRepositoryTest {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private TaskRepository taskRepository;

    private Task createTask(String description) {
        Task task = new Task();
        task.setDescription(description);
        task.setStatus(Task.TaskStatus.PENDING);
        task.setDestination("destination");
        task.setScheduledTime(LocalDateTime.now().plusDays(1));
        return taskRepository.save(task);
    }

    private Shipment createShipment(Task task, String status) {
        Shipment shipment = new Shipment();
        shipment.setTask(task);
        shipment.setShipmentStatus(status);
        return shipmentRepository.save(shipment);
    }

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        shipmentRepository.deleteAll();
    }

    @Test
    public void testFindByTaskId() {
        Task task = createTask("Test Task");
        createShipment(task, "待发货");
        createShipment(task, "运输中");

        List<Shipment> shipments = shipmentRepository.findByTaskId(task.getId());

        assertThat(shipments).hasSize(2);
        assertThat(shipments.get(0).getShipmentStatus()).isEqualTo("待发货");
        assertThat(shipments.get(1).getShipmentStatus()).isEqualTo("运输中");
    }

    @Test
    public void testFindByShipmentStatus() {
        Task task = createTask("Test Task");
        createShipment(task, "待发货");
        createShipment(task, "运输中");

        List<Shipment> shipments = shipmentRepository.findByShipmentStatus("待发货");

        assertThat(shipments).hasSize(1);
        assertThat(shipments.get(0).getShipmentStatus()).isEqualTo("待发货");
    }
} 