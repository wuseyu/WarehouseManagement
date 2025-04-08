package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
    void shouldCreateProduct() throws Exception {
        Product product = createTestProduct();
        when(productRepository.save(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.category").value(product.getCategory()));
    }

    @Test
    void shouldGetProduct() throws Exception {
        Product product = createTestProduct();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value(product.getName()));
    }

    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        Product product1 = createTestProduct();
        Product product2 = createTestProduct();
        product2.setId(2L);
        product2.setName("测试商品2");

        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(product1.getId()))
                .andExpect(jsonPath("$[0].name").value(product1.getName()))
                .andExpect(jsonPath("$[1].id").value(product2.getId()))
                .andExpect(jsonPath("$[1].name").value(product2.getName()));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        Product product = createTestProduct();
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value(product.getName()));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingProduct() throws Exception {
        Product product = createTestProduct();
        when(productRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        when(productRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingProduct() throws Exception {
        when(productRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSearchProductsByName() throws Exception {
        Product product = createTestProduct();
        when(productRepository.findByNameContaining("测试")).thenReturn(Arrays.asList(product));

        mockMvc.perform(get("/api/products/search")
                        .param("name", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(product.getId()))
                .andExpect(jsonPath("$[0].name").value(product.getName()));
    }

    @Test
    void shouldSearchProductsByCategory() throws Exception {
        Product product = createTestProduct();
        when(productRepository.findByCategory("测试分类")).thenReturn(Arrays.asList(product));

        mockMvc.perform(get("/api/products/search")
                        .param("category", "测试分类"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(product.getId()))
                .andExpect(jsonPath("$[0].category").value(product.getCategory()));
    }
}