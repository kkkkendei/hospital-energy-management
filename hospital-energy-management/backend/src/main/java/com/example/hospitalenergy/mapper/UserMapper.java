package com.example.hospitalenergy.mapper;

import com.example.hospitalenergy.entity.User;
import com.example.hospitalenergy.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;
import java.util.Set;

@Mapper
public interface UserMapper {
    Optional<User> findByUsername(@Param("username") String username);
    Boolean existsByUsername(@Param("username") String username);
    Boolean existsByEmail(@Param("email") String email);
    void save(User user); // MyBatis 中通常是 insert

    // 由于User实体中roles是Set<Role>，而数据库中通常角色是字符串或关联表
    // 我们需要方法来保存用户和角色的关联关系
    // 简单起见，我们假设有一个 user_roles 表来存储关联
    // 或者在User表中有个roles字段存储逗号分隔的角色名

    // 插入用户，并返回生成的主键 (需要在User对象中设置id属性)
    void insertUser(User user);

    // 插入用户角色关联
    void insertUserRole(@Param("userId") Long userId, @Param("role") String roleName);

    // 根据用户名查找用户及其角色
    // 这个查询会复杂一些，需要联表或多次查询
    // 为了简化，我们在 User 实体中直接用了 Set<Role>，实际查询可能需要转换
    // 或者我们可以创建一个 UserWithRoles DTO
    User findUserWithRolesByUsername(@Param("username") String username);

    Set<Role> findRolesByUserId(@Param("userId") Long userId);

} 