package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.repository.ProductRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 获取所有产品
     * @return 产品列表
     */
    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('PRODUCT_VIEW')")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * 根据 ID 获取产品
     * @param id 产品ID
     * @return 产品
     */
    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('PRODUCT_VIEW')")
    public Optional<Product> getProduct(Long id) {
        return productRepository.findById(id);
    }

    /**
     * 创建产品
     * @param product 产品信息
     * @return 创建的产品
     */
    @PreAuthorize("@customSecurityExpression.hasPermission('PRODUCT_CREATE')")
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * 更新产品
     * @param id 产品ID
     * @param product 产品信息
     * @return 更新后的产品
     */
    @PreAuthorize("@customSecurityExpression.hasPermission('PRODUCT_UPDATE')")
    public Product updateProduct(Long id, Product product) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("产品不存在: " + id);
        }
        product.setId(id);
        return productRepository.save(product);
    }

    /**
     * 删除产品
     * @param id 产品ID
     */
    @PreAuthorize("@customSecurityExpression.hasPermission('PRODUCT_DELETE')")
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("产品不存在: " + id);
        }
        productRepository.deleteById(id);
    }

    /**
     * 搜索产品
     * @param name 产品名称
     * @param category 产品分类
     * @return 产品列表
     */
    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('PRODUCT_VIEW')")
    public List<Product> searchProducts(String name, String category) {
        if (name != null) {
            return productRepository.findByNameContaining(name);
        }
        if (category != null) {
            return productRepository.findByCategory(category);
        }
        return productRepository.findAll();
    }
}