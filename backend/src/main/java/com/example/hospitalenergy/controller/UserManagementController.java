package com.example.hospitalenergy.controller;

import com.example.hospitalenergy.dto.MessageResponse;
import com.example.hospitalenergy.entity.User;
import com.example.hospitalenergy.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user-management")
public class UserManagementController {

    @Autowired
    private UserMapper userMapper;

    // 获取所有用户列表
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userMapper.findAllUsers();
        return ResponseEntity.ok(users);
    }

    // 更新用户权限
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long userId, @RequestParam Boolean enabled) {
        // 更新用户权限
        int result = userMapper.updateUserEnabled(userId, enabled);
        if (result > 0) {
            return ResponseEntity.ok(new MessageResponse("用户权限更新成功"));
        } else {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("用户不存在或更新失败"));
        }
    }
} 