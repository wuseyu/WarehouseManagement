package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Permission;
import com.example.warehousemanagement.entity.Role;
import com.example.warehousemanagement.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    
    // 检查角色和权限的关联是否存在
    boolean existsByRoleAndPermission(Role role, Permission permission);
    
    // 查找角色的所有权限
    List<RolePermission> findByRole(Role role);
    
    // 查找权限关联的所有角色
    List<RolePermission> findByPermission(Permission permission);
    
    // 通过角色名称查找权限
    @Query("SELECT rp FROM RolePermission rp WHERE rp.role.name = :roleName")
    List<RolePermission> findByRoleName(String roleName);
    
    // 通过角色和权限码查找
    @Query("SELECT rp FROM RolePermission rp WHERE rp.role = :role AND rp.permission.code = :permissionCode")
    Optional<RolePermission> findByRoleAndPermissionCode(Role role, String permissionCode);
} 