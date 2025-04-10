package com.example.warehousemanagement;

import com.example.warehousemanagement.security.JwtAuthenticationFilter;
import com.example.warehousemanagement.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 启用方法级安全控制注解，如 @PreAuthorize、@PostAuthorize 等
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 使用自定义CORS配置
                .csrf(csrf -> csrf.disable()) // 在API应用中通常禁用CSRF
                .authorizeHttpRequests(authorize -> authorize
                        // 根路径和登录相关接口不需要认证
                        .requestMatchers("/", "/api/auth/**", "/error").permitAll()
                        // 订单相关接口
                        .requestMatchers("/api/orders").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('ORDER_CREATE')"))
                        .requestMatchers("/api/orders/{orderId}/items").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('ORDER_ITEM_CREATE')"))
                        .requestMatchers("/api/orders/{orderId}").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('ORDER_VIEW')"))
                        .requestMatchers("/api/orders/user/{userId}").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('ORDER_VIEW')"))
                        .requestMatchers("/api/orders/status/{status}").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('ORDER_VIEW')"))
                        .requestMatchers("/api/orders/{orderId}/status").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('ORDER_UPDATE')"))
                        .requestMatchers("/api/orders/{orderId}/confirm").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('ORDER_CONFIRM')"))
                        .requestMatchers("/api/orders/{orderId}/ship").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('ORDER_SHIP')"))
                        .requestMatchers("/api/orders/{orderId}/complete").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('ORDER_COMPLETE')"))
                        .requestMatchers("/api/orders/search").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('ORDER_VIEW')"))
                        // 产品相关接口
                        .requestMatchers("/api/products").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('PRODUCT_CREATE')"))
                        .requestMatchers("/api/products/{id}").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('PRODUCT_VIEW')"))
                        .requestMatchers("/api/products/{id}/update").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('PRODUCT_UPDATE')"))
                        .requestMatchers("/api/products/{id}/delete").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('PRODUCT_DELETE')"))
                        .requestMatchers("/api/products/search").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('PRODUCT_VIEW')"))
                        // 用户相关接口
                        .requestMatchers("/api/users").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('USER_CREATE')"))
                        .requestMatchers("/api/users/{username}").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('USER_VIEW')"))
                        .requestMatchers("/api/users/role/{roleName}").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('USER_VIEW')"))
                        .requestMatchers("/api/users/id/{id}").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('USER_VIEW')"))
                        .requestMatchers("/api/users/{id}/delete").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('USER_DELETE')"))
                        .requestMatchers("/api/users/all").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('USER_VIEW')"))
                        // 库存相关接口
                        .requestMatchers("/api/inventories").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('INVENTORY_CREATE')"))
                        .requestMatchers("/api/inventories/{id}").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('INVENTORY_VIEW')"))
                        .requestMatchers("/api/inventories/{id}/adjust").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('INVENTORY_UPDATE')"))
                        .requestMatchers("/api/inventories/bulk-status").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('INVENTORY_UPDATE')"))
                        // 车辆相关接口
                        .requestMatchers("/api/vehicles").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('VEHICLE_CREATE')"))
                        .requestMatchers("/api/vehicles/{id}").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('VEHICLE_VIEW')"))
                        .requestMatchers("/api/vehicles/all").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('VEHICLE_VIEW')"))
                        .requestMatchers("/api/vehicles/{id}/update").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('VEHICLE_UPDATE')"))
                        .requestMatchers("/api/vehicles/{id}/delete").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('VEHICLE_DELETE')"))
                        .requestMatchers("/api/vehicles/status/{status}").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('VEHICLE_VIEW')"))
                        .requestMatchers("/api/vehicles/{id}/tasks").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('VEHICLE_UPDATE')"))
                        .requestMatchers("/api/vehicles/{id}/maintenance").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('VEHICLE_UPDATE')"))
                        .requestMatchers("/api/vehicles/{id}/available").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('VEHICLE_UPDATE')"))
                        // 配送记录相关接口
                        .requestMatchers("/api/shipments").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('SHIPMENT_CREATE')"))
                        .requestMatchers("/api/shipments/{id}").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('SHIPMENT_VIEW')"))
                        .requestMatchers("/api/shipments/all").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('SHIPMENT_VIEW')"))
                        .requestMatchers("/api/shipments/{id}/delete").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('SHIPMENT_DELETE')"))
                        .requestMatchers("/api/shipments/task/{taskId}").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('SHIPMENT_VIEW')"))
                        .requestMatchers("/api/shipments/status/{status}").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('SHIPMENT_VIEW')"))
                        // 任务相关接口
                        .requestMatchers("/api/tasks").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('TASK_CREATE')"))
                        .requestMatchers("/api/tasks/{id}").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('TASK_VIEW')"))
                        .requestMatchers("/api/tasks/all").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('TASK_VIEW')"))
                        .requestMatchers("/api/tasks/{id}/delete").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('TASK_DELETE')"))
                        .requestMatchers("/api/tasks/status/{status}").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('TASK_VIEW')"))
                        .requestMatchers("/api/tasks/user/{userId}").access(new WebExpressionAuthorizationManager("@customSecurityExpression.hasPermission('TASK_VIEW')"))
                        // 其他请求也都需要经过认证
                        .anyRequest().authenticated()
                )
                // 使用无状态会话管理，适合REST API
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 添加JWT过滤器在UsernamePasswordAuthenticationFilter之前
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000", 
                "http://localhost:3001", 
                "http://localhost:3002", 
                "http://localhost"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        // 设置允许凭证为false，避免CORS预检问题
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}