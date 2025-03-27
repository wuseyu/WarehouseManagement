package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // 创建新产品
    public Product createProduct(Product product) {
        product.setCreatedAt(new Timestamp(System.currentTimeMillis())); // 设置创建时间
        return productRepository.save(product);
    }

    // 通过 ID 获取产品
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // 获取所有产品
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 更新产品
    public Product updateProduct(Long id, Product product) {
        product.setId(id); // 设置产品 ID
        return productRepository.save(product);
    }

    // 删除产品
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // 根据名称查找产品
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    // 根据价格区间查找产品
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }
}