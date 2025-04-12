package com.example.warehousemanagement.config;

import com.example.warehousemanagement.entity.*;
import com.example.warehousemanagement.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 模拟数据初始化器：生成模拟的库存和订单数据
 */
@Component
@Order(3) // 在DataInitializer和其他初始化器之后执行
public class MockDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MockDataInitializer.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private DataSource dataSource;

    // 常量和随机数据源
    private static final String[] PRODUCT_CATEGORIES = {
        "饮料-矿泉水", "饮料-碳酸饮料", "饮料-果汁", "食品-零食", "食品-主食", 
        "日用品-清洁", "日用品-个护", "电子-配件", "电子-小家电", "服装-男装", "服装-女装"
    };
    
    private static final String[] PRODUCT_NAMES_PREFIX = {
        "高山", "清泉", "甜心", "新鲜", "原味", "纯净", "清香", "醇厚", "优质", "特级", 
        "精选", "珍藏", "尊享", "至尊", "经典", "豪华", "传统", "现代", "智能", "时尚"
    };
    
    private static final String[] PRODUCT_NAMES_SUFFIX = {
        "矿泉水", "可乐", "雪碧", "橙汁", "苹果汁", "薯片", "饼干", "巧克力", "面包", "速食面", 
        "洗衣液", "洗发水", "沐浴露", "牙膏", "充电宝", "耳机", "小风扇", "T恤", "连衣裙", "牛仔裤"
    };
    
    private static final String[] WAREHOUSE_LOCATIONS = {
        "北京市朝阳区建国路88号", "上海市浦东新区张江高科技园区", "广州市天河区珠江新城", "深圳市南山区科技园",
        "成都市高新区天府大道", "武汉市洪山区光谷大道", "杭州市西湖区文三路", "南京市建邺区奥体大道",
        "重庆市渝北区龙溪街道", "西安市雁塔区高新路"
    };
    
    private static final String[] WAREHOUSE_NAMES = {
        "中央仓库", "北区仓库", "南区仓库", "东区仓库", "西区仓库", "高新区仓库", "物流中心", "配送中心", 
        "冷链仓", "保税仓", "危化品仓", "中转仓", "城市仓", "前置仓", "社区仓"
    };
    
    private static final String[] BATCH_NUMBERS = {
        "B202301", "B202302", "B202303", "B202304", "B202305", 
        "B202306", "B202307", "B202308", "B202309", "B202310"
    };
    
    private static final String[] DELIVERY_ADDRESSES = {
        "北京市海淀区中关村大街1号", "上海市徐汇区肇嘉浜路1111号", "广州市越秀区解放北路123号",
        "深圳市福田区深南大道1000号", "成都市锦江区红星路三段99号", "武汉市江汉区解放大道688号",
        "杭州市上城区延安路100号", "南京市鼓楼区中山北路100号", "重庆市渝中区解放碑步行街",
        "西安市碑林区南大街30号", "天津市和平区南京路100号", "济南市历下区泉城路11号",
        "青岛市市南区香港中路10号", "大连市中山区人民路50号", "沈阳市和平区太原街20号"
    };

    // 车辆数据相关常量
    private static final String[] DRIVER_NAMES = {
        "张三", "李四", "王五", "赵六", "钱七", "孙八", "周九", "吴十",
        "郑一", "王二", "刘一", "陈二", "杨三", "黄四", "赵五", "吴六"
    };
    
    private static final String[] PLATE_NUMBERS_PREFIX = {
        "京A", "京B", "沪A", "沪B", "粤A", "粤B", "浙A", "浙B", 
        "苏A", "苏B", "鲁A", "鲁B", "冀A", "冀B", "豫A", "豫B"
    };
    
    private static final String[] LOCATIONS = {
        "北京市朝阳区", "上海市浦东新区", "广州市天河区", "深圳市南山区",
        "成都市高新区", "武汉市洪山区", "杭州市西湖区", "南京市建邺区",
        "重庆市渝北区", "西安市雁塔区", "天津市和平区", "济南市历下区",
        "青岛市市南区", "大连市中山区", "沈阳市和平区", "长沙市岳麓区"
    };
    
    private static final String[] INSURANCE_TYPES = {
        "交强险+商业险A套餐", "交强险+商业险B套餐", "交强险+商业险C套餐", 
        "基础保障型", "全面保障型", "豪华保障型", "经济型保险", "全险保障"
    };

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("【模拟数据】开始初始化模拟数据...");
        
        // 检查是否存在强制重新生成数据的参数
        boolean forceRegenerate = checkForRegenFlag(args);
        
        // 1. 清理旧数据（如果需要的话）
        if (forceRegenerate) {
            // 清除旧订单数据
            clearOrderData();
        }
        
        // 初始化角色
        initRoles();
        
        // 2. 初始化基础数据
        List<Product> products = initProducts(30); // 创建30个产品
        List<Warehouse> warehouses = initWarehouses(5); // 创建5个仓库
        List<User> storeUsers = findUsersByRole("ROLE_STORE"); // 获取门店角色用户
        List<User> operatorUsers = findUsersByRole("ROLE_CITY_OPERATOR"); // 获取城市运营商角色用户
        
        // 3. 初始化车辆数据
        initVehicles(20); // 创建20辆车
        
        // 如果没有相应角色的用户，创建一些
        if (storeUsers.isEmpty()) {
            storeUsers = createUsersWithRole("store", 3, "ROLE_STORE");
        }
        
        if (operatorUsers.isEmpty()) {
            operatorUsers = createUsersWithRole("operator", 2, "ROLE_CITY_OPERATOR");
        }
        
        // 4. 创建库存数据（每个仓库x每个产品，约50条）
        initInventories(products, warehouses);
        
        // 5. 创建订单数据部分 - 暂时注释掉
        /*
        List<User> orderUsers = new ArrayList<>(storeUsers);
        orderUsers.addAll(operatorUsers);
        
        // 无论是否有现有数据，都创建新的测试订单数据
        logger.info("【模拟数据】开始生成订单测试数据...");
        initOrders(products, orderUsers, 50);
        
        // 6. 输出总结信息
        long totalOrders = orderRepository.count();
        logger.info("【模拟数据】模拟数据初始化完成，当前系统中共有{}个订单", totalOrders);
        */
        // 输出创建成功的基础数据信息
        logger.info("【模拟数据】模拟数据初始化完成，创建了{}个产品、{}个仓库", 
                productRepository.count(), warehouseRepository.count());
    }
    
    /**
     * 检查是否有强制重生成数据的命令行参数
     */
    private boolean checkForRegenFlag(String[] args) {
        if (args != null) {
            for (String arg : args) {
                if ("--regenerate-mock-data".equals(arg) || "-rmd".equals(arg)) {
                    logger.info("【模拟数据】检测到强制重生成数据的标志");
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 清除所有订单数据
     */
    private void clearOrderData() {
        long orderCount = orderRepository.count();
        if (orderCount > 0) {
            logger.info("【模拟数据】开始清除{}条旧订单数据...", orderCount);
            try {
                // 直接使用JDBC执行SQL删除，绕过JPA的级联删除限制
                orderRepository.deleteAllOrderItems();
                orderRepository.deleteAllOrders();
                logger.info("【模拟数据】成功清除所有订单数据");
            } catch (Exception e) {
                logger.error("【模拟数据】清除订单数据失败: {}", e.getMessage(), e);
            }
        } else {
            logger.info("【模拟数据】当前没有订单数据，无需清除");
        }
    }
    
    /**
     * 初始化角色
     */
    private void initRoles() {
        logger.info("【模拟数据】检查现有角色...");
        
        // 通过名称和类型处理角色不匹配的问题
        List<Role> existingRoles = roleRepository.findAll();
        
        // 处理SUPER_ADMIN角色
        handleRoleMismatch(existingRoles, "ROLE_SUPER_ADMIN", "SUPER_ADMIN", "系统最高权限");
        
        // 处理STORE角色
        handleRoleMismatch(existingRoles, "ROLE_STORE", "STORE", "终端销售");
        
        // 处理CITY_OPERATOR角色
        handleRoleMismatch(existingRoles, "ROLE_CITY_OPERATOR", "CITY_OPERATOR", "城市运营管理");
    }
    
    /**
     * 处理角色名称和类型不匹配的问题
     */
    private void handleRoleMismatch(List<Role> existingRoles, String expectedName, String expectedType, String responsibility) {
        // 先检查是否有完全匹配的角色
        boolean hasExactMatch = existingRoles.stream()
                .anyMatch(r -> expectedName.equals(r.getName()) && expectedType.equals(r.getType()));
        
        if (hasExactMatch) {
            logger.info("【模拟数据】角色{}已存在且类型正确", expectedName);
            return;
        }
        
        // 检查是否有相同类型的角色但名称不同
        Optional<Role> sameTypeRole = existingRoles.stream()
                .filter(r -> expectedType.equals(r.getType()))
                .findFirst();
        
        if (sameTypeRole.isPresent()) {
            // 直接更新已存在角色的名称，而不创建新角色
            Role role = sameTypeRole.get();
            logger.info("【模拟数据】找到类型为{}的角色，名称为{}，更新为{}", 
                expectedType, role.getName(), expectedName);
            role.setName(expectedName);
            role.setResponsibility(responsibility);
            roleRepository.save(role);
            return;
        }
        
        // 检查是否有同名角色但类型不同
        Optional<Role> sameNameRole = existingRoles.stream()
                .filter(r -> expectedName.equals(r.getName()))
                .findFirst();
        
        if (sameNameRole.isPresent()) {
            // 更新类型
            Role role = sameNameRole.get();
            role.setType(expectedType);
            roleRepository.save(role);
            logger.info("【模拟数据】已将角色{}的类型更新为{}", expectedName, expectedType);
            return;
        }
        
        // 都不存在，创建新角色
        logger.info("【模拟数据】创建新角色:{}, 类型:{}", expectedName, expectedType);
        Role newRole = new Role();
        newRole.setName(expectedName);
        newRole.setType(expectedType);
        newRole.setResponsibility(responsibility);
        roleRepository.save(newRole);
    }
    
    /**
     * 创建产品数据
     */
    private List<Product> initProducts(int count) {
        logger.info("【模拟数据】开始创建{}个产品...", count);
        
        // 检查已有产品数量，如果足够则直接返回
        List<Product> existingProducts = productRepository.findAll();
        if (existingProducts.size() >= count) {
            logger.info("【模拟数据】已有{}个产品，无需创建", existingProducts.size());
            return existingProducts;
        }
        
        List<Product> products = new ArrayList<>();
        
        // 创建不足的产品数量
        for (int i = existingProducts.size(); i < count; i++) {
            String category = PRODUCT_CATEGORIES[i % PRODUCT_CATEGORIES.length];
            String namePrefix = PRODUCT_NAMES_PREFIX[i % PRODUCT_NAMES_PREFIX.length];
            String nameSuffix = PRODUCT_NAMES_SUFFIX[i % PRODUCT_NAMES_SUFFIX.length];
            String name = namePrefix + nameSuffix;
            
            Product product = new Product(name, category);
            
            // 随机产品属性
            product.setDescription("这是一款" + name + "，" + category + "类别的优质产品。");
            product.setPurchasePrice(randomBigDecimal(5, 100, 2));
            product.setSellingPrice(randomBigDecimal(product.getPurchasePrice().floatValue() * 1.2f, 
                                                  product.getPurchasePrice().floatValue() * 1.5f, 2));
            product.setWeight(randomBigDecimal(0.1f, 10.0f, 2));
            product.setVolume(randomBigDecimal(0.001f, 0.5f, 3));
            product.setStackingLimit(ThreadLocalRandom.current().nextInt(1, 6));
            product.setPurchaseUnit("箱");
            product.setSalesUnit("个");
            product.setUnitConversionRatio(new BigDecimal(ThreadLocalRandom.current().nextInt(6, 25)));
            product.setSupplierName(namePrefix + "公司");
            
            // 随机设置保质期
            boolean hasExpiration = ThreadLocalRandom.current().nextBoolean();
            product.setHasExpiration(hasExpiration);
            if (hasExpiration) {
                product.setShelfLifeDays(ThreadLocalRandom.current().nextInt(30, 730)); // 1个月到2年
            }
            
            // 设置创建时间
            product.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            
            // 手动设置唯一SKU，避免冲突
            String categoryCode = category.contains("-") ? category.split("-")[0] : category;
            String uniqueSku = generateUniqueId(categoryCode, i);
            product.setSku(uniqueSku);
            
            products.add(product);
        }
        
        // 保存产品
        productRepository.saveAll(products);
        products.addAll(existingProducts);
        
        logger.info("【模拟数据】成功创建{}个产品", products.size() - existingProducts.size());
        return products;
    }
    
    /**
     * 创建仓库数据
     */
    private List<Warehouse> initWarehouses(int count) {
        logger.info("【模拟数据】开始创建{}个仓库...", count);
        
        // 检查已有仓库数量，如果足够则直接返回
        List<Warehouse> existingWarehouses = warehouseRepository.findAll();
        if (existingWarehouses.size() >= count) {
            logger.info("【模拟数据】已有{}个仓库，无需创建", existingWarehouses.size());
            return existingWarehouses;
        }
        
        List<Warehouse> warehouses = new ArrayList<>();
        
        // 创建不足的仓库数量
        for (int i = existingWarehouses.size(); i < count; i++) {
            String name = WAREHOUSE_NAMES[i % WAREHOUSE_NAMES.length] + "-" + (i + 1);
            String location = WAREHOUSE_LOCATIONS[i % WAREHOUSE_LOCATIONS.length];
            
            Warehouse warehouse = new Warehouse(name, location);
            
            // 随机仓库属性
            warehouse.setWarehouseType(Warehouse.WarehouseType.values()[ThreadLocalRandom.current().nextInt(Warehouse.WarehouseType.values().length)]);
            warehouse.setTotalVolume(randomBigDecimal(1000, 10000, 2)); // 1000-10000立方米
            warehouse.setTotalWeight(randomBigDecimal(500, 5000, 2)); // 500-5000吨
            warehouse.setZone("Zone-" + (char)('A' + i % 5) + "-" + (i % 3 + 1));
            warehouse.setStatus(Warehouse.WarehouseStatus.ACTIVE);
            
            // 随机分配一个管理员（如果有用户的话）
            List<User> users = userRepository.findAll();
            if (!users.isEmpty()) {
                warehouse.setManager(users.get(ThreadLocalRandom.current().nextInt(users.size())));
            }
            
            warehouses.add(warehouse);
        }
        
        // 保存仓库
        warehouseRepository.saveAll(warehouses);
        warehouses.addAll(existingWarehouses);
        
        logger.info("【模拟数据】成功创建{}个仓库", warehouses.size() - existingWarehouses.size());
        return warehouses;
    }
    
    /**
     * 创建库存数据
     */
    private void initInventories(List<Product> products, List<Warehouse> warehouses) {
        logger.info("【模拟数据】开始创建库存数据...");
        
        // 获取当前库存总数
        long existingCount = inventoryRepository.count();
        
        // 如果已有50条以上库存记录，就不再创建
        if (existingCount >= 50) {
            logger.info("【模拟数据】已有{}条库存记录，无需创建", existingCount);
            return;
        }
        
        List<Inventory> inventories = new ArrayList<>();
        
        // 为每个仓库的每个产品创建库存记录
        for (Warehouse warehouse : warehouses) {
            for (Product product : products) {
                // 每个产品在每个仓库可能有多个批次
                int batchCount = ThreadLocalRandom.current().nextInt(1, 3); // 1-2个批次
                
                for (int i = 0; i < batchCount; i++) {
                    // 随机决定是否为这个组合创建库存
                    if (ThreadLocalRandom.current().nextDouble() > 0.3) { // 70%的概率创建库存
                        Inventory inventory = new Inventory();
                        inventory.setWarehouse(warehouse);
                        inventory.setProduct(product);
                        
                        // 随机库存数量 (10-1000)
                        inventory.setQuantity(ThreadLocalRandom.current().nextInt(10, 1001));
                        
                        // 随机批次号
                        inventory.setBatchNo(BATCH_NUMBERS[ThreadLocalRandom.current().nextInt(BATCH_NUMBERS.length)]);
                        
                        // 如果产品有保质期，设置过期时间
                        if (product.isHasExpiration()) {
                            int daysToAdd = product.getShelfLifeDays() != null ? 
                                            product.getShelfLifeDays() : ThreadLocalRandom.current().nextInt(30, 365);
                            inventory.setExpirationDate(LocalDate.now().plusDays(daysToAdd));
                        }
                        
                        // 随机库存状态 (95%可用，5%冻结)
                        if (ThreadLocalRandom.current().nextDouble() > 0.95) {
                            inventory.setStatus(Inventory.InventoryStatus.FROZEN);
                        } else {
                            inventory.setStatus(Inventory.InventoryStatus.AVAILABLE);
                        }
                        
                        // 设置锁定数量 (0-10% 的库存量)
                        int lockedQuantity = (int) (inventory.getQuantity() * ThreadLocalRandom.current().nextDouble(0, 0.1));
                        inventory.setLockedQuantity(lockedQuantity);
                        
                        // 设置时间戳
                        Timestamp now = new Timestamp(System.currentTimeMillis());
                        inventory.setCreatedAt(now);
                        inventory.setUpdatedAt(now);
                        
                        inventories.add(inventory);
                        
                        // 如果达到了50条记录，就停止创建
                        if (inventories.size() + existingCount >= 50) {
                            break;
                        }
                    }
                }
                
                // 如果达到了50条记录，就停止创建
                if (inventories.size() + existingCount >= 50) {
                    break;
                }
            }
            
            // 如果达到了50条记录，就停止创建
            if (inventories.size() + existingCount >= 50) {
                break;
            }
        }
        
        // 保存库存
        inventoryRepository.saveAll(inventories);
        
        logger.info("【模拟数据】成功创建{}条库存记录", inventories.size());
    }
    
    /**
     * 初始化订单数据
     */
    private void initOrders(List<Product> products, List<User> users, int count) {
        logger.info("【模拟数据】开始创建{}个订单...", count);
        
        // 检查用户列表是否为空
        if (users.isEmpty()) {
            // 检查是否已存在admin用户
            Optional<User> existingAdmin = userRepository.findByUsername("admin");
            if (existingAdmin.isPresent()) {
                // 如果已有admin用户，直接使用
                users = Collections.singletonList(existingAdmin.get());
                logger.info("【模拟数据】使用已存在的admin用户创建订单");
            } else {
                // 如果没有用户，创建一个默认用户
                User defaultUser = new User();
                defaultUser.setUsername("admin");
                defaultUser.setPassword(passwordEncoder.encode("admin"));
                defaultUser.setEmail("admin@example.com");
                defaultUser.setPhone("13800000000");
                
                // 获取超级管理员角色
                Optional<Role> roleOpt = roleRepository.findByName("ROLE_SUPER_ADMIN");
                if (roleOpt.isPresent()) {
                    defaultUser.getRoles().add(roleOpt.get());
                } else {
                    // 尝试通过类型获取角色
                    List<Role> allRoles = roleRepository.findAll();
                    Optional<Role> adminRole = allRoles.stream()
                        .filter(role -> "SUPER_ADMIN".equals(role.getType()))
                        .findFirst();
                    
                    if (adminRole.isPresent()) {
                        defaultUser.getRoles().add(adminRole.get());
                    } else {
                        logger.warn("【模拟数据】找不到超级管理员角色，用户将没有任何角色");
                    }
                }
                
                defaultUser = userRepository.save(defaultUser);
                users = Collections.singletonList(defaultUser);
                logger.info("【模拟数据】没有可用用户，已创建默认用户: {}", defaultUser.getUsername());
            }
        }
        
        // 如果无论如何都需要生成新的订单数据，先删除所有现有订单数据
        try {
            logger.info("【模拟数据】清除现有订单数据...");
            orderRepository.deleteAllOrderItems();
            orderRepository.deleteAllOrders();
            logger.info("【模拟数据】成功清除所有订单数据");
        } catch (Exception e) {
            logger.error("【模拟数据】清除订单数据失败: {}", e.getMessage(), e);
        }
        
        // 确保有产品数据
        if (products.isEmpty()) {
            logger.warn("【模拟数据】没有可用产品，无法创建订单");
            return;
        }
        
        List<com.example.warehousemanagement.entity.Order> orders = new ArrayList<>();
        
        // 确保使用20种不同的产品，或所有可用产品
        List<Product> selectedProducts = new ArrayList<>(products);
        if (selectedProducts.size() > 20) {
            Collections.shuffle(selectedProducts);
            selectedProducts = selectedProducts.subList(0, 20);
            logger.info("【模拟数据】从{}个产品中选择了20个产品用于创建订单项", products.size());
        } else {
            logger.info("【模拟数据】使用全部{}个产品创建订单项", products.size());
        }
        
        // 创建订单
        for (int i = 0; i < count; i++) {
            try {
                // 随机选择一个用户
                User user = users.get(ThreadLocalRandom.current().nextInt(users.size()));
                
                // 随机选择一个配送地址
                String deliveryAddress = DELIVERY_ADDRESSES[ThreadLocalRandom.current().nextInt(DELIVERY_ADDRESSES.length)];
                
                com.example.warehousemanagement.entity.Order order = new com.example.warehousemanagement.entity.Order();
                order.setUser(user);
                order.setDeliveryAddress(deliveryAddress);
                
                // 确保设置订单号 - 使用更易读的格式
                String orderNo = "ORD-" + String.format("%06d", i + 1);
                if (orderNo == null || orderNo.isEmpty()) {
                    orderNo = "ORD-" + System.currentTimeMillis() + "-" + i;
                }
                order.setOrderNo(orderNo);
                
                // 随机订单状态 - 按比例分配不同状态
                double rand = ThreadLocalRandom.current().nextDouble();
                
                // 分配状态: 30% 待处理, 25% 处理中, 20% 已发货, 15% 已送达, 10% 已取消
                if (rand < 0.3) {
                    order.setStatus(com.example.warehousemanagement.entity.Order.OrderStatus.PENDING);
                } else if (rand < 0.55) {
                    order.setStatus(com.example.warehousemanagement.entity.Order.OrderStatus.PROCESSING);
                } else if (rand < 0.75) {
                    order.setStatus(com.example.warehousemanagement.entity.Order.OrderStatus.SHIPPED);
                } else if (rand < 0.9) {
                    order.setStatus(com.example.warehousemanagement.entity.Order.OrderStatus.DELIVERED);
                } else {
                    order.setStatus(com.example.warehousemanagement.entity.Order.OrderStatus.CANCELLED);
                }
                
                // 随机添加2-6个订单项
                int itemCount = ThreadLocalRandom.current().nextInt(2, 7);
                List<Product> shuffledProducts = new ArrayList<>(selectedProducts);
                Collections.shuffle(shuffledProducts);
                
                for (int j = 0; j < itemCount && j < shuffledProducts.size(); j++) {
                    Product product = shuffledProducts.get(j);
                    
                    // 创建订单项
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(product);
                    orderItem.setOrder(order);
                    orderItem.setQuantity(ThreadLocalRandom.current().nextInt(1, 11)); // 1-10个
                    
                    // 确保产品价格不为null
                    if (product.getSellingPrice() != null) {
                        orderItem.setUnitPrice(product.getSellingPrice());
                    } else {
                        // 如果产品没有设置售价，使用随机价格
                        orderItem.setUnitPrice(randomBigDecimal(10, 100, 2));
                    }
                    
                    // 随机批次号
                    orderItem.setBatchNo(BATCH_NUMBERS[ThreadLocalRandom.current().nextInt(BATCH_NUMBERS.length)]);
                    
                    // 随机仓库ID
                    List<Warehouse> warehouses = warehouseRepository.findAll();
                    if (!warehouses.isEmpty()) {
                        Warehouse warehouse = warehouses.get(ThreadLocalRandom.current().nextInt(warehouses.size()));
                        orderItem.setWarehouseId(warehouse.getId());
                    }
                    
                    // 添加到订单
                    order.getOrderItems().add(orderItem);
                }
                
                // 计算订单总金额
                order.calculateTotalAmount();
                
                // 设置时间戳 - 创建时间分布在过去90天内，最近的订单更多
                double dayRandom = Math.pow(ThreadLocalRandom.current().nextDouble(), 2); // 使分布偏向较小值
                long dayOffset = (long)(dayRandom * 90);
                long timeOffset = dayOffset * 24 * 60 * 60 * 1000;
                Timestamp createdTime = new Timestamp(System.currentTimeMillis() - timeOffset);
                order.setCreatedAt(createdTime);
                
                // 更新时间 - 在创建时间之后，但天数差异根据订单状态变化
                long updateDelay;
                switch (order.getStatus()) {
                    case PENDING:
                        updateDelay = 0; // 刚创建的订单
                        break;
                    case PROCESSING:
                        updateDelay = ThreadLocalRandom.current().nextLong(1, 3) * 24 * 60 * 60 * 1000; // 1-2天后
                        break;
                    case SHIPPED:
                        updateDelay = ThreadLocalRandom.current().nextLong(3, 6) * 24 * 60 * 60 * 1000; // 3-5天后
                        break;
                    case DELIVERED:
                    case CANCELLED:
                        updateDelay = ThreadLocalRandom.current().nextLong(6, 11) * 24 * 60 * 60 * 1000; // 6-10天后
                        break;
                    default:
                        updateDelay = 0;
                }
                
                // 确保更新时间不超过当前时间
                long updatedTimestamp = createdTime.getTime() + updateDelay;
                if (updatedTimestamp > System.currentTimeMillis()) {
                    updatedTimestamp = System.currentTimeMillis();
                }
                
                Timestamp updatedTime = new Timestamp(updatedTimestamp);
                order.setUpdatedAt(updatedTime);
                
                // 验证订单是否有必填字段
                if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
                    logger.error("【模拟数据】订单号为空，跳过此订单");
                    continue;
                }
                
                if (order.getUser() == null) {
                    logger.error("【模拟数据】订单用户为空，跳过此订单");
                    continue;
                }
                
                if (order.getDeliveryAddress() == null || order.getDeliveryAddress().isEmpty()) {
                    logger.error("【模拟数据】配送地址为空，跳过此订单");
                    continue;
                }
                
                orders.add(order);
            } catch (Exception e) {
                logger.error("【模拟数据】创建订单过程中出错: {}", e.getMessage(), e);
            }
        }
        
        // 逐个保存订单，避免批量保存失败
        int successCount = 0;
        for (com.example.warehousemanagement.entity.Order order : orders) {
            try {
                orderRepository.save(order);
                successCount++;
            } catch (Exception e) {
                logger.error("【模拟数据】保存订单失败，订单号: {}, 错误: {}", order.getOrderNo(), e.getMessage());
            }
        }
        
        logger.info("【模拟数据】成功创建{}个订单，每个订单包含2-6个订单项，使用了约{}种不同产品", 
            successCount, Math.min(selectedProducts.size(), 20));
    }
    
    /**
     * 查找具有特定角色的用户
     */
    private List<User> findUsersByRole(String roleName) {
        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isPresent()) {
            return userRepository.findUsersByRoleName(roleName);
        }
        return new ArrayList<>();
    }
    
    /**
     * 创建具有特定角色的用户
     */
    private List<User> createUsersWithRole(String prefix, int count, String roleName) {
        logger.info("【模拟数据】开始创建{}个{}角色的用户...", count, roleName);
        
        List<User> users = new ArrayList<>();
        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        
        if (roleOpt.isPresent()) {
            Role role = roleOpt.get();
            
            for (int i = 0; i < count; i++) {
                User user = new User();
                user.setUsername(prefix + (i + 1));
                user.setPassword(passwordEncoder.encode("password"));
                user.setEmail(prefix + (i + 1) + "@example.com");
                user.setPhone("1380000" + String.format("%04d", i + 1));
                
                // 添加角色
                user.setRoles(Collections.singletonList(role));
                
                // 设置时间戳
                Timestamp now = new Timestamp(System.currentTimeMillis());
                user.setCreatedAt(now);
                user.setUpdatedAt(now);
                
                users.add(user);
            }
            
            // 保存用户
            userRepository.saveAll(users);
            logger.info("【模拟数据】成功创建{}个{}角色的用户", users.size(), roleName);
        } else {
            logger.error("【模拟数据】未找到角色: {}", roleName);
        }
        
        return users;
    }
    
    /**
     * 生成指定范围内的随机BigDecimal
     */
    private BigDecimal randomBigDecimal(float min, float max, int scale) {
        float random = min + ThreadLocalRandom.current().nextFloat() * (max - min);
        return new BigDecimal(random).setScale(scale, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 生成带时间戳的唯一ID
     */
    private String generateUniqueId(String prefix, int index) {
        return prefix + "-" + System.currentTimeMillis() + "-" + index;
    }

    /**
     * 初始化车辆数据
     */
    private void initVehicles(int count) {
        logger.info("【模拟数据】开始创建{}辆车辆...", count);
        
        // 检查已有车辆数量
        long existingCount = vehicleRepository.count();
        
        if (existingCount >= count) {
            logger.info("【模拟数据】已有{}辆车辆，无需创建", existingCount);
            return;
        }
        
        List<Vehicle> vehicles = new ArrayList<>();
        
        for (int i = 0; i < count - existingCount; i++) {
            Vehicle vehicle = new Vehicle();
            
            // 司机姓名使用拼音
            String driverName = DRIVER_NAMES[ThreadLocalRandom.current().nextInt(DRIVER_NAMES.length)];
            vehicle.setDriverName(driverName);
            
            // 随机容量 (3-30)
            vehicle.setCapacity(ThreadLocalRandom.current().nextInt(3, 31));
            
            // 随机状态，按概率分配: 60% 可用, 30% 使用中, 10% 维修中
            double statusRandom = ThreadLocalRandom.current().nextDouble();
            if (statusRandom < 0.6) {
                vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            } else if (statusRandom < 0.9) {
                vehicle.setStatus(Vehicle.VehicleStatus.IN_USE);
            } else {
                vehicle.setStatus(Vehicle.VehicleStatus.MAINTENANCE);
            }
            
            // 当前位置
            vehicle.setCurrentLocation(LOCATIONS[ThreadLocalRandom.current().nextInt(LOCATIONS.length)]);
            
            // 保险详情
            String insuranceType = INSURANCE_TYPES[ThreadLocalRandom.current().nextInt(INSURANCE_TYPES.length)];
            String expiryDate = LocalDate.now().plusMonths(ThreadLocalRandom.current().nextInt(1, 13)).toString();
            vehicle.setInsuranceDetails(insuranceType + "，到期日期: " + expiryDate);
            
            // 车牌号由PrePersist方法自动生成
            vehicles.add(vehicle);
        }
        
        // 批量保存
        vehicles = vehicleRepository.saveAll(vehicles);
        
        logger.info("【模拟数据】成功创建{}辆车辆", vehicles.size());
    }
} 