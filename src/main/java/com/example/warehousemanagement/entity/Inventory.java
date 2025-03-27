package com.example.warehousemanagement.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
