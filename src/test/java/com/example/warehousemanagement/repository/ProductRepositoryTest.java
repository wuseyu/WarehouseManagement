package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductRepositoryTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService; // 这里可以直接使用 ProductService 进行测试

    private Product product;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("This is a test product.");
        product.setCategory("Test Category");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    public void testCreateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product createdProduct = productService.createProduct(product);
        assertNotNull(createdProduct);
        assertEquals("Test Product", createdProduct.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testGetProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> foundProduct = productService.getProductById(1L);
        assertTrue(foundProduct.isPresent());
        assertEquals("Test Product", foundProduct.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetAllProducts() {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Another Product");
        product2.setPrice(BigDecimal.valueOf(49.99));

        when(productRepository.findAll()).thenReturn(Arrays.asList(product, product2));

        List<Product> products = productService.getAllProducts();
        assertEquals(2, products.size());
        assertEquals("Test Product", products.get(0).getName());
        assertEquals("Another Product", products.get(1).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteProduct() {
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    // 根据名称查找产品
    @Test
    public void testFindByName() {
        when(productRepository.findByName("Test Product")).thenReturn(Arrays.asList(product));

        List<Product> foundProducts = productService.getProductsByName("Test Product");
        assertEquals(1, foundProducts.size());
        assertEquals("Test Product", foundProducts.get(0).getName());
        verify(productRepository, times(1)).findByName("Test Product");
    }

    // 根据价格区间查找产品
    @Test
    public void testFindByPriceBetween() {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Cheap Product");
        product2.setPrice(BigDecimal.valueOf(49.99));
        product2.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        when(productRepository.findByPriceBetween(BigDecimal.valueOf(30.00), BigDecimal.valueOf(100.00)))
                .thenReturn(Arrays.asList(product, product2));

        List<Product> foundProducts = productService.getProductsByPriceRange(BigDecimal.valueOf(30.00), BigDecimal.valueOf(100.00));
        assertEquals(2, foundProducts.size());
        assertEquals("Test Product", foundProducts.get(0).getName());
        assertEquals("Cheap Product", foundProducts.get(1).getName());
        verify(productRepository, times(1)).findByPriceBetween(BigDecimal.valueOf(30.00), BigDecimal.valueOf(100.00));

        when(productRepository.findByPriceBetween(BigDecimal.valueOf(30.00), BigDecimal.valueOf(80.00)))
                .thenReturn(Arrays.asList(product2)); // 只返回价格在区间内的产品

        List<Product> foundProducts2 = productService.getProductsByPriceRange(BigDecimal.valueOf(30.00), BigDecimal.valueOf(80.00));
        assertEquals(1, foundProducts2.size()); // 只应返回一个产品
        assertEquals("Cheap Product", foundProducts2.get(0).getName()); // 确保返回的是便宜的产品
        verify(productRepository, times(1)).findByPriceBetween(BigDecimal.valueOf(30.00), BigDecimal.valueOf(80.00));

    }
}