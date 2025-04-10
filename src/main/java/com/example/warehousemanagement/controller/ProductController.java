package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

// 直接使用 Repository 进行基础 CRUD
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 获取所有产品
     */
    @GetMapping
    @PreAuthorize("@customSecurityExpression.hasPermission('PRODUCT_VIEW')")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    /**
     * 根据 ID 获取产品
     */
    @GetMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.hasPermission('PRODUCT_VIEW')")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建产品
     */
    @PostMapping
    @PreAuthorize("@customSecurityExpression.hasPermission('PRODUCT_CREATE')")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product product) {
        return ResponseEntity.ok(productRepository.save(product));
    }

    /**
     * 更新产品
     */
    @PutMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.hasPermission('PRODUCT_UPDATE')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody @Valid Product product) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        product.setId(id);
        return ResponseEntity.ok(productRepository.save(product));
    }

    /**
     * 删除产品
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.hasPermission('PRODUCT_DELETE')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 搜索产品
     */
    @GetMapping("/search")
    @PreAuthorize("@customSecurityExpression.hasPermission('PRODUCT_VIEW')")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {
        if (name != null) {
            return ResponseEntity.ok(productRepository.findByNameContaining(name));
        }
        if (category != null) {
            return ResponseEntity.ok(productRepository.findByCategory(category));
        }
        return ResponseEntity.ok(productRepository.findAll());
    }
}