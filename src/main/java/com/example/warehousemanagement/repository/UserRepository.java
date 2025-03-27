package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username); // 额外的查询方法

}
