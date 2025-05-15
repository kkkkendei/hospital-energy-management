package com.example.hospitalenergy.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;
    private String email; // 可选，用于注册或密码找回
    private boolean enabled = true; // 账户是否启用

    // 在实际项目中，角色通常会是一个单独的表并通过中间表关联
    // 为了简化，这里直接使用 Set<Role> 并假设角色名直接存储或通过某种方式转换
    // 如果使用JPA，可以用@ElementCollection和@Enumerated
    // 如果使用MyBatis，可能需要自定义TypeHandler或在查询时处理
    private Set<Role> roles = new HashSet<>();

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
} 