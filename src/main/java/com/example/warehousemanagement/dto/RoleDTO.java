package com.example.warehousemanagement.dto;

import com.example.warehousemanagement.entity.Role;
import com.example.warehousemanagement.util.RoleUtils;
import lombok.Data;

@Data
public class RoleDTO {
    private Long id;
    private String name;
    private String type;
    private String responsibility;
    private String displayName;

    public static RoleDTO fromEntity(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setType(role.getType());
        dto.setResponsibility(role.getResponsibility());
        // 使用RoleUtils获取友好的显示名称
        dto.setDisplayName(RoleUtils.getDisplayName(role.getName()));
        return dto;
    }
} 