package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    // 检查权限码是否存在
    boolean existsByCode(String code);
    
    // 通过权限码查找权限
    Optional<Permission> findByCode(String code);
    
    // 通过资源类型和操作类型查找权限
    Optional<Permission> findByResourceTypeAndAction(
        Permission.ResourceType resourceType, 
        Permission.ActionType actionType
    );
} 