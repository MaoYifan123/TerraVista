package com.example.terravista.repository;

import com.example.terravista.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByApiKey(String apiKey);
    boolean existsByEmail(String email); // 添加这个方法
}