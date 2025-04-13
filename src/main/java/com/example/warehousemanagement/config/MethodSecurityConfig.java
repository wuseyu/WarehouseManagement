package com.example.warehousemanagement.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * 方法级安全配置类
 * 用于配置方法级别的安全表达式处理器
 */
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    /**
     * 配置方法安全表达式处理器
     * 使其能够解析自定义安全表达式Bean引用
     */
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setApplicationContext(applicationContext);
        return expressionHandler;
    }
} 