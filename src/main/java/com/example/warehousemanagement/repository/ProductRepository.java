package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 根据产品名称模糊查找
    List<Product> findByNameContaining(String name);

    // 根据类别查找
    List<Product> findByCategory(String category);

    // 根据创建时间范围查找
    List<Product> findByCreatedAtBetween(Timestamp startTime, Timestamp endTime);

    // 检查产品名称是否存在
    boolean existsByName(String name);
}