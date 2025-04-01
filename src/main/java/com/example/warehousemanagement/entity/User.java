package com.example.warehousemanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "`user`")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 20)
    private String phone;

    @OneToMany(mappedBy = "user")
    private List<Order> orders; // 可选：仅为方便查询，非必须  一个用户对应多个订单

    @OneToOne // 必须：角色关联 一个角色对应一种权限职责
    private Role roles;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
