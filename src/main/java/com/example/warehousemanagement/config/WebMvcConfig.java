package com.example.warehousemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 用于配置自定义表达式解析器和Bean引用解析
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public SpelExpressionParser spelExpressionParser() {
        return new SpelExpressionParser();
    }
    
    @Bean
    public TemplateParserContext templateParserContext() {
        return new TemplateParserContext();
    }
    
    @Bean
    public StandardEvaluationContext standardEvaluationContext() {
        return new StandardEvaluationContext();
    }
} 