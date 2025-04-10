package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Role;
import com.example.warehousemanagement.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    // 获取所有角色
    @GetMapping
    @PreAuthorize("@customSecurityExpression.hasPermission('USER_VIEW')")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    // 根据ID获取角色
    @GetMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.hasPermission('USER_VIEW')")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Optional<Role> role = roleRepository.findById(id);
        return role.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 根据类型获取角色
    @GetMapping("/type/{type}")
    @PreAuthorize("@customSecurityExpression.hasPermission('USER_VIEW')")
    public ResponseEntity<Role> getRoleByType(@PathVariable String type) {
        try {
            Role.RoleType roleType = Role.RoleType.valueOf(type);
            Optional<Role> role = roleRepository.findByType(roleType);
            return role.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
} 