package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.dto.UpdatePasswordDTO;
import com.example.warehousemanagement.dto.UpdateUserDTO;
import com.example.warehousemanagement.dto.UserDTO;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.repository.UserRepository;
import com.example.warehousemanagement.security.UserDetailsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.sql.Timestamp;

/**
 * 个人资料控制器
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping
    public ResponseEntity<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsEntity)) {
            logger.warn("【个人资料控制器】用户未登录或未找到用户详情");
            return ResponseEntity.badRequest().build();
        }

        UserDetailsEntity userDetails = (UserDetailsEntity) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(UserDTO.fromEntity(user)))
                .orElseGet(() -> {
                    logger.warn("【个人资料控制器】未找到用户ID: {}", userId);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * 更新当前用户资料
     */
    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody UpdateUserDTO updateUserDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsEntity)) {
            logger.warn("【个人资料控制器】用户未登录或未找到用户详情");
            return ResponseEntity.badRequest().build();
        }

        UserDetailsEntity userDetails = (UserDetailsEntity) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        logger.info("【个人资料控制器】更新用户资料, 用户ID: {}", userId);

        return userRepository.findById(userId)
                .map(user -> {
                    if (updateUserDTO.getEmail() != null) {
                        user.setEmail(updateUserDTO.getEmail());
                    }
                    if (updateUserDTO.getPhone() != null) {
                        user.setPhone(updateUserDTO.getPhone());
                    }

                    // 更新时间
                    user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

                    User updatedUser = userRepository.save(user);
                    logger.info("【个人资料控制器】用户资料更新成功, 用户ID: {}", userId);
                    return ResponseEntity.ok(UserDTO.fromEntity(updatedUser));
                })
                .orElseGet(() -> {
                    logger.warn("【个人资料控制器】未找到用户, ID: {}", userId);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * 修改当前用户密码
     */
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid UpdatePasswordDTO updatePasswordDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsEntity)) {
            logger.warn("【个人资料控制器】用户未登录或未找到用户详情");
            return ResponseEntity.badRequest().build();
        }

        UserDetailsEntity userDetails = (UserDetailsEntity) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        logger.info("【个人资料控制器】修改用户密码, 用户ID: {}", userId);

        return userRepository.findById(userId)
                .map(user -> {
                    // 验证旧密码
                    if (!passwordEncoder.matches(updatePasswordDTO.getOldPassword(), user.getPassword())) {
                        logger.warn("【个人资料控制器】旧密码验证失败, 用户ID: {}", userId);
                        throw new BadCredentialsException("当前密码不正确");
                    }

                    // 验证新密码不能与旧密码相同
                    if (passwordEncoder.matches(updatePasswordDTO.getNewPassword(), user.getPassword())) {
                        logger.warn("【个人资料控制器】新密码与当前密码相同, 用户ID: {}", userId);
                        return ResponseEntity.badRequest().body("新密码不能与当前密码相同");
                    }

                    // 更新密码
                    user.setPassword(passwordEncoder.encode(updatePasswordDTO.getNewPassword()));
                    
                    // 更新时间
                    user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

                    userRepository.save(user);
                    logger.info("【个人资料控制器】用户密码修改成功, 用户ID: {}", userId);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> {
                    logger.warn("【个人资料控制器】未找到用户, ID: {}", userId);
                    return ResponseEntity.notFound().build();
                });
    }
} 