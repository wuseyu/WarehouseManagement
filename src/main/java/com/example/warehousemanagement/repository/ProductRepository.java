package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 根据名称查找产品
    List<Product> findByName(String name);
    
    // 根据价格区间查找产品
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
}
