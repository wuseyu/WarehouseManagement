package com.example.warehousemanagement.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateUserDTO {
    private String username;
    private String password;
    private String email;
    private String phone;
    private List<Long> roleIds;
} 