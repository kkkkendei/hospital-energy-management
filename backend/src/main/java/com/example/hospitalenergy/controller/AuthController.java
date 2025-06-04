package com.example.hospitalenergy.controller;

import com.example.hospitalenergy.dto.LoginRequest;
import com.example.hospitalenergy.dto.MessageResponse;
import com.example.hospitalenergy.dto.SignUpRequest;
import com.example.hospitalenergy.dto.UserLoginResponse;
import com.example.hospitalenergy.entity.Role;
import com.example.hospitalenergy.entity.User;
import com.example.hospitalenergy.mapper.UserMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    UserMapper userMapper;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        logger.info("Attempting to sign in user : {}", loginRequest.getUsername());
        User user = userMapper.findUserWithRolesByUsername(loginRequest.getUsername());

        if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
            logger.info("User {} signed in successfully .", loginRequest.getUsername());
            
            List<String> roleNames = user.getRoles().stream()
                                         .map(Role::name)
                                         .collect(Collectors.toList());

            return ResponseEntity.ok(new UserLoginResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    roleNames,
                    "Login successful !",
                    user.isEnabled()
            ));
        } else {
            logger.warn("Login failed for user : {}. User found: {}, Password match: {}",
                        loginRequest.getUsername(), 
                        user != null, 
                        user != null && user.getPassword().equals(loginRequest.getPassword()));
            return ResponseEntity.status(401).body(new MessageResponse("登录失败: 用户名或密码错误 "));
        }
    }

    @PostMapping("/signup")
    @Transactional
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        logger.info("Attempting to register user  with username: {} and email: {}", signUpRequest.getUsername(), signUpRequest.getEmail());

        if (Boolean.TRUE.equals(userMapper.existsByUsername(signUpRequest.getUsername()))) {
            logger.warn("Registration failed: Username {} already exists.", signUpRequest.getUsername());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("错误: 用户名已存在!"));
        }

        if (Boolean.TRUE.equals(userMapper.existsByEmail(signUpRequest.getEmail()))) {
            logger.warn("Registration failed: Email {} already exists.", signUpRequest.getEmail());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("错误: 邮箱已注册!"));
        }
        
        logger.info("Username and email are unique. Proceeding with user creation ...");

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword());

        logger.info("User object created. Password will be stored as is (UNSAFE!). Roles from request: {}", signUpRequest.getRoles());

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> rolesEnumSet = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            logger.info("No roles specified, defaulting to ROLE_USER.");
            rolesEnumSet.add(Role.ROLE_USER);
        } else {
            strRoles.forEach(roleStr -> {
                logger.info("Processing role string: {}", roleStr);
                try {
                    String roleEnumName = "ROLE_" + roleStr.toUpperCase();
                    if (roleStr.equalsIgnoreCase("admin")) {
                         rolesEnumSet.add(Role.ROLE_ADMIN);
                    } else if (roleStr.equalsIgnoreCase("user")) {
                         rolesEnumSet.add(Role.ROLE_USER);
                    } else {
                        logger.warn("Unknown role string: {}, defaulting to ROLE_USER", roleStr);
                        rolesEnumSet.add(Role.ROLE_USER);
                    }
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid role string: {} provided. Defaulting to ROLE_USER.", roleStr, e);
                    rolesEnumSet.add(Role.ROLE_USER);
                }
            });
        }
        if(rolesEnumSet.isEmpty()) {
            logger.warn("rolesEnumSet is empty after processing strRoles, defaulting to ROLE_USER.");
            rolesEnumSet.add(Role.ROLE_USER);
        }
        user.setRoles(rolesEnumSet);
        logger.info("User roles set to: {}", rolesEnumSet);

        try {
            userMapper.insertUser(user);
            logger.info("insertUser called. User ID after insert (if generated): {}", user.getId());

            Long userId = user.getId();
            if (userId != null) {
                if (!rolesEnumSet.isEmpty()) {
                    logger.info("User ID {} obtained. Inserting roles into user_roles table.", userId);
                    for (Role roleEnum : rolesEnumSet) {
                        logger.info("Inserting role {} for user ID {}.", roleEnum.name(), userId);
                        userMapper.insertUserRole(userId, roleEnum.name());
                    }
                    logger.info("User roles inserted successfully for user ID {}.", userId);
                }
            } else {
                logger.error("Failed to obtain user ID after insertUser. Roles cannot be saved for user: {}", user.getUsername());
                 return ResponseEntity.status(500).body(new MessageResponse("错误: 用户创建失败，无法获取用户ID!"));
            }
        } catch (Exception e) {
            logger.error("Exception during database operation for user {}: {}", signUpRequest.getUsername(), e.getMessage(), e);
            return ResponseEntity.status(500).body(new MessageResponse("错误: 数据库操作失败: " + e.getMessage()));
        }

        logger.info("User {} registered successfully .", user.getUsername());
        return ResponseEntity.ok(new MessageResponse("用户注册成功 !"));
    }
} 