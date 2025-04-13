package com.example.warehousemanagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    
    // API权限映射关系定义
    private static final Map<String, Set<String>> API_PERMISSION_MAP = new HashMap<>();
    
    static {
        // 产品API权限 - 超级管理员和供应商可以操作
        API_PERMISSION_MAP.put("/api/products", Set.of("ROLE_SUPER_ADMIN", "ROLE_SUPPLIER"));
        
        // 用户API权限 - 仅超级管理员可以操作
        API_PERMISSION_MAP.put("/api/users", Set.of("ROLE_SUPER_ADMIN"));
        
        // 订单API权限 - 超级管理员、代理商和门店可以操作
        API_PERMISSION_MAP.put("/api/orders", Set.of("ROLE_SUPER_ADMIN", "ROLE_AGENT", "ROLE_STORE"));
        
        // 库存API权限 - 超级管理员和城市运营商可以操作
        API_PERMISSION_MAP.put("/api/inventories", Set.of("ROLE_SUPER_ADMIN", "ROLE_CITY_OPERATOR"));
        
        // 车辆API权限 - 超级管理员和代理商可以操作
        API_PERMISSION_MAP.put("/api/vehicles", Set.of("ROLE_SUPER_ADMIN", "ROLE_AGENT"));
        
        // 配送记录API权限 - 超级管理员和代理商可以操作
        API_PERMISSION_MAP.put("/api/shipments", Set.of("ROLE_SUPER_ADMIN", "ROLE_AGENT"));
        
        // 任务API权限 - 超级管理员和代理商可以操作
        API_PERMISSION_MAP.put("/api/tasks", Set.of("ROLE_SUPER_ADMIN", "ROLE_AGENT"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String requestURI = request.getRequestURI();
            String method = request.getMethod();
            logger.info("【JWT过滤器】处理请求: {} {}", method, requestURI);
            
            // 对于不需要认证的路径，直接放行
            if (requestURI.equals("/") || requestURI.startsWith("/api/auth/")) {
                logger.info("【JWT过滤器】公开API路径，无需认证: {}", requestURI);
                filterChain.doFilter(request, response);
                return;
            }
            
            String jwt = parseJwt(request);
            logger.info("【JWT过滤器】请求路径: {}, JWT令牌: {}", requestURI, jwt != null ? "已获取" : "未获取");
            
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                logger.info("【JWT过滤器】JWT令牌解析用户名: {}", username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                StringBuilder rolesBuilder = new StringBuilder();
                if (userDetails instanceof UserDetailsEntity) {
                    UserDetailsEntity userDetailsEntity = (UserDetailsEntity) userDetails;
                    rolesBuilder.append("用户ID: ").append(userDetailsEntity.getUserId()).append(", 角色: ");
                    userDetailsEntity.getAuthorities().forEach(auth -> 
                        rolesBuilder.append(auth.getAuthority()).append(" ")
                    );
                    logger.info("【JWT过滤器】认证用户信息: {}", rolesBuilder.toString());
                }
                String roles = rolesBuilder.toString();
                
                // 设置认证上下文
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("【JWT过滤器】成功设置认证信息到SecurityContext");
                
                // 权限判断 - 检查用户是否有权限访问此API
                if (!checkApiPermission(requestURI, userDetails)) {
                    logger.warn("【JWT过滤器】用户无权访问API: {}, 角色: {}", requestURI, roles);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"error\":\"权限不足\",\"message\":\"您没有权限访问此资源\"}");
                    return;
                }
                
                logger.info("【JWT过滤器】用户有权访问API: {}, 角色: {}", requestURI, roles);
            } else if (jwt != null) {
                logger.warn("【JWT过滤器】JWT令牌验证失败，可能已过期或无效");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"认证失败\",\"message\":\"身份验证失败，请重新登录\"}");
                return;
            } else {
                logger.warn("【JWT过滤器】请求未携带JWT令牌，拒绝访问: {}", requestURI);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"未认证\",\"message\":\"请先登录\"}");
                return;
            }
        } catch (Exception e) {
            logger.error("【JWT过滤器】无法设置用户认证: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"服务器错误\",\"message\":\"服务器处理认证时出错\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        logger.debug("【JWT过滤器】Authorization头: {}", headerAuth);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
    
    /**
     * 检查用户是否有权限访问特定API
     * 基于URL路径前缀和用户角色进行判断
     */
    private boolean checkApiPermission(String requestURI, UserDetails userDetails) {
        // 超级管理员有全部权限
        boolean isSuperAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_SUPER_ADMIN".equals(auth.getAuthority()));
        
        if (isSuperAdmin) {
            logger.info("【JWT过滤器】超级管理员可访问所有API: {}", requestURI);
            return true;
        }
        
        // 针对其他角色，检查API权限映射
        for (Map.Entry<String, Set<String>> entry : API_PERMISSION_MAP.entrySet()) {
            String apiPrefix = entry.getKey();
            Set<String> allowedRoles = entry.getValue();
            
            if (requestURI.startsWith(apiPrefix)) {
                // 检查用户是否拥有允许的角色之一
                boolean hasPermission = userDetails.getAuthorities().stream()
                        .anyMatch(auth -> allowedRoles.contains(auth.getAuthority()));
                
                if (hasPermission) {
                    logger.info("【JWT过滤器】用户角色匹配API权限: {}", requestURI);
                    return true;
                } else {
                    logger.warn("【JWT过滤器】用户角色不匹配API权限要求: {}", requestURI);
                    return false;
                }
            }
        }
        
        // 默认拒绝访问
        logger.warn("【JWT过滤器】API没有定义权限映射，拒绝访问: {}", requestURI);
        return false;
    }
} 