package com.example.warehousemanagement.utils;

import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * 产品数据初始化工具类
 * 用于应用启动时初始化产品数据
 */
@Component
public class ProductDataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductDataInitializer.class);
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * 初始化产品数据的Bean
     */
    @Bean
    public CommandLineRunner initProductData() {
        return args -> {
            // 检查是否已有产品数据
            if (productRepository.count() > 0) {
                logger.info("数据库中已存在产品数据，跳过初始化");
                return;
            }
            
            logger.info("开始初始化产品数据...");
            
            // 创建20条产品数据
            List<Product> products = Arrays.asList(
                createProduct("矿泉水 550ml", "饮品-矿泉水", "农夫山泉纯净水，550ml小瓶装", new BigDecimal("2.50"), new BigDecimal("1.80"), "农夫山泉", "箱", "瓶", new BigDecimal("24"), new BigDecimal("0.55"), new BigDecimal("0.001")),
                createProduct("矿泉水 1.5L", "饮品-矿泉水", "农夫山泉纯净水，1.5L大瓶装", new BigDecimal("4.50"), new BigDecimal("3.20"), "农夫山泉", "箱", "瓶", new BigDecimal("12"), new BigDecimal("1.50"), new BigDecimal("0.0025")),
                createProduct("可乐 330ml", "饮品-碳酸饮料", "可口可乐，330ml罐装", new BigDecimal("3.50"), new BigDecimal("2.60"), "可口可乐公司", "箱", "罐", new BigDecimal("24"), new BigDecimal("0.33"), new BigDecimal("0.0008")),
                createProduct("雪碧 330ml", "饮品-碳酸饮料", "雪碧，清爽柠檬味，330ml罐装", new BigDecimal("3.50"), new BigDecimal("2.60"), "可口可乐公司", "箱", "罐", new BigDecimal("24"), new BigDecimal("0.33"), new BigDecimal("0.0008")),
                createProduct("蒙牛纯牛奶 250ml", "饮品-牛奶", "蒙牛纯牛奶，250ml盒装", new BigDecimal("3.20"), new BigDecimal("2.40"), "蒙牛乳业", "箱", "盒", new BigDecimal("24"), new BigDecimal("0.25"), new BigDecimal("0.0006")),
                createProduct("伊利纯牛奶 250ml", "饮品-牛奶", "伊利纯牛奶，250ml盒装", new BigDecimal("3.20"), new BigDecimal("2.40"), "伊利集团", "箱", "盒", new BigDecimal("24"), new BigDecimal("0.25"), new BigDecimal("0.0006")),
                createProduct("方便面 红烧牛肉", "食品-方便面", "康师傅红烧牛肉面，袋装", new BigDecimal("4.50"), new BigDecimal("3.50"), "康师傅", "箱", "袋", new BigDecimal("30"), new BigDecimal("0.1"), new BigDecimal("0.001")),
                createProduct("方便面 老坛酸菜", "食品-方便面", "统一老坛酸菜面，袋装", new BigDecimal("4.50"), new BigDecimal("3.50"), "统一企业", "箱", "袋", new BigDecimal("30"), new BigDecimal("0.1"), new BigDecimal("0.001")),
                createProduct("巧克力威化饼干", "食品-饼干", "徐福记巧克力威化饼干，200g袋装", new BigDecimal("8.50"), new BigDecimal("6.50"), "徐福记", "箱", "袋", new BigDecimal("24"), new BigDecimal("0.2"), new BigDecimal("0.0006")),
                createProduct("苏打饼干", "食品-饼干", "奥利奥苏打饼干，300g袋装", new BigDecimal("10.50"), new BigDecimal("8.00"), "奥利奥", "箱", "袋", new BigDecimal("20"), new BigDecimal("0.3"), new BigDecimal("0.0008")),
                createProduct("洗发水 400ml", "日化-洗护", "飘柔洗发水，400ml瓶装", new BigDecimal("28.90"), new BigDecimal("22.00"), "宝洁公司", "箱", "瓶", new BigDecimal("12"), new BigDecimal("0.4"), new BigDecimal("0.001")),
                createProduct("沐浴露 400ml", "日化-洗护", "舒肤佳沐浴露，400ml瓶装", new BigDecimal("26.90"), new BigDecimal("20.00"), "宝洁公司", "箱", "瓶", new BigDecimal("12"), new BigDecimal("0.4"), new BigDecimal("0.001")),
                createProduct("牙膏 120g", "日化-口腔", "高露洁牙膏，120g管装", new BigDecimal("16.90"), new BigDecimal("12.00"), "高露洁", "箱", "支", new BigDecimal("48"), new BigDecimal("0.12"), new BigDecimal("0.0003")),
                createProduct("牙刷 软毛", "日化-口腔", "高露洁软毛牙刷，单支装", new BigDecimal("12.90"), new BigDecimal("8.00"), "高露洁", "盒", "支", new BigDecimal("36"), new BigDecimal("0.02"), new BigDecimal("0.0001")),
                createProduct("抽纸 100抽", "日化-纸品", "维达抽纸，100抽/包", new BigDecimal("6.50"), new BigDecimal("4.80"), "维达纸业", "箱", "包", new BigDecimal("30"), new BigDecimal("0.1"), new BigDecimal("0.001")),
                createProduct("卷纸 10卷", "日化-纸品", "维达卷纸，10卷/提", new BigDecimal("42.90"), new BigDecimal("34.00"), "维达纸业", "件", "提", new BigDecimal("4"), new BigDecimal("1.2"), new BigDecimal("0.02")),
                createProduct("洗衣液 2kg", "日化-洗涤", "立白洗衣液，2kg瓶装", new BigDecimal("36.50"), new BigDecimal("28.00"), "立白集团", "箱", "瓶", new BigDecimal("6"), new BigDecimal("2.0"), new BigDecimal("0.003")),
                createProduct("洗洁精 1kg", "日化-洗涤", "立白洗洁精，1kg瓶装", new BigDecimal("18.50"), new BigDecimal("14.00"), "立白集团", "箱", "瓶", new BigDecimal("12"), new BigDecimal("1.0"), new BigDecimal("0.0015")),
                createProduct("香蕉 1kg", "生鲜-水果", "云南香蕉，1kg装", new BigDecimal("8.50"), new BigDecimal("6.00"), "云南水果基地", "箱", "斤", new BigDecimal("10"), new BigDecimal("1.0"), new BigDecimal("0.002")),
                createProduct("苹果 1kg", "生鲜-水果", "山东红富士苹果，1kg装", new BigDecimal("12.50"), new BigDecimal("9.00"), "山东果园", "箱", "斤", new BigDecimal("10"), new BigDecimal("1.0"), new BigDecimal("0.002"))
            );
            
            // 保存产品数据
            productRepository.saveAll(products);
            
            logger.info("产品数据初始化完成！共添加 {} 条记录", products.size());
        };
    }
    
    /**
     * 创建产品对象
     */
    private Product createProduct(String name, String category, String description, 
                                 BigDecimal sellingPrice, BigDecimal purchasePrice, 
                                 String supplierName, String purchaseUnit, String salesUnit, 
                                 BigDecimal unitConversionRatio, BigDecimal weight, BigDecimal volume) {
        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setDescription(description);
        product.setSellingPrice(sellingPrice);
        product.setPurchasePrice(purchasePrice);
        product.setSupplierName(supplierName);
        product.setPurchaseUnit(purchaseUnit);
        product.setSalesUnit(salesUnit);
        product.setUnitConversionRatio(unitConversionRatio);
        product.setWeight(weight);
        product.setVolume(volume);
        product.setHasExpiration(true);
        product.setShelfLifeDays(category.startsWith("生鲜") ? 7 : 365);
        product.setStackingLimit(category.contains("纸品") ? 5 : 3);
        product.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        
        if (category.contains("-")) {
            product.setCategoryCode(category.split("-")[0]);
        } else {
            product.setCategoryCode(category.toUpperCase());
        }
        
        return product;
    }
} 