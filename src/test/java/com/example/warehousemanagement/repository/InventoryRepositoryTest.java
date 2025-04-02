package com.example.warehousemanagement.repository;


import com.example.warehousemanagement.entity.Inventory;
import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.entity.Warehouse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.sql.Timestamp;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Inventory createInventory() {
        Warehouse warehouse = new Warehouse();
        warehouse.setName("Main");
        warehouse.setLocation("Shanghai");
        Warehouse savedWarehouse = entityManager.persistFlushFind(warehouse);

        Product product = new Product("A001", "Laptop");
        product.setCategory("Electronics");
        Product savedProduct = entityManager.persistFlushFind(product);
        Inventory inventory = new Inventory(savedWarehouse, savedProduct, 100);
        inventory.setCreatedAt(new Timestamp(System.currentTimeMillis())); // 确保时间设置
        return entityManager.persistFlushFind(inventory);
    }

    @Test
    @Transactional
    void shouldAdjustQuantityWithVersion() {
        Inventory inv = createInventory();


        entityManager.flush();
        int originalVersion = inv.getVersion();

        int affectedRows = inventoryRepository.adjustQuantity(
                inv.getId(),
                10,
                originalVersion);

        entityManager.flush();
        entityManager.clear();

        Inventory updated = inventoryRepository.findById(inv.getId()).get();
        assertThat(affectedRows).isEqualTo(1);
        assertThat(updated.getQuantity()).isEqualTo(110);
        assertThat(updated.getVersion()).isEqualTo(originalVersion + 1);
    }
}