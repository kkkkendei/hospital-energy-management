package com.example.hospitalenergy.dto;

import lombok.Data;
import java.util.Set;

@Data
public class SignUpRequest {
    private String username;
    private String email;
    private String password;
    private Set<String> roles; // 注册时可以指定角色，例如 ["user"] 或 ["admin", "user"]
} 