# 仓库管理系统

基于 Spring Boot 的智能化仓储管理系统

## 技术栈

### 核心框架
- Spring Boot 3.4.0
- Spring Data JPA
- Hibernate 6.4

### 测试框架
- JUnit 5
- Mockito 5.2+
- Spring Test

## 关键特性
- 支持乐观锁的库存管理
- 基于 JPA 审计的实体版本控制
- 三层架构测试覆盖：
  - Repository 层：`@DataJpaTest`
  - Service 层：Mockito 单元测试
  - Controller 层：`@WebMvcTest`

## 构建命令
```bash
mvn clean install -DskipTests
```

## 测试运行
```bash
# 运行全部测试
mvn test

# 运行指定测试类
mvn test -Dtest=InventoryControllerTest

# 运行带乐观锁测试
mvn test -Dtest=InventoryServiceTest#adjustInventoryQuantity_ConcurrentFailure
```
## 系统架构
                          +---------------+
                          |   Controller  |
                          +-------+-------+
                                  | 调用服务方法
                                  v
                          +-------+-------+
                          |    Service    +---------+
                          +-------+-------+         |
                                  | 调用仓库方法       | 审计日志
                                  v                 v
                          +-------+-------+    +-----+-----+
                          |  Repository  |    |   AOP层   |
                          +-------+-------+    +-----------+
                                  | 操作数据库
                                  v
                          +-------+-------+
                          |  MySQL 8.0   |
                          +-------------+