package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 创建新用户
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // 通过 ID 获取用户
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // 通过用户名获取用户
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 获取所有用户
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 删除用户
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
