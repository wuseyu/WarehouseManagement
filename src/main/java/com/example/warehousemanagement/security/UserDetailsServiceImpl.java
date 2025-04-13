package com.example.warehousemanagement.security;

import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("【用户加载】正在加载用户: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("【用户加载】用户名 {} 不存在", username);
                    return new UsernameNotFoundException("用户名 " + username + " 不存在");
                });
        
        logger.info("【用户加载】成功加载用户: ID={}, 用户名={}, 角色数={}", 
                   user.getId(), user.getUsername(), user.getRoles().size());
                   
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> 
                logger.info("【用户加载】用户角色: ID={}, 名称={}, 类型={}", 
                           role.getId(), role.getName(), role.getType())
            );
        }
        
        UserDetailsEntity userDetails = new UserDetailsEntity(user);
        logger.info("【用户加载】创建UserDetailsEntity, 权限数={}", userDetails.getAuthorities().size());
        logger.info("【用户加载】用户权限: {}", userDetails.getAuthorities());
        
        return userDetails;
    }
} 