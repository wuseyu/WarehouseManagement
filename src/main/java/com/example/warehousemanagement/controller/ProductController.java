package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

// 直接使用 Repository 进行基础 CRUD
@RestController
@RequestMapping("/api/products")
@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SUPPLIER')")  // 只有超级管理员和供应商可以访问产品相关接口
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 获取所有产品
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        logger.info("【产品控制器】获取所有产品");
        return ResponseEntity.ok(productRepository.findAll());
    }

    /**
     * 根据 ID 获取产品
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        logger.info("【产品控制器】获取产品详情，ID: {}", id);
        return productRepository.findById(id)
                .map(product -> {
                    logger.info("【产品控制器】找到产品: {}", product.getName());
                    return ResponseEntity.ok(product);
                })
                .orElseGet(() -> {
                    logger.warn("【产品控制器】未找到产品，ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * 创建产品
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product product) {
        logger.info("【产品控制器】创建新产品: {}, 分类: {}", product.getName(), product.getCategory());
        
        // 设置创建时间
        if (product.getCreatedAt() == null) {
            product.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            logger.info("【产品控制器】设置产品创建时间: {}", product.getCreatedAt());
        }
        
        Product savedProduct = productRepository.save(product);
        logger.info("【产品控制器】产品创建成功，ID: {}", savedProduct.getId());
        return ResponseEntity.ok(savedProduct);
    }

    /**
     * 更新产品
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody @Valid Product product) {
        logger.info("【产品控制器】更新产品，ID: {}", id);
        return productRepository.findById(id)
                .map(existingProduct -> {
                    logger.info("【产品控制器】找到要更新的产品，ID: {}, 现有SKU: {}", id, existingProduct.getSku());
                    
                    // 确保保留原有SKU值
                    if (product.getSku() == null) {
                        product.setSku(existingProduct.getSku());
                        logger.info("【产品控制器】保留原有SKU: {}", existingProduct.getSku());
                    }
                    
                    product.setId(id);
                    Product updatedProduct = productRepository.save(product);
                    logger.info("【产品控制器】产品更新成功，ID: {}", updatedProduct.getId());
                    return ResponseEntity.ok(updatedProduct);
                })
                .orElseGet(() -> {
                    logger.warn("【产品控制器】未找到要更新的产品，ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * 删除产品
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("【产品控制器】删除产品，ID: {}", id);
        if (!productRepository.existsById(id)) {
            logger.warn("【产品控制器】未找到要删除的产品，ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        logger.info("【产品控制器】产品删除成功，ID: {}", id);
        return ResponseEntity.ok().build();
    }

    /**
     * 搜索产品
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {
        logger.info("【产品控制器】搜索产品，名称: {}, 分类: {}", name, category);
        
        List<Product> products;
        if (name != null) {
            products = productRepository.findByNameContaining(name);
            logger.info("【产品控制器】按名称搜索产品，结果数量: {}", products.size());
        } else if (category != null) {
            products = productRepository.findByCategory(category);
            logger.info("【产品控制器】按分类搜索产品，结果数量: {}", products.size());
        } else {
            products = productRepository.findAll();
            logger.info("【产品控制器】获取所有产品，结果数量: {}", products.size());
        }
        
        return ResponseEntity.ok(products);
    }
}