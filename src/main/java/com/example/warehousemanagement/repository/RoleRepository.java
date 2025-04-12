package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // 根据角色名称查找角色
    Optional<Role> findByName(String name);

    // 检查角色名称是否存在
    boolean existsByName(String name);

    // 检查角色类型是否存在
    boolean existsByType(String type);
}