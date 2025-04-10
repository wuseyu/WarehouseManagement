package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    private ProductRepository productRepository;

    private ProductController productController;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        productController = new ProductController(productRepository);
    }

    private Product createTestProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("测试商品");
        product.setDescription("这是一个测试商品");
        product.setCategory("测试分类");
        product.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return product;
    }

    @Test
    @WithMockUser(authorities = "PRODUCT_CREATE")
    void shouldCreateProduct() throws Exception {
        Product product = createTestProduct();
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ResponseEntity<Product> response = productController.createProduct(product);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(product.getId(), response.getBody().getId());
        assertEquals(product.getName(), response.getBody().getName());
        assertEquals(product.getCategory(), response.getBody().getCategory());
    }

    @Test
    @WithMockUser(authorities = "PRODUCT_VIEW")
    void shouldGetProduct() throws Exception {
        Product product = createTestProduct();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ResponseEntity<Product> response = productController.getProduct(1L);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(product.getId(), response.getBody().getId());
        assertEquals(product.getName(), response.getBody().getName());
    }

    @Test
    @WithMockUser(authorities = "PRODUCT_VIEW")
    void shouldReturn404WhenProductNotFound() throws Exception {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Product> response = productController.getProduct(1L);
        
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    @WithMockUser(authorities = "PRODUCT_VIEW")
    void shouldGetAllProducts() throws Exception {
        Product product1 = createTestProduct();
        Product product2 = createTestProduct();
        product2.setId(2L);
        product2.setName("测试商品2");

        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        ResponseEntity<List<Product>> response = productController.getAllProducts();
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals(product1.getId(), response.getBody().get(0).getId());
        assertEquals(product1.getName(), response.getBody().get(0).getName());
        assertEquals(product2.getId(), response.getBody().get(1).getId());
        assertEquals(product2.getName(), response.getBody().get(1).getName());
    }

    @Test
    @WithMockUser(authorities = "PRODUCT_UPDATE")
    void shouldUpdateProduct() throws Exception {
        Product product = createTestProduct();
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ResponseEntity<Product> response = productController.updateProduct(1L, product);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(product.getId(), response.getBody().getId());
        assertEquals(product.getName(), response.getBody().getName());
    }

    @Test
    @WithMockUser(authorities = "PRODUCT_UPDATE")
    void shouldReturn404WhenUpdatingNonExistingProduct() throws Exception {
        Product product = createTestProduct();
        when(productRepository.existsById(1L)).thenReturn(false);

        ResponseEntity<Product> response = productController.updateProduct(1L, product);
        
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    @WithMockUser(authorities = "PRODUCT_DELETE")
    void shouldDeleteProduct() throws Exception {
        when(productRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<Void> response = productController.deleteProduct(1L);
        
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @WithMockUser(authorities = "PRODUCT_DELETE")
    void shouldReturn404WhenDeletingNonExistingProduct() throws Exception {
        when(productRepository.existsById(1L)).thenReturn(false);

        ResponseEntity<Void> response = productController.deleteProduct(1L);
        
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    @WithMockUser(authorities = "PRODUCT_VIEW")
    void shouldSearchProductsByName() throws Exception {
        Product product = createTestProduct();
        when(productRepository.findByNameContaining("测试")).thenReturn(Arrays.asList(product));

        ResponseEntity<List<Product>> response = productController.searchProducts("测试", null);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(product.getId(), response.getBody().get(0).getId());
        assertEquals(product.getName(), response.getBody().get(0).getName());
    }

    @Test
    @WithMockUser(authorities = "PRODUCT_VIEW")
    void shouldSearchProductsByCategory() throws Exception {
        Product product = createTestProduct();
        when(productRepository.findByCategory("测试分类")).thenReturn(Arrays.asList(product));

        ResponseEntity<List<Product>> response = productController.searchProducts(null, "测试分类");
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(product.getId(), response.getBody().get(0).getId());
        assertEquals(product.getCategory(), response.getBody().get(0).getCategory());
    }
}