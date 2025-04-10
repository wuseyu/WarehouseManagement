package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 包含用户实体的Optional对象
     */
    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('USER_VIEW')")
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 根据角色名称查找用户
     * @param roleName 角色名称
     * @return 用户列表
     */
    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('USER_VIEW')")
    public List<User> findUsersByRoleName(String roleName) {
        return userRepository.findUsersByRoleName(roleName);
    }

    /**
     * 保存用户
     * @param user 用户实体
     * @return 保存后的用户实体
     */
    @Transactional
    @PreAuthorize("@customSecurityExpression.hasPermission('USER_CREATE')")
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * 根据 ID 查找用户
     * @param id 用户 ID
     * @return 包含用户实体的 Optional 对象
     */
    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('USER_VIEW')")
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 删除用户
     * @param id 用户 ID
     */
    @Transactional
    @PreAuthorize("@customSecurityExpression.hasPermission('USER_DELETE')")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * 获取所有用户
     * @return 用户列表
     */
    @Transactional(readOnly = true)
    @PreAuthorize("@customSecurityExpression.hasPermission('USER_VIEW')")
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}