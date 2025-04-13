package com.example.warehousemanagement.config;

import com.example.warehousemanagement.security.CustomSecurityExpression;
import com.example.warehousemanagement.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Web安全配置类
 * 基于角色的权限控制
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private CustomSecurityExpression customSecurityExpression;
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("【安全配置】初始化WebSecurityConfig, 使用基于角色的权限控制");
        
        // 基于角色的权限控制
        http.authorizeHttpRequests(authorize -> authorize
            // 公开接口
            .requestMatchers("/", "/api/auth/**").permitAll()
            
            // 产品相关接口 - 超级管理员可访问
            .requestMatchers("/api/products/**").hasRole("SUPER_ADMIN")
            
            // 用户相关接口 - 超级管理员可访问
            .requestMatchers("/api/users/**").hasRole("SUPER_ADMIN")
            
            // 订单相关接口 - 超级管理员可访问
            .requestMatchers("/api/orders/**").hasRole("SUPER_ADMIN")
            
            // 库存相关接口 - 超级管理员可访问
            .requestMatchers("/api/inventories/**").hasRole("SUPER_ADMIN")
            
            // 车辆相关接口 - 超级管理员可访问
            .requestMatchers("/api/vehicles/**").hasRole("SUPER_ADMIN")
            
            // 配送记录相关接口 - 超级管理员可访问
            .requestMatchers("/api/shipments/**").hasRole("SUPER_ADMIN")
            
            // 任务相关接口 - 超级管理员可访问
            .requestMatchers("/api/tasks/**").hasRole("SUPER_ADMIN")
            
            // 其他请求需要认证
            .anyRequest().authenticated()
        );
        
        // 禁用CSRF，启用无状态会话
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        // 添加JWT过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        logger.info("【安全配置】SecurityFilterChain配置完成");
        return http.build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 