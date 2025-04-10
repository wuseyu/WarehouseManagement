package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.sql.Timestamp;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 创建用户
    @PostMapping
    @PreAuthorize("@customSecurityExpression.hasPermission('USER_CREATE')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // 根据用户名查找用户
    @GetMapping("/{username}")
    @PreAuthorize("@customSecurityExpression.hasPermission('USER_VIEW')")
    public ResponseEntity<User> findByUsername(@PathVariable String username) {
        Optional<User> userOptional = userService.findByUsername(username);
        return userOptional
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 根据角色名查找用户列表
    @GetMapping("/role/{roleName}")
    @PreAuthorize("@customSecurityExpression.hasPermission('USER_VIEW')")
    public ResponseEntity<List<User>> findUsersByRoleName(@PathVariable String roleName) {
        List<User> users = userService.findUsersByRoleName(roleName);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // 根据 ID 查找用户
    @GetMapping("/id/{id}")
    @PreAuthorize("@customSecurityExpression.hasPermission('USER_VIEW')")
    public ResponseEntity<Optional<User>> findById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 删除用户
    @DeleteMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.hasPermission('USER_DELETE')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 获取所有用户
    @GetMapping
    @PreAuthorize("@customSecurityExpression.hasPermission('USER_VIEW')")
    public ResponseEntity<List<User>> findAllUsers() {
        List<User> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // 更新用户
    @PutMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.hasPermission('USER_UPDATE')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        Optional<User> existingUser = userService.findById(id);
        if (existingUser.isPresent()) {
            User userToUpdate = existingUser.get();
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setPhone(user.getPhone());
            userToUpdate.setRoles(user.getRoles());
            userToUpdate.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            User updatedUser = userService.saveUser(userToUpdate);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 修改用户密码
    @PostMapping("/password/{id}")
    @PreAuthorize("@customSecurityExpression.hasPermission('USER_UPDATE') or @userService.isCurrentUser(#id)")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordData) {
        Optional<User> userOptional = userService.findById(id);
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>("用户不存在", HttpStatus.NOT_FOUND);
        }
        
        User user = userOptional.get();
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return new ResponseEntity<>("旧密码不正确", HttpStatus.BAD_REQUEST);
        }
        
        // 设置新密码并保存
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userService.saveUser(user);
        
        return new ResponseEntity<>("密码修改成功", HttpStatus.OK);
    }
}