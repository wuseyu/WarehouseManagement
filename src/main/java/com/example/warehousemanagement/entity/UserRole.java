package com.example.warehousemanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
    name = "user_role",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"}),
    indexes = {
        @Index(name = "idx_user_role_region", columnList = "assigned_region"),
        @Index(name = "idx_user_role_warehouse", columnList = "user_id, role_id")
    }
)
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_role_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_role_role"))
    private Role role;

    @Column(name = "assigned_region", length = 50)
    private String assignedRegion; 

    @Column(name = "assigned_time", nullable = false, updatable = false)
    private LocalDateTime assignedTime = LocalDateTime.now(); 

    @Column(name = "assigned_by", nullable = false, length = 50)
    private String assignedBy; 

    public UserRole() {}

    public UserRole(User user, Role role, String assignedBy) {
        this.user = user;
        this.role = role;
        this.assignedBy = assignedBy;
    }
}