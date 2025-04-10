package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;
    
    private Product testProduct;
    
    @BeforeEach
    void setUp() {
        // 初始化测试产品
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("测试产品");
        testProduct.setDescription("测试描述");
        testProduct.setPurchasePrice(new BigDecimal("89.99"));
        testProduct.setSellingPrice(new BigDecimal("99.99"));
        testProduct.setCategory("测试类别");
        testProduct.setSku("TEST-000001");
        testProduct.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    void getAllProducts_Success() {
        // 准备测试数据
        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct));

        // 执行测试
        List<Product> products = productService.getAllProducts();

        // 验证结果
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("测试产品");
        verify(productRepository).findAll();
    }

    @Test
    void getProduct_Success() {
        // 准备测试数据
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // 执行测试
        Optional<Product> product = productService.getProduct(1L);

        // 验证结果
        assertThat(product).isPresent();
        assertThat(product.get().getName()).isEqualTo("测试产品");
        verify(productRepository).findById(1L);
    }

    @Test
    void createProduct_Success() {
        // 准备测试数据
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // 执行测试
        Product createdProduct = productService.createProduct(testProduct);

        // 验证结果
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getName()).isEqualTo("测试产品");
        verify(productRepository).save(testProduct);
    }

    @Test
    void updateProduct_Success() {
        // 准备测试数据
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // 执行测试
        Product updatedProduct = productService.updateProduct(1L, testProduct);

        // 验证结果
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getName()).isEqualTo("测试产品");
        verify(productRepository).existsById(1L);
        verify(productRepository).save(testProduct);
    }
    
    @Test
    void updateProduct_NonExisting() {
        // 准备测试数据
        when(productRepository.existsById(1L)).thenReturn(false);

        // 执行测试并验证异常
        assertThatThrownBy(() -> productService.updateProduct(1L, testProduct))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("产品不存在");
                
        // 验证调用
        verify(productRepository).existsById(1L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_Success() {
        // 准备测试数据
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // 执行测试
        productService.deleteProduct(1L);

        // 验证调用
        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }
    
    @Test
    void deleteProduct_NonExisting() {
        // 准备测试数据
        when(productRepository.existsById(1L)).thenReturn(false);

        // 执行测试并验证异常
        assertThatThrownBy(() -> productService.deleteProduct(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("产品不存在");
                
        // 验证调用
        verify(productRepository).existsById(1L);
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void searchProducts_ByName_Success() {
        // 准备测试数据
        when(productRepository.findByNameContaining("测试")).thenReturn(Arrays.asList(testProduct));

        // 执行测试
        List<Product> products = productService.searchProducts("测试", null);

        // 验证结果
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("测试产品");
        verify(productRepository).findByNameContaining("测试");
    }
    
    @Test
    void searchProducts_ByCategory_Success() {
        // 准备测试数据
        when(productRepository.findByCategory("测试类别")).thenReturn(Arrays.asList(testProduct));

        // 执行测试
        List<Product> products = productService.searchProducts(null, "测试类别");

        // 验证结果
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getCategory()).isEqualTo("测试类别");
        verify(productRepository).findByCategory("测试类别");
    }
} 