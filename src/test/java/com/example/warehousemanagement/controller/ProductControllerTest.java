package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    private Product product;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("This is a test product.");
        product.setCategory("Test Category");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    public void testGetAllProducts() throws Exception {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Another Product");
        product2.setPrice(BigDecimal.valueOf(49.99));

        List<Product> products = Arrays.asList(product, product2);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$[1].name").value("Another Product"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    public void testGetProductById() throws Exception {
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    public void testGetProductByIdNotFound() throws Exception {
        when(productService.getProductById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    public void testCreateProduct() throws Exception {
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Product\",\"description\":\"This is a test product.\",\"category\":\"Test Category\",\"price\":99.99}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(product);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Product\",\"description\":\"Updated description.\",\"category\":\"Updated Category\",\"price\":89.99}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    public void testGetProductsByName() throws Exception {
        when(productService.getProductsByName("Test Product")).thenReturn(Arrays.asList(product));

        mockMvc.perform(get("/api/products/search?name=Test Product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"));

        verify(productService, times(1)).getProductsByName("Test Product");
    }

/**
 * todo
 */

//    @Test
//    public void testGetProductsByPriceRange() throws Exception {
//        Product product2 = new Product();
//        product2.setId(2L);
//        product2.setName("Cheap Product");
//        product2.setPrice(BigDecimal.valueOf(49.99));
//        product2.setCreatedAt(new Timestamp(System.currentTimeMillis()));
//
//        // 模拟返回价格在区间内的产品
//        when(productService.getProductsByPriceRange(BigDecimal.valueOf(30.00), BigDecimal.valueOf(80.00)))
//                .thenReturn(Arrays.asList(product2)); // 只返回价格在区间内的产品
//
//        mockMvc.perform(get("/api/products/search/price?minPrice=30.00&maxPrice=80.00"))
//                .andDo(print()) // 打印响应内容
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[?(@.id == 2)].name").value("Cheap Product")); // 使用 ID 查找产品名称
//
//        verify(productService, times(1)).getProductsByPriceRange(BigDecimal.valueOf(30.00), BigDecimal.valueOf(80.00));
//    }

}