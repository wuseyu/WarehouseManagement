package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest  // 只加载 JPA 相关的组件，适用于 Repository 测试
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindUser() {
        // 创建用户
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password123");
        user.setEmail("test@example.com");

        // 保存到数据库
        User savedUser = userRepository.save(user);

        // 通过 ID 查找
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);

        // 验证
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("testUser");
        assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void testFindByUsername() {
        // 创建并保存用户
        User user = new User();
        user.setUsername("findUser");
        user.setPassword("securePass");
        user.setEmail("find@example.com");
        userRepository.save(user);

        // 通过用户名查找
        User foundUser = userRepository.findByUsername("findUser");

        // 验证
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("findUser");
    }

    @Test
    public void testDeleteUser() {
        // 创建并保存用户
        User user = new User();
        user.setUsername("deleteUser");
        user.setPassword("deletePass");
        user.setEmail("delete@example.com");
        user = userRepository.save(user);

        // 删除用户
        userRepository.deleteById(user.getId());

        // 验证用户已删除
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    public void testUpdateUser() {
        // 创建并保存用户
        User user = new User();
        user.setUsername("updateUser");
        user.setPassword("oldPass");
        user.setEmail("update@example.com");
        user = userRepository.save(user);

        // 更新用户信息
        user.setPassword("newPass");
        user.setEmail("new@example.com");
        userRepository.save(user);

        // 重新查询
        User updatedUser = userRepository.findById(user.getId()).orElse(null);

        // 验证
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getPassword()).isEqualTo("newPass");
        assertThat(updatedUser.getEmail()).isEqualTo("new@example.com");
    }

}
