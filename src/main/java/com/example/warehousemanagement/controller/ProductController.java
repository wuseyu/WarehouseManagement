package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product product) {
        return ResponseEntity.ok(productRepository.save(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody @Valid Product product) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        product.setId(id);
        return ResponseEntity.ok(productRepository.save(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
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