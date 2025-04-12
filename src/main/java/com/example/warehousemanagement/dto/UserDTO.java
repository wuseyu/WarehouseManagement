package com.example.warehousemanagement.dto;

import com.example.warehousemanagement.entity.User;
import lombok.Data;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private List<RoleDTO> roles;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                .map(RoleDTO::fromEntity)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
} 